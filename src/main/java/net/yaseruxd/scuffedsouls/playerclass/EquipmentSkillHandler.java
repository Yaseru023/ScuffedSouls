package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentSkillHandler {

    private static final int TICK_INTERVAL = 20;

    // Identity skills
    private static final String SHIELD_COUNTER_SKILL = "combat_evolution:shield_counter";
    private static final String REVELATION_SKILL = "epicfight:revelation";

    // Dodge skills
    private static final String DODGE_LIGHT = "wom:precise_roll";
    private static final String DODGE_MEDIUM = "epicfightx:step";
    private static final String DODGE_HEAVY = "wom:punishment_kick";
    private static final String DODGE_YAMATO = "cdmoveset:yamato_step";

    // Guard skills
    private static final String GUARD_DEFAULT = "epicfight:parrying";
    private static final String GUARD_PERFECT_BULWARK = "wom:perfect_bulwark";

    // Weapon IDs
    private static final ResourceLocation HERRSCHER = new ResourceLocation("wom:herrscher");
    private static final ResourceLocation GESETZ = new ResourceLocation("wom:gesetz");
    private static final ResourceLocation YAMATO = new ResourceLocation("cdmoveset:yamato");

    // Weight attribute
    private static final ResourceLocation WEIGHT_ATTR = new ResourceLocation("epicfight:weight");

    private static final Set<UUID> grantedShieldCounter = new HashSet<>();
    private static final Map<UUID, Integer> joinDelay = new HashMap<>();
    private static final Map<UUID, String> lastDodge = new HashMap<>();
    private static final Map<UUID, String> lastGuard = new HashMap<>();

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            grantedShieldCounter.remove(player.getUUID());
        }
    }
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            joinDelay.put(uuid, 400); // increase to 20 seconds
            // Clear cached skills so they get reapplied fresh after delay
            lastDodge.remove(uuid);
            lastGuard.remove(uuid);
            grantedShieldCounter.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            joinDelay.put(uuid, 400);
            lastDodge.remove(uuid);
            lastGuard.remove(uuid);
            grantedShieldCounter.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            joinDelay.put(uuid, 200); // shorter for dimension change
            lastDodge.remove(uuid);
            lastGuard.remove(uuid);
        }
    }
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            grantedShieldCounter.remove(uuid);
            joinDelay.remove(uuid);
            lastDodge.remove(uuid);
            lastGuard.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        // Tick down join delays
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

        if (server.getTickCount() % TICK_INTERVAL != 0) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            if (joinDelay.containsKey(uuid)) continue;

            ItemStack mainhand = player.getMainHandItem();
            ItemStack offhand = player.getOffhandItem();

            ResourceLocation mainhandId = ForgeRegistries.ITEMS.getKey(mainhand.getItem());
            ResourceLocation offhandId = ForgeRegistries.ITEMS.getKey(offhand.getItem());

            boolean hasShield = isShield(offhand);
            boolean hasSwordMainhand = isSwordOrLongsword(mainhandId);

            // --- Identity skill ---
            if (hasShield && hasSwordMainhand && !grantedShieldCounter.contains(uuid)) {
                revokeIdentitySkill(server, player);
                grantIdentitySkill(server, player, SHIELD_COUNTER_SKILL);
                grantedShieldCounter.add(uuid);
            } else if ((!hasShield || !hasSwordMainhand) && grantedShieldCounter.contains(uuid)) {
                revokeIdentitySkill(server, player);
                grantIdentitySkill(server, player, REVELATION_SKILL);
                grantedShieldCounter.remove(uuid);
            } else if (!hasShield && !grantedShieldCounter.contains(uuid)) {
                grantIdentitySkill(server, player, REVELATION_SKILL);
            }

            // --- Herrscher + Gesetz combo ---
            boolean isHerrscher = HERRSCHER.equals(mainhandId);
            boolean isGesetz = GESETZ.equals(offhandId);

            String targetGuard = (isHerrscher && isGesetz) ? GUARD_PERFECT_BULWARK : GUARD_DEFAULT;

            if (isHerrscher && isGesetz) {
                grantIdentitySkill(server, player, REVELATION_SKILL);
            }

            if (!targetGuard.equals(lastGuard.get(uuid))) {
                revokeGuardSkill(server, player);
                grantGuardSkill(server, player, targetGuard);
            }

            // --- Dodge skill based on weight or weapon ---
            String targetDodge;
            boolean isYamato = YAMATO.equals(mainhandId);
            if (isYamato) {
                targetDodge = DODGE_YAMATO;
            } else {
                double weight = getWeight(player);
                if (weight <= 89) {
                    targetDodge = DODGE_LIGHT;
                } else if (weight <= 100) {
                    targetDodge = DODGE_MEDIUM;
                } else {
                    targetDodge = DODGE_HEAVY;
                }
            }

            if (!targetDodge.equals(lastDodge.get(uuid))) {
                grantDodgeSkill(server, player, targetDodge);
                lastDodge.put(uuid, targetDodge);
            }
        }
    }

    private static boolean isSwordOrLongsword(ResourceLocation itemId) {
        if (itemId == null) return false;
        String path = itemId.getPath();
        return path.endsWith("_sword") || path.endsWith("_longsword");
    }

    private static double getWeight(ServerPlayer player) {
        Attribute weightAttr = ForgeRegistries.ATTRIBUTES.getValue(WEIGHT_ATTR);
        if (weightAttr == null) return 0;
        var instance = player.getAttribute(weightAttr);
        if (instance == null) return 0;
        return Math.round(instance.getValue());
    }

    private static boolean isShield(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getUseAnimation() == UseAnim.BLOCK;
    }

    private static void grantIdentitySkill(MinecraftServer server, ServerPlayer player, String skill) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source,
                    String.format("epicfight skill add %s identity %s",
                            player.getName().getString(), skill));
        } catch (Exception e) {}
    }

    private static void revokeIdentitySkill(MinecraftServer server, ServerPlayer player) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source,
                    String.format("epicfight skill remove %s identity",
                            player.getName().getString()));
        } catch (Exception e) {}
    }

    private static void grantDodgeSkill(MinecraftServer server, ServerPlayer player, String skill) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source,
                    String.format("epicfight skill add %s dodge %s",
                            player.getName().getString(), skill));
        } catch (Exception e) {}
    }

    private static void grantGuardSkill(MinecraftServer server, ServerPlayer player, String skill) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source,
                    String.format("epicfight skill add %s guard %s",
                            player.getName().getString(), skill));
            lastGuard.put(player.getUUID(), skill);
        } catch (Exception e) {}
    }

    private static void revokeGuardSkill(MinecraftServer server, ServerPlayer player) {
        try {
            var source = server.createCommandSourceStack().withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source,
                    String.format("epicfight skill remove %s guard",
                            player.getName().getString()));
            lastGuard.remove(player.getUUID());
        } catch (Exception e) {}
    }
}