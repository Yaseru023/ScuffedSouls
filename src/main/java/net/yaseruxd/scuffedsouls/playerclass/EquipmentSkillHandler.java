package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentSkillHandler {

    private static final int TICK_INTERVAL = 20;
    private static final String SHIELD_COUNTER_SKILL = "combat_evolution:shield_counter";
    private static final String REVELATION_SKILL = "epicfight:revelation";

    private static final Set<UUID> grantedShieldCounter = new HashSet<>();
    private static final Set<UUID> needsRevalidation = new HashSet<>();
    private static final Map<UUID, ItemStack> pendingRestore = new HashMap<>();
    private static final Map<UUID, Integer> pendingRestoreSlot = new HashMap<>();
    private static final Map<UUID, Integer> restoreCountdown = new HashMap<>();
    private static final Map<UUID, Integer> joinDelay = new HashMap<>(); // ← new

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            grantedShieldCounter.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            needsRevalidation.add(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            needsRevalidation.add(player.getUUID());
            joinDelay.put(player.getUUID(), 200); // 10 second delay on join
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            grantedShieldCounter.remove(uuid);
            needsRevalidation.remove(uuid);
            joinDelay.remove(uuid);
            pendingRestore.remove(uuid);
            pendingRestoreSlot.remove(uuid);
            restoreCountdown.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            needsRevalidation.add(player.getUUID());
        }
    }

    private static void triggerSwordSwap(ServerPlayer player) {
        UUID uuid = player.getUUID();
        int originalSlot = player.getInventory().selected;
        ItemStack currentMainhand = player.getMainHandItem().copy();

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));

        pendingRestore.put(uuid, currentMainhand);
        pendingRestoreSlot.put(uuid, originalSlot);
        restoreCountdown.put(uuid, 2);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        // Process pending sword-swap restores every tick
        if (!restoreCountdown.isEmpty()) {
            for (UUID uuid : new HashSet<>(restoreCountdown.keySet())) {
                int remaining = restoreCountdown.get(uuid) - 1;
                if (remaining <= 0) {
                    ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                    ItemStack original = pendingRestore.remove(uuid);
                    Integer slot = pendingRestoreSlot.remove(uuid);
                    restoreCountdown.remove(uuid);
                    if (player != null && original != null && slot != null) {
                        player.getInventory().items.set(slot, original);
                    }
                } else {
                    restoreCountdown.put(uuid, remaining);
                }
            }
        }

        // Tick down join delays every tick
        if (!joinDelay.isEmpty()) {
            for (UUID uuid : new HashSet<>(joinDelay.keySet())) {
                int remaining = joinDelay.get(uuid) - 1;
                if (remaining <= 0) {
                    joinDelay.remove(uuid);
                } else {
                    joinDelay.put(uuid, remaining);
                }
            }
        }

        // Revalidation check every tick
        if (!needsRevalidation.isEmpty()) {
            for (UUID uuid : new HashSet<>(needsRevalidation)) {
                if (joinDelay.containsKey(uuid)) continue; // still waiting

                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player == null) continue;

                ItemStack mainhand = player.getMainHandItem();
                ItemStack offhand = player.getOffhandItem();

                if (!mainhand.isEmpty() && isShield(offhand)) {
                    triggerSwordSwap(player);
                    needsRevalidation.remove(uuid);
                }
            }
        }

        if (server.getTickCount() % TICK_INTERVAL != 0) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();

            // Skip players still in join delay
            if (joinDelay.containsKey(uuid)) continue;

            ItemStack offhand = player.getOffhandItem();
            boolean hasShield = isShield(offhand);

            if (hasShield && !grantedShieldCounter.contains(uuid)) {
                revokeIdentitySkill(server, player);
                grantIdentitySkill(server, player, SHIELD_COUNTER_SKILL);
                grantedShieldCounter.add(uuid);
            } else if (!hasShield && grantedShieldCounter.contains(uuid)) {
                revokeIdentitySkill(server, player);
                grantIdentitySkill(server, player, REVELATION_SKILL);
                grantedShieldCounter.remove(uuid);
            } else if (!hasShield && !grantedShieldCounter.contains(uuid)) {
                grantIdentitySkill(server, player, REVELATION_SKILL);
            }
        }
    }

    private static boolean isShield(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getUseAnimation() == UseAnim.BLOCK;
    }

    private static void grantIdentitySkill(MinecraftServer server, ServerPlayer player, String skill) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            String cmd = String.format("epicfight skill add %s identity %s",
                    player.getName().getString(), skill);
            server.getCommands().performPrefixedCommand(source, cmd);
        } catch (Exception e) {
            // Epic Fight not ready yet — will retry next tick cycle
        }
    }

    private static void revokeIdentitySkill(MinecraftServer server, ServerPlayer player) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            String cmd = String.format("epicfight skill remove %s identity",
                    player.getName().getString());
            server.getCommands().performPrefixedCommand(source, cmd);
        } catch (Exception e) {
            // Epic Fight not ready yet
        }
    }
}