package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponScalingTooltipHandler {

    // Dark Souls gold color
    private static final TextColor GOLD_COLOR = TextColor.fromRgb(0xC89B3C);

    // Darker gold for the label
    private static final TextColor LABEL_COLOR = TextColor.fromRgb(0xA0A0A0);

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        WeaponRequirement requirement = WeaponRequirementManager.get(itemId);
        if (requirement == null) return;

        int reinforceLevel = WeaponScalingManager.getReinforceLevel(stack);

        // Sort stats by min descending — same order as scaling manager
        List<WeaponRequirement.StatRequirement> stats = new ArrayList<>(requirement.stats);
        stats.sort((a, b) -> Double.compare(b.min(), a.min()));

        // Build the scaling line
        // e.g. "Scaling:  DEX: B"
        // or   "Scaling:  STR: D  DEX: E"
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < stats.size(); i++) {
            WeaponRequirement.StatRequirement stat = stats.get(i);
            String grade = i == 0
                    ? WeaponScalingManager.getPrimaryGrade(reinforceLevel)
                    : WeaponScalingManager.getSecondaryGrade(reinforceLevel);

            String statName = capitalize(stat.stat());
            line.append(statName).append(": ").append(grade);

            if (i < stats.size() - 1) {
                line.append("  ");
            }
        }

        // Add to tooltip
        event.getToolTip().add(
                Component.literal("Scaling: ")
                        .setStyle(Style.EMPTY.withColor(LABEL_COLOR))
                        .append(
                                Component.literal(line.toString())
                                        .setStyle(Style.EMPTY.withColor(GOLD_COLOR))
                        )
        );
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
