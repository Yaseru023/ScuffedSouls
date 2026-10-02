package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    private static final float SPAWN_CHANCE           = 0.80f;
    private static final float SEAL_CRYSTAL_DROP_CHANCE       = 0.02f;
    private static final float SEAL_CRYSTAL_ELITE_DROP_CHANCE = 0.05f;
    private static final float ELITE_CHANCE           = 0.10f;

    // TODO: replace with actual bks_invaders item IDs
    private static final List<String> HELMETS = List.of(
            "look_open_pot_helmet",
            "look_sallet_knight_helmet",
            "look_hound_helmet"
    );

    private static final List<String> CHESTS = List.of(
            "look_saxon_tunic_chestplate",
            "look_sallet_knight_chestplate",
            "look_hound_chestplate"
    );

    private static final List<String> LEGS = List.of(
            "look_saxon_tunic_leggings",
            "look_sallet_knight_leggings",
            "look_hound_leggings"
    );

    private static final List<String> BOOTS = List.of(
            "look_saxon_tunic_boots",
            "look_sallet_knight_boots",
            "look_hound_boots"
    );

    // TODO: replace with actual bks_invaders elite item IDs
    private static final String ELITE_HELMET     = "look_bastion_helmet";
    private static final String ELITE_CHESTPLATE = "look_bastion_chestplate";
    private static final String ELITE_LEGGINGS   = "look_bastion_leggings";
    private static final String ELITE_BOOTS      = "look_bastion_boots";

    @SubscribeEvent
    public static void onHuskSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Husk husk)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        if (RANDOM.nextFloat() < ELITE_CHANCE) {
            spawnEliteHusk(husk);
            return;
        }

        spawnNormalHusk(husk);
    }

    @SubscribeEvent
    public static void onHuskDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Husk)) return;

        Item sealCrystal = getNetherItem("seal_crystal");
        if (sealCrystal == null) return;

        if (event.getEntity().hasCustomName()) {
            // elite drop
            if (RANDOM.nextFloat() < SEAL_CRYSTAL_ELITE_DROP_CHANCE) {
                event.getEntity().spawnAtLocation(new ItemStack(sealCrystal));
            }
        } else {
            // normal drop
            if (RANDOM.nextFloat() < SEAL_CRYSTAL_DROP_CHANCE) {
                event.getEntity().spawnAtLocation(new ItemStack(sealCrystal));
            }
        }
    }

    private static void spawnNormalHusk(Husk husk) {
        Item helmet = getBksItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getBksItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getBksItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getBksItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(husk);
    }

    private static void spawnEliteHusk(Husk husk) {
        Item helmet = getBksItem(ELITE_HELMET);
        Item chest  = getBksItem(ELITE_CHESTPLATE);
        Item legs   = getBksItem(ELITE_LEGGINGS);
        Item boots  = getBksItem(ELITE_BOOTS);

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(husk);
    }

    private static Item getBksItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("bks_invaders", itemId));
    }

    private static Item getNetherItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("nether_remastered", itemId));
    }

    private static void setNoGearDrop(Husk husk) {
        husk.setDropChance(EquipmentSlot.MAINHAND, 0f);
        husk.setDropChance(EquipmentSlot.OFFHAND,  0f);
        husk.setDropChance(EquipmentSlot.HEAD,     0f);
        husk.setDropChance(EquipmentSlot.CHEST,    0f);
        husk.setDropChance(EquipmentSlot.LEGS,     0f);
        husk.setDropChance(EquipmentSlot.FEET,     0f);
    }
}