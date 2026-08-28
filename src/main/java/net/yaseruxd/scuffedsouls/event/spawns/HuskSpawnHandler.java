package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HuskSpawnHandler {

    private static final Random RANDOM = new Random();
    private static final float SPAWN_CHANCE = 0.50f;
    private static final float SEAL_CRYSTAL_DROP_CHANCE        = 0.02f; // 2% normal husk
    private static final float SEAL_CRYSTAL_ELITE_DROP_CHANCE  = 0.01f; // 5% elite husk

    // --- Weapon pool (magistuarmory) ---
    private static final List<String> WEAPONS = List.of(
            "rusted_bastardsword",
            "rusted_heavymace",
            "stone_zweihander"
    );
    // --- Helmet pool (magistuarmory) ---
    private static final List<String> HELMETS = List.of(
            "rustedgreathelm",
            "rusted_barbut",
            "rustednorman_helmet",
            "rustedchainmail_helmet",
            "rustedkettlehat"
    );
    // --- Chest pool (magistuarmory) ---
    private static final List<String> CHESTS = List.of(
            "rustedcrusader_chestplate",
            "rustedhalfarmor_chestplate",
            "rustedchainmail_chestplate"
    );
    // --- Legs pool (magistuarmory) ---
    private static final List<String> LEGS = List.of(
            "rustedchainmail_leggings"
    );
    // --- Boots pool (magistuarmory) ---
    private static final List<String> BOOTS = List.of(
            "rustedcrusader_boots",
            "rustedchainmail_boots"
    );
    // --- Offhand pool (magistuarmory) ---
    private static final List<String> OFFHANDS = List.of(
            "corruptedroundshield"
    );

    @SubscribeEvent
    public static void onHuskSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Husk husk)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        // 10% chance for elite husk
        if (RANDOM.nextFloat() < 0.10f) {
            spawnEliteHusk(husk);
            return;
        }

        Item weapon = getItem(WEAPONS.get(RANDOM.nextInt(WEAPONS.size())));
        Item helmet = getItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (RANDOM.nextFloat() < 0.10f) {
            husk.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else {
            husk.setItemSlot(EquipmentSlot.MAINHAND,
                    new ItemStack(weapon != null ? weapon : Items.IRON_SWORD));
        }

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        if (RANDOM.nextFloat() < 0.40f) {
            Item offhand = getItem(OFFHANDS.get(RANDOM.nextInt(OFFHANDS.size())));
            if (offhand != null) husk.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(offhand));
        }

        setNoGearDrop(husk);
    }

    @SubscribeEvent
    public static void onHuskDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Husk)) return;

        Item sealCrystal = getNetherItem("seal_crystal");
        if (sealCrystal == null) return;

        if (RANDOM.nextFloat() < SEAL_CRYSTAL_DROP_CHANCE) {
            event.getEntity().spawnAtLocation(new ItemStack(sealCrystal));
        }
    }

    private static void spawnEliteHusk(Husk husk) {
        Item weapon = getItem("iron_concavehalberd");
        Item helmet = getItem("rustedgreathelm");
        Item chest  = getItem("rustedcrusader_chestplate");
        Item legs   = getItem("rustedchainmail_leggings");
        Item boots  = getItem("rustedchainmail_boots");

        husk.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(weapon != null ? weapon : Items.DIAMOND_AXE));

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(husk);

        // Elite husks have a higher chance to drop the Seal Crystal
        Item sealCrystal = getNetherItem("seal_crystal");
        if (sealCrystal != null && RANDOM.nextFloat() < SEAL_CRYSTAL_ELITE_DROP_CHANCE) {
            husk.spawnAtLocation(new ItemStack(sealCrystal));
        }
    }

    // magistuarmory
    private static Item getItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("magistuarmory", itemId));
    }
    // magistuarmoryaddon
    private static Item getAddonItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("magistuarmoryaddon", itemId));
    }
    // epicfightdd
    private static Item getDDItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("epicfight_dd", itemId));
    }
    // nether_remastered
    private static Item getNetherItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("nether_remastered", itemId));
    }

    private static void setNoGearDrop(Husk husk) {
        husk.setDropChance(EquipmentSlot.MAINHAND, 0f);
        husk.setDropChance(EquipmentSlot.OFFHAND, 0f);
        husk.setDropChance(EquipmentSlot.HEAD, 0f);
        husk.setDropChance(EquipmentSlot.CHEST, 0f);
        husk.setDropChance(EquipmentSlot.LEGS, 0f);
        husk.setDropChance(EquipmentSlot.FEET, 0f);
    }
}