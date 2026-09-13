package net.yaseruxd.scuffedsouls.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.yaseruxd.scuffedsouls.weapon.WeaponRequirementManager;


@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEvents {

    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack mainhand = player.getMainHandItem();
        if (mainhand.isEmpty()) return;

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(mainhand.getItem());
        if (itemId == null) return;

        if (!WeaponRequirementManager.hasRequirements(itemId)) return;

        if (!WeaponRequirementManager.meetsRequirements(player, itemId)) {
            event.setCanceled(true);

            player.displayClientMessage(
                    Component.literal("§c✖ You lack the stats to wield this weapon."),
                    true
            );
        }
    }
}
