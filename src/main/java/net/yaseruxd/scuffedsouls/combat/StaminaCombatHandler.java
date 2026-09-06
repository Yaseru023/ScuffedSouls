package net.yaseruxd.scuffedsouls.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaCombatHandler {

    private static final float DRAIN_MULTIPLIER = 0.2f;

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(patch -> {
            if (!(patch instanceof PlayerPatch<?> playerPatch)) return;
            if (playerPatch.getMaxStamina() <= 0) return;

            if (playerPatch.getStamina() <= 0) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer attacker)) return;

        attacker.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(patch -> {
            if (!(patch instanceof PlayerPatch<?> playerPatch)) return;
            if (playerPatch.getMaxStamina() <= 0) return;

            // Block the attack if stamina is empty (safety net for EpicFight routed attacks)
            if (playerPatch.getStamina() <= 0) {
                event.setCanceled(true);
                return;
            }

            float drain = playerPatch.getModifiedStaminaConsume(event.getAmount() * DRAIN_MULTIPLIER);
            playerPatch.setStamina(playerPatch.getStamina() - drain);
            playerPatch.resetActionTick();
        });
    }
}