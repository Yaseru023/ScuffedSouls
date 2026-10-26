package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ClassManager {

    private static final String CLASS_KEY = "scuffedsouls_class";

    public static void assignClass(ServerPlayer player, PlayerClass playerClass) {
        // Save class to persistent NBT
        player.getPersistentData().putString(CLASS_KEY, playerClass.name());

        giveStartingItems(player, playerClass);
        giveEstusFlask(player);
        applyStats(player, playerClass);
        applySpells(player, playerClass);  // immediate, no delay needed

        // Save player data
        MinecraftServer server = player.getServer();
        if (server != null) {
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                server.execute(() -> server.getPlayerList().saveAll());
            }).start();
        }
    }

    public static boolean hasClass(ServerPlayer player) {
        return player.getPersistentData().contains(CLASS_KEY);
    }

    public static PlayerClass getClass(ServerPlayer player) {
        String className = player.getPersistentData().getString(CLASS_KEY);
        try {
            return PlayerClass.valueOf(className);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void giveStartingItems(ServerPlayer player, PlayerClass playerClass) {
        for (ItemStack stack : playerClass.startingItems) {
            if (stack.isEmpty()) continue;
            var slot = player.getEquipmentSlotForItem(stack);
            switch (slot) {
                case HEAD  -> player.setItemSlot(EquipmentSlot.HEAD,  stack.copy());
                case CHEST -> player.setItemSlot(EquipmentSlot.CHEST, stack.copy());
                case LEGS  -> player.setItemSlot(EquipmentSlot.LEGS,  stack.copy());
                case FEET  -> player.setItemSlot(EquipmentSlot.FEET,  stack.copy());
                default    -> player.getInventory().add(stack.copy());
            }
        }
    }

    private static void giveEstusFlask(ServerPlayer player) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bonfires", "estus_flask"));
        if (item == null) return;

        ItemStack flask = new ItemStack(item, 1);
        CompoundTag tag = new CompoundTag();
        tag.putInt("estus", 5);
        tag.putInt("reinforce_level", 5);
        tag.putInt("uses", 5);
        tag.putInt("reinforce_max", 10);
        flask.setTag(tag);

        player.getInventory().add(flask);
    }

    private static void applyStats(ServerPlayer player, PlayerClass playerClass) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        CommandSourceStack source = server.createCommandSourceStack().withSuppressedOutput();
        runCommand(server, source, player, "esr_setstat1", playerClass.str);
        runCommand(server, source, player, "esr_setstat2", playerClass.vit);
        runCommand(server, source, player, "esr_setstat3", playerClass.end);
        runCommand(server, source, player, "esr_setstat4", playerClass.intel);
        runCommand(server, source, player, "esr_setstat5", playerClass.mag);
        runCommand(server, source, player, "esr_setstat6", playerClass.dex);
        runCommand(server, source, player, "esr_setstat7", playerClass.sta);
        runCommand(server, source, player, "esr_setstat8", playerClass.lck);
    }

    // Fires /spellcraft learn <spell> immediately for each starting spell.
    // No delay needed since spellcraft doesn't have Epic Fight's capability-init timing issue.
    private static void applySpells(ServerPlayer player, PlayerClass playerClass) {
        if (playerClass.startingSpells.isEmpty()) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        // "learn" is executor-based (getPlayerOrException()) so we impersonate the player
        // as the command source — this makes the server run the command AS the player,
        // so getPlayerOrException() returns the correct target.
        CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(4)
                .withSuppressedOutput();

        for (String spell : playerClass.startingSpells) {
            String cmd = String.format("spellcraft learn %s", spell);
            server.getCommands().performPrefixedCommand(source, cmd);
        }
    }

    private static void runCommand(MinecraftServer server, CommandSourceStack source,
                                   ServerPlayer player, String command, int value) {
        String cmd = String.format("%s %s %d", command, player.getName().getString(), value);
        server.getCommands().performPrefixedCommand(source, cmd);
    }
}