package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class BuildupStorage {

    private static final String ROOT = "ScuffedSoulsBuildup";

    public static BuildupData get(Player player) {
        CompoundTag root = player.getPersistentData().getCompound(ROOT);
        return BuildupData.fromNBT(root);
    }

    public static void save(Player player, BuildupData data) {
        player.getPersistentData().put(ROOT, data.toNBT());
    }

    public static void reset(Player player) {
        player.getPersistentData().remove(ROOT);
    }
}