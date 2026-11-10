package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "scuffedsouls")
public class WeaponScalingEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new WeaponScalingLoader());
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        // Direct entity check keeps arrows, thorns, etc. from using the held weapon's scaling.
        if (!(event.getSource().getDirectEntity() instanceof Player player)) return;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem());
        if (id == null || !WeaponScalingManager.has(id)) return;

        double bonus = WeaponScalingManager.getBonus(player, id);
        if (bonus > 0) {
            event.setAmount(event.getAmount() + (float) bonus);
        }
    }
}