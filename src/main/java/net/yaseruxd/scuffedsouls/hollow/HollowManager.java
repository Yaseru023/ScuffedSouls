package net.yaseruxd.scuffedsouls.hollow;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class HollowManager {

    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID STAMINA_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

    private static final String HEALTH_MODIFIER_NAME = "scuffedsouls:hollow_health";
    private static final String STAMINA_MODIFIER_NAME = "scuffedsouls:hollow_stamina";

    private static final double REDUCTION_PER_LEVEL = -0.1; // -10% per level

    public static void applyModifiers(Player player) {
        int level = HollowData.getLevel(player);
        double reduction = REDUCTION_PER_LEVEL * level;

        applyAttribute(player,
                Attributes.MAX_HEALTH,
                HEALTH_MODIFIER_UUID,
                HEALTH_MODIFIER_NAME,
                reduction);

        Attribute staminaAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("epicfight", "staminar"));

        if (staminaAttr != null) {
            applyAttribute(player,
                    staminaAttr,
                    STAMINA_MODIFIER_UUID,
                    STAMINA_MODIFIER_NAME,
                    reduction);
        }
    }

    public static void removeModifiers(Player player) {
        removeAttribute(player, Attributes.MAX_HEALTH, HEALTH_MODIFIER_UUID);

        Attribute staminaAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("epicfight", "staminar"));

        if (staminaAttr != null) {
            removeAttribute(player, staminaAttr, STAMINA_MODIFIER_UUID);
        }
    }

    private static void applyAttribute(Player player, Attribute attribute,
                                       UUID uuid, String name, double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        // Remove existing first to avoid stacking
        instance.removeModifier(uuid);

        if (amount != 0) {
            instance.addPermanentModifier(new AttributeModifier(
                    uuid,
                    name,
                    amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
    }

    private static void removeAttribute(Player player, Attribute attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(uuid);
    }
}