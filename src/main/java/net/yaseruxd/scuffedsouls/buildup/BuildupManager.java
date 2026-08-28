package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.yaseruxd.scuffedsouls.network.BuildupSyncPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BuildupManager {

    private static final Set<UUID> JUST_APPLIED = new HashSet<>();

    public static boolean isApplying(UUID playerUUID) {
        return JUST_APPLIED.contains(playerUUID);
    }

    public static void handleIncomingEffect(Player player, ResourceLocation effectId, BuildupDefinition def) {
        BuildupData data = BuildupStorage.get(player);
        float current = data.get(effectId);
        float newAmount = current + def.getBuildupPerApplication();

        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);

        if (newAmount >= def.getMaxBuildup()) {
            data.reset(effectId);
            BuildupStorage.save(player, data);
            sync(player, effectId, 0.0F);
            applyEffect(player, effect, def);
        } else {
            data.set(effectId, newAmount);
            BuildupStorage.save(player, data);
            sync(player, effectId, newAmount);
        }
    }

    public static void applyEffect(Player player, MobEffect effect, BuildupDefinition def) {
        if (effect == null) return;

        JUST_APPLIED.add(player.getUUID());
        try {
            player.addEffect(new MobEffectInstance(
                    effect,
                    def.getDuration(),
                    def.getAmplifier(),
                    false,
                    true
            ));
        } finally {
            // Clear on next server tick to catch any delayed re-fires from other mods
            if (player.getServer() != null) {
                player.getServer().execute(() -> JUST_APPLIED.remove(player.getUUID()));
            } else {
                JUST_APPLIED.remove(player.getUUID());
            }
        }
    }

    public static void decayAndSync(Player player) {
        BuildupData data = BuildupStorage.get(player);
        boolean changed = false;

        for (Map.Entry<ResourceLocation, BuildupDefinition> entry :
                BuildupDefinitions.getAllByEffect().entrySet()) {
            ResourceLocation effectId = entry.getKey();
            BuildupDefinition def = entry.getValue();

            float current = data.get(effectId);
            if (current <= 0.0F) continue;

            float newValue = Math.max(0.0F, current - def.getDecayPerTick());
            if (newValue != current) {
                data.set(effectId, newValue);
                changed = true;
            }
        }

        if (changed) {
            BuildupStorage.save(player, data);
            for (ResourceLocation effectId : BuildupDefinitions.getAllByEffect().keySet()) {
                sync(player, effectId, data.get(effectId));
            }
        }
    }

    private static void sync(Player player, ResourceLocation effectId, float amount) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new BuildupSyncPacket(effectId, amount)
            );
        }
    }
}