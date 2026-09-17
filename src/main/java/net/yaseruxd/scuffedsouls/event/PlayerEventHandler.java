package net.yaseruxd.scuffedsouls.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.yaseruxd.scuffedsouls.block.SoulAnchorData;
import net.yaseruxd.scuffedsouls.buildup.BuildupData;
import net.yaseruxd.scuffedsouls.buildup.BuildupDefinitions;
import net.yaseruxd.scuffedsouls.buildup.BuildupStorage;
import net.yaseruxd.scuffedsouls.network.BuildupDefinitionsSyncPacket;
import net.yaseruxd.scuffedsouls.network.BuildupSyncPacket;
import net.yaseruxd.scuffedsouls.network.HollowSyncPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.registry.ModBlocks;

import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.yaseruxd.scuffedsouls.hollow.HollowData;
import net.yaseruxd.scuffedsouls.hollow.HollowManager;

@EventBusSubscriber(
        modid = "scuffedsouls",
        bus = Bus.FORGE
)
public class PlayerEventHandler {


    private static final Set<UUID> pendingJoinSync = new HashSet<>();
    private static final Map<UUID, Integer> joinTickCounter = new HashMap<>();

    public PlayerEventHandler() {
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        Level level = player.level();

        if (!level.isClientSide()) {

            // Hollow increment — always happens on death, regardless of XP
            HollowData.increment(player);
            if (player instanceof ServerPlayer serverPlayer) {
                syncHollowToClient(serverPlayer);
            }

            // Store player's XP in a Soul Anchor.
            int totalXp = player.totalExperience;

            if (totalXp > 0) {
                BlockPos deathPos = player.blockPosition();
                BlockPos placePos;

                for (
                        placePos = deathPos;
                        placePos.getY() > level.getMinBuildHeight()
                                && level.getBlockState(placePos).isAir();
                        placePos = placePos.below()
                ) {}

                placePos = placePos.above();

                SoulAnchorData.storeXp(level, placePos, totalXp, player.getUUID());
                level.setBlock(placePos, ModBlocks.SOUL_ANCHOR.get().defaultBlockState(), 3);

                player.totalExperience = 0;
                player.experienceLevel = 0;
                player.experienceProgress = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player entity = event.getEntity();

        if (entity instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            pendingJoinSync.add(uuid);
            joinTickCounter.put(uuid, 0);

            // Reapply hollow modifiers from stored NBT level
            HollowManager.removeModifiers(player);
            HollowManager.applyModifiers(player);
            syncHollowToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return; // only on death respawn, not end portal

        Player original = event.getOriginal();
        Player clone = event.getEntity();

        // Required to access the original player's persistent data
        original.reviveCaps();

        int hollowLevel = HollowData.getLevel(original);
        HollowData.setLevel(clone, hollowLevel);

        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        if (!pendingJoinSync.contains(uuid)) return;

        int ticks = joinTickCounter.merge(uuid, 1, Integer::sum);

        if (player.connection != null && player.isAddedToWorld() && ticks >= 20) {

            pendingJoinSync.remove(uuid);
            joinTickCounter.remove(uuid);

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new BuildupDefinitionsSyncPacket(BuildupDefinitions.getAllByEffect())
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()) {
            // Existing buildup reset
            BuildupData data = BuildupStorage.get(player);
            data.resetAll();
            BuildupStorage.save(player, data);

            if (player instanceof ServerPlayer serverPlayer) {
                for (ResourceLocation buildupId : BuildupDefinitions.getAllByEffect().keySet()) {
                    ModNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new BuildupSyncPacket(buildupId, 0.0F)
                    );
                }
            }

            // Apply hollow modifiers based on stored level
            HollowManager.removeModifiers(player);
            HollowManager.applyModifiers(player);
            if (player instanceof ServerPlayer serverPlayer) {
                syncHollowToClient(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        if (server.getTickCount() % 6000 == 0) {
            for (ServerLevel level : server.getAllLevels()) {
                SoulAnchorData data = SoulAnchorData.get(level);
                for (Long posKey : new ArrayList<>(data.xpMap.keySet())) {
                    BlockPos pos = BlockPos.of(posKey);
                    if (SoulAnchorData.isExpired(level, pos)) {
                        level.destroyBlock(pos, false);
                        SoulAnchorData.clearXp(level, pos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation destination = event.getTo().location();
        ResourceLocation paradise = new ResourceLocation("the_faint_radiance", "paradise");

        if (destination.equals(paradise)) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        }

        ResourceLocation source = event.getFrom().location();
        if (source.equals(paradise)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @SubscribeEvent
    public static void onItemFinishedUsing(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getItem().getItem() != Items.ENCHANTED_GOLDEN_APPLE) return;
        if (player.level().isClientSide()) return;

        int currentLevel = HollowData.getLevel(player);
        if (currentLevel <= 0) return;

        HollowData.decrement(player);
        HollowManager.removeModifiers(player);
        HollowManager.applyModifiers(player);
        syncHollowToClient(player);
    }

    private static void syncHollowToClient(ServerPlayer player) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new HollowSyncPacket(HollowData.getLevel(player))
        );
    }
}