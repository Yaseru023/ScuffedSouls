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
    private static final float SPAWN_CHANCE = 0.80f;
    private static final float SEAL_CRYSTAL_DROP_CHANCE       = 0.02f;
    private static final float SEAL_CRYSTAL_ELITE_DROP_CHANCE = 0.05f;

    private static final List<String> HELMETS = List.of(
            "soldier_helmet"
    );

    private static final List<String> CHESTS = List.of(
            "soldier_chestplate"
    );

    private static final List<String> LEGS = List.of(
            "soldier_leggings"
    );

    private static final List<String> BOOTS = List.of(
            "soldier_boots"
    );

    @SubscribeEvent
    public static void onHuskSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Husk husk)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        if (RANDOM.nextFloat() < 0.10f) {
            spawnEliteHusk(husk);
            return;
        }

        Item helmet = getItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

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
        Item helmet = getItem("chaos_knight_helmet");
        Item chest  = getItem("chaos_knight_chestplate");
        Item legs   = getItem("chaos_knight_leggings");
        Item boots  = getItem("chaos_knight_boots");

        if (helmet != null) husk.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) husk.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) husk.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(husk);

        Item sealCrystal = getNetherItem("seal_crystal");
        if (sealCrystal != null && RANDOM.nextFloat() < SEAL_CRYSTAL_ELITE_DROP_CHANCE) {
            husk.spawnAtLocation(new ItemStack(sealCrystal));
        }
    }

    private static Item getItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("slu", itemId));
    }

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