package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WeaponScalingManager {

    public static final UUID SCALING_MODIFIER_UUID =
            UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public static final String MODIFIER_NAME = "scuffedsouls:weapon_scaling";

    public static String getPrimaryGrade(int level) {
        if (level >= 9) return "A";
        if (level >= 7) return "B";
        if (level >= 5) return "C";
        if (level >= 1) return "D";
        return "E";
    }

    public static String getSecondaryGrade(int level) {
        if (level >= 9) return "B";
        if (level >= 7) return "C";
        if (level >= 5) return "D";
        if (level >= 3) return "D";
        if (level >= 1) return "E";
        return "E";
    }

    public static double getGradeMultiplier(String grade) {
        return switch (grade) {
            case "S" -> 0.20;
            case "A" -> 0.15;
            case "B" -> 0.12;
            case "C" -> 0.08;
            case "D" -> 0.05;
            default  -> 0.02; // E
        };
    }

    public static int getReinforceLevel(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return 0;
        if (stack.getTag().contains("reinforce_level")) {
            return stack.getTag().getInt("reinforce_level");
        }
        return 0;
    }

    public static void applyScalingModifier(ItemStack stack, Player player) {
        // If holding nothing or no requirement, remove the modifier and bail
        if (stack.isEmpty()) {
            removeScalingModifier(player);
            return;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        System.out.println("[ScuffedSouls] applyScalingModifier called for: " + itemId);

        if (itemId == null) {
            removeScalingModifier(player);
            return;
        }

        WeaponRequirement requirement = WeaponRequirementManager.get(itemId);
        System.out.println("[ScuffedSouls] requirement found: " + (requirement != null));

        if (requirement == null) {
            removeScalingModifier(player);
            return;
        }

        int reinforceLevel = getReinforceLevel(stack);
        System.out.println("[ScuffedSouls] reinforce level: " + reinforceLevel);

        if (reinforceLevel <= 0) {
            removeScalingModifier(player);
            return;
        }

        // Sort stats by min descending to determine primary and secondary
        List<WeaponRequirement.StatRequirement> stats = new ArrayList<>(requirement.stats);
        stats.sort((a, b) -> Double.compare(b.min(), a.min()));

        WeaponRequirement.StatRequirement primaryStat = stats.get(0);
        WeaponRequirement.StatRequirement secondaryStat = stats.size() > 1 ? stats.get(1) : null;

        String primaryGrade = getPrimaryGrade(reinforceLevel);
        String secondaryGrade = secondaryStat != null ? getSecondaryGrade(reinforceLevel) : null;

        double primaryStatValue = WeaponRequirementManager.getPlayerStat(player, primaryStat.stat());
        double secondaryStatValue = secondaryStat != null
                ? WeaponRequirementManager.getPlayerStat(player, secondaryStat.stat()) : 0;

        double bonus = primaryStatValue * getGradeMultiplier(primaryGrade);
        if (secondaryStat != null && secondaryGrade != null) {
            bonus += secondaryStatValue * getGradeMultiplier(secondaryGrade);
        }

        System.out.println("[ScuffedSouls] calculated bonus: " + bonus);

        if (bonus <= 0) {
            removeScalingModifier(player);
            return;
        }

        // Apply directly onto the player's attack damage attribute
        var attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) return;

        // Remove old one first to avoid duplicates
        attackDamage.removeModifier(SCALING_MODIFIER_UUID);
        // Then add the new one
        attackDamage.addTransientModifier(new AttributeModifier(
                SCALING_MODIFIER_UUID,
                MODIFIER_NAME,
                bonus,
                AttributeModifier.Operation.ADDITION
        ));

        System.out.println("[ScuffedSouls] Applied bonus: " + bonus + " to " + player.getName().getString());
    }

    public static void removeScalingModifier(Player player) {
        var attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) return;
        attackDamage.removeModifier(SCALING_MODIFIER_UUID);
    }
}