package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkeletonSpawnHandler {

    private static final Random RANDOM = new Random();
    private static final float SPAWN_CHANCE = 0.50f;

    // --- Helmet pool ---
    private static final List<String> HELMETS = List.of(
            "rustedgreathelm",
            "rusted_barbut",
            "rustednorman_helmet",
            "rustedchainmail_helmet",
            "rustedkettlehat"
    );
    // --- Chest pool ---
    private static final List<String> CHESTS = List.of(
            "rustedcrusader_chestplate",
            "rustedhalfarmor_chestplate",
            "rustedchainmail_chestplate"
    );
    // --- Legs pool ---
    private static final List<String> LEGS = List.of(
            "rustedchainmail_leggings"
    );
    // --- Boots pool ---
    private static final List<String> BOOTS = List.of(
            "rustedcrusader_boots",
            "rustedchainmail_boots"
    );

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSkeletonSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        Item helmet = getItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (helmet != null) skeleton.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) skeleton.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) skeleton.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) skeleton.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(skeleton);
    }

    // magistuarmory
    private static Item getItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("magistuarmory", itemId));
    }

    private static void setNoGearDrop(Skeleton skeleton) {
        skeleton.setDropChance(EquipmentSlot.MAINHAND, 0f);
        skeleton.setDropChance(EquipmentSlot.OFFHAND, 0f);
        skeleton.setDropChance(EquipmentSlot.HEAD, 0f);
        skeleton.setDropChance(EquipmentSlot.CHEST, 0f);
        skeleton.setDropChance(EquipmentSlot.LEGS, 0f);
        skeleton.setDropChance(EquipmentSlot.FEET, 0f);
    }
}