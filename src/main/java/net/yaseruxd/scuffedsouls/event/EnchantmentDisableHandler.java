package net.yaseruxd.scuffedsouls.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentDisableHandler {

    private static final int TICK_INTERVAL = 20;

    // Block enchanting table from offering any enchantments
    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        event.setEnchantLevel(0);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if (server.getTickCount() % TICK_INTERVAL != 0) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            var inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                stripAllEnchantments(inventory.getItem(i));
                convertEnchantedBook(inventory, i);
            }
        }
    }

    private static void stripAllEnchantments(ItemStack stack) {
        if (stack.isEmpty()) return;
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        tag.remove("Enchantments");
        tag.remove("StoredEnchantments");

        if (tag.isEmpty()) stack.setTag(null);
    }

    private static void convertEnchantedBook(net.minecraft.world.entity.player.Inventory inventory, int slot) {
        ItemStack stack = inventory.getItem(slot);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof EnchantedBookItem)) return;

        // All enchanted books become normal books
        inventory.setItem(slot, new ItemStack(Items.BOOK, stack.getCount()));
    }
}