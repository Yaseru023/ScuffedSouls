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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import net.yaseruxd.scuffedsouls.hollow.HollowData;
import net.yaseruxd.scuffedsouls.hollow.HollowManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import net.yaseruxd.scuffedsouls.playerclass.ClassManager;
import net.yaseruxd.scuffedsouls.network.OpenClassScreenPacket;

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

            if (!ClassManager.hasClass(player)) {
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new OpenClassScreenPacket()
                );
            }

            // Reapply hollow modifiers from stored NBT level
            HollowManager.removeModifiers(player);
            HollowManager.applyModifiers(player);
            syncHollowToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        Player clone = event.getEntity();

        original.reviveCaps();

        // Preserve class selection
        clone.getPersistentData().putString(
                "scuffedsouls_class",
                original.getPersistentData().getString("scuffedsouls_class")
        );

        // Preserve Hollow data
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
    public static void onHumanoidKilled(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();

        // Only process humanoid mobs
        if (!isHumanoid(killed)) return;

        // Only process player kills
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Player must currently have Hollow
        int hollowLevel = HollowData.getLevel(player);
        if (hollowLevel <= 0) return;

        // Decrease Hollow
        HollowData.decrement(player);

// Play Soul Speed's soul escape sound
        player.playNotifySound(
                SoundEvents.SOUL_ESCAPE,
                SoundSource.PLAYERS,
                1.8F,
                0.85F
        );

        // Update player attributes
        HollowManager.removeModifiers(player);
        HollowManager.applyModifiers(player);

        // Update HUD
        syncHollowToClient(player);

        // Release souls from the killed mob
        summonSoulParticles(killed, player);
    }

    private static void summonSoulParticles(
            LivingEntity killed,
            ServerPlayer player
    ) {
        if (!(killed.level() instanceof ServerLevel level)) return;

        Vec3 target = player.position().add(0, 1.0, 0);

        // Create a burst of souls around the dead mob
        for (int i = 0; i < 16; i++) {

            double x = killed.getX()
                    + (killed.getRandom().nextDouble() - 0.5) * 1.5;

            double y = killed.getY()
                    + killed.getRandom().nextDouble() * killed.getBbHeight();

            double z = killed.getZ()
                    + (killed.getRandom().nextDouble() - 0.5) * 1.5;

            Vec3 particlePosition = new Vec3(x, y, z);

            // Direction toward the player
            Vec3 direction = target.subtract(particlePosition).normalize();

            // Spawn one particle with directed velocity
            level.sendParticles(
                    ParticleTypes.SOUL,
                    x, y, z,
                    0,
                    direction.x * 0.25,
                    direction.y * 0.25,
                    direction.z * 0.25,
                    1.0
            );
        }
    }

    private static void syncHollowToClient(ServerPlayer player) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new HollowSyncPacket(HollowData.getLevel(player))
        );
    }

    private static final TagKey<EntityType<?>> HUMANOID =
            TagKey.create(
                    Registries.ENTITY_TYPE,
                    new ResourceLocation("scuffedsouls", "humanoid")
            );

    private static boolean isHumanoid(LivingEntity entity) {
        return entity.getType().is(HUMANOID);
    }
}