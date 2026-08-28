package net.yaseruxd.scuffedsouls.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
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

    private static final float STAMINA_DRAIN_RATIO = 0.5f;

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean mainhandShield = player.getMainHandItem().getItem() instanceof ShieldItem;
        boolean offhandShield = player.getOffhandItem().getItem() instanceof ShieldItem;
        if (!mainhandShield && !offhandShield) return;

        float blockedDamage = event.getBlockedDamage();
        float staminaCost = blockedDamage * STAMINA_DRAIN_RATIO;

        if (staminaCost <= 0) return;

        net.minecraft.server.MinecraftServer server =
                net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        net.minecraft.commands.CommandSourceStack source =
                server.createCommandSourceStack().withSuppressedOutput();

        String cmd = String.format("epicfight stamina subtract %s %s",
                player.getName().getString(),
                (int) Math.ceil(staminaCost));

        server.getCommands().performPrefixedCommand(source, cmd);
    }

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
