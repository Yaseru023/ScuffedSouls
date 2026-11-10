package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = "scuffedsouls", value = Dist.CLIENT)
public class WeaponScalingTooltip {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (id == null) return;

        WeaponScaling scaling = WeaponScalingManager.get(id);
        if (scaling == null) return;

        Player player = event.getEntity();
        List<Component> tip = event.getToolTip();

        tip.add(Component.empty());
        tip.add(Component.literal("Scaling").withStyle(ChatFormatting.GRAY));

        double total = 0;
        for (WeaponScaling.Entry e : scaling.stats) {
            String name = e.stat().substring(0, 1).toUpperCase(Locale.ROOT) + e.stat().substring(1);

            MutableComponent line = Component.literal(" " + name + " ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(e.grade()).withStyle(gradeColor(e.grade())));

            if (player != null) {
                double bonus = e.bonus(WeaponRequirementManager.getPlayerStat(player, e.stat()));
                total += bonus;
                line.append(Component.literal(String.format(" (+%.2f)", bonus))
                        .withStyle(bonus > 0 ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
            }
            tip.add(line);
        }

        if (player != null) {
            tip.add(Component.literal(String.format(" Bonus Damage: +%.2f", total)).withStyle(ChatFormatting.GOLD));
        }
    }

    private static ChatFormatting gradeColor(String grade) {
        return switch (grade) {
            case "S" -> ChatFormatting.LIGHT_PURPLE;
            case "A" -> ChatFormatting.GOLD;
            case "B" -> ChatFormatting.YELLOW;
            case "C" -> ChatFormatting.GREEN;
            case "D" -> ChatFormatting.AQUA;
            default  -> ChatFormatting.GRAY;
        };
    }
}