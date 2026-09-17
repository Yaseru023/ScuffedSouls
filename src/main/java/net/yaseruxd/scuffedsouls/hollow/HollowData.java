package net.yaseruxd.scuffedsouls.hollow;

import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class HollowData {

    private static final String KEY = "scuffedsouls:hollow_level";
    private static final int MAX_LEVEL = 5;

    public static int getLevel(Player player) {
        CompoundTag tag = player.getPersistentData();
        return tag.getInt(KEY);
    }

    public static void setLevel(Player player, int level) {
        CompoundTag tag = player.getPersistentData();
        tag.putInt(KEY, Mth.clamp(level, 0, MAX_LEVEL));
    }

    public static void increment(Player player) {
        setLevel(player, getLevel(player) + 1);
    }

    public static void decrement(Player player) {
        setLevel(player, getLevel(player) - 1);
    }

    public static boolean isMaxHollow(Player player) {
        return getLevel(player) >= MAX_LEVEL;
    }
}