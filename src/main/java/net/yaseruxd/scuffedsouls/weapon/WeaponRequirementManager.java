package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.felinamods.epicstatsmodremastered.network.EpicStatsModRemasteredModVariables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WeaponRequirementManager {

    public static double getPlayerStat(Player player, String stat) {
        EpicStatsModRemasteredModVariables.PlayerVariables vars =
                player.getCapability(EpicStatsModRemasteredModVariables.PLAYER_VARIABLES_CAPABILITY)
                        .orElse(null);

        if (vars == null) {
            return 1;
        }

        return switch (stat.toLowerCase()) {
            case "strength"     -> vars.stat_1_level / 0.1;
            case "vitality"     -> vars.stat_2_level / 0.1;
            case "endurance"    -> vars.stat_3_level / 0.1;
            case "intelligence" -> vars.stat_4_level / 0.1;
            case "magic"        -> vars.stat_5_level / 0.1;
            case "dexterity"    -> vars.stat_6_level / 0.1;
            case "stamina"      -> vars.stat_7_level / 0.1;
            case "luck"         -> vars.stat_8_level / 0.1;
            default -> 0;
        };
    }

    private static final Map<ResourceLocation, WeaponRequirement> REQUIREMENTS = new HashMap<>();

    public static void clear() {
        REQUIREMENTS.clear();
    }

    public static void register(WeaponRequirement req) {
        REQUIREMENTS.put(req.item, req);
    }

    public static boolean meetsRequirements(Player player, ResourceLocation itemId) {
        WeaponRequirement req = REQUIREMENTS.get(itemId);
        if (req == null) return true;

        for (WeaponRequirement.StatRequirement stat : req.stats) {
            double playerStat = getPlayerStat(player, stat.stat());

            if (playerStat < stat.min()) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasRequirements(ResourceLocation itemId) {
        return REQUIREMENTS.containsKey(itemId);
    }

    public static WeaponRequirement get(ResourceLocation itemId) {
        return REQUIREMENTS.get(itemId);
    }

    // Used by WeaponRequirementSyncPacket to send the full set to a client on login.
    public static Collection<WeaponRequirement> getAll() {
        return REQUIREMENTS.values();
    }
}