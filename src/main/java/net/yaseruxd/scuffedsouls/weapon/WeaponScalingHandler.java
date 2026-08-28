package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponScalingHandler {

    // Fires when any equipment slot changes
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // Just reapply based on current mainhand, manager handles removal internally
        WeaponScalingManager.applyScalingModifier(player.getMainHandItem(), player);
    }

    // Reapply when player respawns or changes dimension
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        reapplyAll(player);
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        reapplyAll(player);
    }

    private static void reapplyAll(Player player) {
        // Reapply on mainhand and offhand
        WeaponScalingManager.applyScalingModifier(
                player.getMainHandItem(), player);
        WeaponScalingManager.applyScalingModifier(
                player.getOffhandItem(), player);
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (event.player.tickCount % 20 != 0) return;

        // Temporary debug
        System.out.println("[ScuffedSouls] Tick firing for: " + event.player.getName().getString());

        WeaponScalingManager.applyScalingModifier(
                event.player.getMainHandItem(), event.player);
        WeaponScalingManager.applyScalingModifier(
                event.player.getOffhandItem(), event.player);
    }

}