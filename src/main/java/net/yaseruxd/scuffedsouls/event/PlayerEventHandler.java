package net.yaseruxd.scuffedsouls.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.network.OpenClassScreenPacket;
import net.yaseruxd.scuffedsouls.playerclass.ClassManager;
import net.yaseruxd.scuffedsouls.registry.ModBlocks;

@EventBusSubscriber(
        modid = "scuffedsouls",
        bus = Bus.FORGE
)
public class PlayerEventHandler {

    private static final float DURABILITY_LOSS_ON_DEATH = 0.1F;

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

        // Server-side only
        if (!level.isClientSide()) {

            // Lose 10% durability from every damageable item.
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack stack = player.getInventory().getItem(i);

                if (!stack.isEmpty() && stack.isDamageableItem()) {
                    int maxDurability = stack.getMaxDamage();
                    int damageToApply =
                            (int) ((float) maxDurability * DURABILITY_LOSS_ON_DEATH);

                    int newDamage = Math.min(
                            stack.getDamageValue() + damageToApply,
                            maxDurability - 1
                    );

                    stack.setDamageValue(newDamage);
                }
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
                ) {
                    // Search downward for solid ground.
                }

                placePos = placePos.above();

                SoulAnchorData.storeXp(
                        level,
                        placePos,
                        totalXp,
                        player.getUUID()
                );

                level.setBlock(
                        placePos,
                        ModBlocks.SOUL_ANCHOR.get().defaultBlockState(),
                        3
                );

                // Remove XP from the player.
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
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        UUID uuid = player.getUUID();

        if (!pendingJoinSync.contains(uuid)) {
            return;
        }

        int ticks = joinTickCounter.merge(
                uuid,
                1,
                Integer::sum
        );

        if (player.connection != null
                && player.isAddedToWorld()
                && ticks >= 20) {

            pendingJoinSync.remove(uuid);
            joinTickCounter.remove(uuid);

            /*
             * IMPORTANT:
             * getAll() returns Collection<BuildupDefinition>.
             * BuildupDefinitionsSyncPacket expects
             * Map<ResourceLocation, BuildupDefinition>.
             *
             * Therefore we use getAllByEffect().
             */
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new BuildupDefinitionsSyncPacket(
                            BuildupDefinitions.getAllByEffect()
                    )
            );

            // Give the player 1 XP using the existing command.
            player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(),
                    "esr_addexp "
                            + player.getName().getString()
                            + " 1"
            );

            // Open class selection screen if player doesn't have a class.
            if (!ClassManager.hasClass(player)) {

                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.BLINDNESS,
                                6000,
                                4,
                                false,
                                false
                        )
                );

                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new OpenClassScreenPacket()
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData =
                event.getOriginal().getPersistentData();

        CompoundTag newData =
                event.getEntity().getPersistentData();

        if (oldData.contains("scuffedsouls_class")) {
            newData.putString(
                    "scuffedsouls_class",
                    oldData.getString("scuffedsouls_class")
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()) {

            BuildupData data = BuildupStorage.get(player);

            data.resetAll();

            BuildupStorage.save(player, data);

            if (player instanceof ServerPlayer serverPlayer) {

                /*
                 * IMPORTANT:
                 * getAll() returns a Collection and therefore
                 * cannot use keySet().
                 *
                 * getAllByEffect() returns the Map we need.
                 */
                for (
                        ResourceLocation buildupId :
                        BuildupDefinitions.getAllByEffect().keySet()
                ) {

                    ModNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(
                                    () -> serverPlayer
                            ),
                            new BuildupSyncPacket(
                                    buildupId,
                                    0.0F
                            )
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }

        MinecraftServer server =
                ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return;
        }

        /*
         * 6000 ticks = 5 minutes.
         */
        if (server.getTickCount() % 6000 == 0) {

            for (ServerLevel level : server.getAllLevels()) {

                SoulAnchorData data =
                        SoulAnchorData.get(level);

                /*
                 * Copy the key set because expired entries
                 * may be removed while processing them.
                 */
                for (
                        Long posKey :
                        new ArrayList<>(data.xpMap.keySet())
                ) {

                    BlockPos pos =
                            BlockPos.of(posKey);

                    if (SoulAnchorData.isExpired(level, pos)) {

                        level.destroyBlock(
                                pos,
                                false
                        );

                        SoulAnchorData.clearXp(
                                level,
                                pos
                        );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(
            PlayerEvent.PlayerChangedDimensionEvent event
    ) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ResourceLocation destination =
                event.getTo().location();

        ResourceLocation paradise =
                new ResourceLocation(
                        "the_faint_radiance",
                        "paradise"
                );

        /*
         * Entering Paradise:
         * Give infinite-duration invisibility.
         */
        if (destination.equals(paradise)) {

            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.INVISIBILITY,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false
                    )
            );
        }

        ResourceLocation source =
                event.getFrom().location();

        /*
         * Leaving Paradise:
         * Remove invisibility.
         */
        if (source.equals(paradise)) {

            player.removeEffect(
                    MobEffects.INVISIBILITY
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEffectCleanup(
            TickEvent.PlayerTickEvent event
    ) {

        if (event.phase != Phase.START) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        /*
         * Remove effects whose duration has reached zero.
         */
        for (
                MobEffectInstance effect :
                new ArrayList<>(player.getActiveEffects())
        ) {

            if (effect.getDuration() <= 0) {
                player.removeEffect(
                        effect.getEffect()
                );
            }
        }
    }
}
