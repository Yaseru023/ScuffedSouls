package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZombieSpawnHandler {

    private static final Random RANDOM = new Random();
    private static final float SPAWN_CHANCE = 0.50f;

    // --- Weapon pool ---
    private static final List<String> WEAPONS = List.of(
            "rusted_bastardsword",
            "rusted_heavymace",
            "stone_zweihander"
    );
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

    // --- Offhand pool ---
    private static final List<String> OFFHANDS = List.of(
            "corruptedroundshield"
    );

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        // 5% chance to spawn as an elite heavy zombie
        if (RANDOM.nextFloat() < 0.05f) {
            spawnEliteZombie(zombie);
            return; // skip normal gear logic
        }

        // --- normal gear logic below, unchanged ---
        Item weapon = getItem(WEAPONS.get(RANDOM.nextInt(WEAPONS.size())));
        Item helmet = getItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (RANDOM.nextFloat() < 0.10f) {
            zombie.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else {
            zombie.setItemSlot(EquipmentSlot.MAINHAND,
                    new ItemStack(weapon != null ? weapon : Items.IRON_SWORD));
        }

        if (helmet != null) zombie.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) zombie.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) zombie.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        if (RANDOM.nextFloat() < 0.40f) {
            Item offhand = getItem(OFFHANDS.get(RANDOM.nextInt(OFFHANDS.size())));
            if (offhand != null) zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(offhand));
        }
        setNoGearDrop(zombie);
    }

    private static void spawnEliteZombie(Zombie zombie) {
        Item weapon = getAddonItem("steel_king_sword");
        Item helmet = getAddonItem("gilded_dark_close_helmet");
        Item chest  = getAddonItem("dark_gilded_parade_chestplate");
        Item legs   = getAddonItem("dark_maximilian_leggings");
        Item boots  = getAddonItem("dark_gilded_parade_boots");

        zombie.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(weapon != null ? weapon : Items.DIAMOND_AXE));

        if (helmet != null) zombie.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) zombie.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) zombie.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(zombie);
    }
    // Magistuarmory
    private static Item getItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("magistuarmory", itemId));
    }
    // MagistuarmoryAddon
    private static Item getAddonItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("magistuarmoryaddon", itemId));
    }
    private static void setNoGearDrop(Zombie zombie) {
        zombie.setDropChance(EquipmentSlot.MAINHAND, 0f);
        zombie.setDropChance(EquipmentSlot.OFFHAND, 0f);
        zombie.setDropChance(EquipmentSlot.HEAD, 0f);
        zombie.setDropChance(EquipmentSlot.CHEST, 0f);
        zombie.setDropChance(EquipmentSlot.LEGS, 0f);
        zombie.setDropChance(EquipmentSlot.FEET, 0f);
    }
}