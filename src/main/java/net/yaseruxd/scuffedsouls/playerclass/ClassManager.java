package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClassManager {

    private static final String CLASS_KEY = "scuffedsouls_class";

    public static void assignClass(ServerPlayer player, PlayerClass playerClass) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.putString(CLASS_KEY, playerClass.name());

        giveStartingItems(player, playerClass);
        applyStats(player, playerClass);
        applySkills(player, playerClass);

        // Delay save to ensure NBT is fully written first
        MinecraftServer server = player.getServer();
        if (server != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // 1 second delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    // Universal combat skills — same for every class, no per-class identity/passive kit anymore
    private static final String DODGE_SKILL = "wom:precise_roll";
    private static final String GUARD_SKILL = "epicfight:parrying";

    private static void applySkills(ServerPlayer player, PlayerClass playerClass) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        // Delay by 2 seconds to let Epic Fight initialize the player
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            server.execute(() -> {
                CommandSourceStack source = server.createCommandSourceStack().withSuppressedOutput();

                String dodgeCmd = String.format("epicfight skill add %s dodge %s",
                        player.getName().getString(), DODGE_SKILL);
                String guardCmd = String.format("epicfight skill add %s guard %s",
                        player.getName().getString(), GUARD_SKILL);

                server.getCommands().performPrefixedCommand(source, dodgeCmd);
                server.getCommands().performPrefixedCommand(source, guardCmd);
            });
        }).start();
    }

    private static void runCommand(MinecraftServer server, CommandSourceStack source,
                                   ServerPlayer player, String command, int value) {
        String cmd = String.format("%s %s %d", command, player.getName().getString(), value);
        server.getCommands().performPrefixedCommand(source, cmd);
    }
}