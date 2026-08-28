package net.yaseruxd.scuffedsouls.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentDisableHandler {

    private static final int TICK_INTERVAL = 20;

    // --- Layer 1: block the enchanting table from ever offering enchantments ---
    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        event.setEnchantLevel(0);
    }

    // --- Layer 2: catch-all scrub for anything that slips through (mob drops, loot, trades, mods ignoring config) ---
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if (server.getTickCount() % TICK_INTERVAL != 0) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                stripEnchantments(inventory.getItem(i));
                // Convert enchanted books to normal books
                convertEnchantedBook(inventory, i);
            }
        }
    }

    private static void stripEnchantments(ItemStack stack) {
        if (stack.isEmpty()) return;
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        boolean changed = false;
        if (tag.contains("Enchantments")) {
            tag.remove("Enchantments");
            changed = true;
        }
        if (tag.contains("StoredEnchantments")) {
            tag.remove("StoredEnchantments");
            changed = true;
        }

        if (changed) {
            stack.setTag(tag.isEmpty() ? null : tag);
        }
    }

    private static void convertEnchantedBook(net.minecraft.world.entity.player.Inventory inventory, int slot) {
        ItemStack stack = inventory.getItem(slot);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof net.minecraft.world.item.EnchantedBookItem)) return;

        // Replace with normal book, same count
        ItemStack normalBook = new ItemStack(net.minecraft.world.item.Items.BOOK, stack.getCount());
        inventory.setItem(slot, normalBook);
    }
}
