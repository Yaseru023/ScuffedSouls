package net.yaseruxd.scuffedsouls.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemRestrictionHandler {

    private static final ResourceLocation SHAPE_OF_SIN =
            new ResourceLocation("the_faint_radiance", "shapeofsin");

    private static final int SPAWN_X = 0;
    private static final int SPAWN_Z = 0;
    private static final int MIN_DISTANCE = 500;

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return;

        // Only in overworld
        ResourceLocation dimension = player.level().dimension().location();
        if (!dimension.equals(new ResourceLocation("minecraft", "overworld"))) return;

        ItemStack stack = event.getItemStack();
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(stack.getItem());

        if (!SHAPE_OF_SIN.equals(itemId)) return;

        double dx = player.getX() - SPAWN_X;
        double dz = player.getZ() - SPAWN_Z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < MIN_DISTANCE) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal(
                    "§7The radiance feels dim here. Venture further from the spawn before summoning."
            ));
        }
    }
}
