package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class WeaponScaling {

    public final ResourceLocation item;
    public final List<Entry> stats;

    public WeaponScaling(ResourceLocation item, List<Entry> stats) {
        this.item = item;
        this.stats = stats;
    }

    public double totalBonus(Player player) {
        double total = 0;
        for (Entry e : stats) {
            total += e.bonus(WeaponRequirementManager.getPlayerStat(player, e.stat()));
        }
        return total;
    }

    public record Entry(String stat, double perLevel, int start, int cap) {

        public double bonus(double level) {
            double effective = Math.min(level, cap) - start;
            return effective > 0 ? effective * perLevel : 0;
        }

        // Tweak these thresholds to taste.
        public String grade() {
            if (perLevel >= 0.12) return "S";
            if (perLevel >= 0.09) return "A";
            if (perLevel >= 0.06) return "B";
            if (perLevel >= 0.04) return "C";
            if (perLevel >= 0.02) return "D";
            return "E";
        }
    }
}