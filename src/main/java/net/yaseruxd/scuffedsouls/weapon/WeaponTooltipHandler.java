package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponTooltipHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        WeaponRequirement req = WeaponRequirementManager.get(itemId);
        if (req == null) return;

        Player player = event.getEntity();

        event.getToolTip().add(Component.empty());
        event.getToolTip().add(
                Component.literal("Requirements")
                        .withStyle(ChatFormatting.GOLD)
        );

        for (WeaponRequirement.StatRequirement stat : req.stats) {

            double playerLevel = player == null
                    ? 0
                    : WeaponRequirementManager.getPlayerStat(player, stat.stat());

            boolean met = playerLevel >= stat.min();

            event.getToolTip().add(
                    Component.literal(
                            (met ? "✔ " : "✖ ")
                                    + getStatName(stat.stat())
                                    + " " + stat.min()
                    ).withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
        }
    }

    private static String getStatName(String stat) {
        return switch (stat.toLowerCase()) {
            case "strength" -> "Strength";
            case "vitality" -> "Vitality";
            case "endurance" -> "Endurance";
            case "intelligence" -> "Intelligence";
            case "magic" -> "Magic";
            case "dexterity" -> "Dexterity";
            case "stamina" -> "Stamina";
            case "luck" -> "Luck";
            default -> stat;
        };
    }
}
