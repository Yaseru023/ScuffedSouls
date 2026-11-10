package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WeaponScalingManager {

    private static final Map<ResourceLocation, WeaponScaling> SCALING = new HashMap<>();

    public static void clear() { SCALING.clear(); }

    public static void register(WeaponScaling s) { SCALING.put(s.item, s); }

    public static boolean has(ResourceLocation id) { return SCALING.containsKey(id); }

    public static WeaponScaling get(ResourceLocation id) { return SCALING.get(id); }

    public static Collection<WeaponScaling> getAll() { return SCALING.values(); }

    public static double getBonus(Player player, ResourceLocation id) {
        WeaponScaling s = SCALING.get(id);
        return s == null ? 0 : s.totalBonus(player);
    }
}