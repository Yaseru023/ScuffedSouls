package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZombieSpawnHandler {

    private static final Random RANDOM = new Random();
    private static final float SPAWN_CHANCE = 0.80f;

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
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (RANDOM.nextFloat() > SPAWN_CHANCE) return;

        if (RANDOM.nextFloat() < 0.05f) {
            spawnEliteZombie(zombie);
            return;
        }

        Item helmet = getItem(HELMETS.get(RANDOM.nextInt(HELMETS.size())));
        Item chest  = getItem(CHESTS.get(RANDOM.nextInt(CHESTS.size())));
        Item legs   = getItem(LEGS.get(RANDOM.nextInt(LEGS.size())));
        Item boots  = getItem(BOOTS.get(RANDOM.nextInt(BOOTS.size())));

        if (helmet != null) zombie.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) zombie.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) zombie.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));

        setNoGearDrop(zombie);
    }

    private static void spawnEliteZombie(Zombie zombie) {
        Item helmet = getItem("slave_knight_helmet");
        Item chest  = getItem("slave_knight_chestplate");
        Item legs   = getItem("slave_knight_leggings");
        Item boots  = getItem("slave_knight_boots");

        if (helmet != null) zombie.setItemSlot(EquipmentSlot.HEAD,  new ItemStack(helmet));
        if (chest  != null) zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        if (legs   != null) zombie.setItemSlot(EquipmentSlot.LEGS,  new ItemStack(legs));
        if (boots  != null) zombie.setItemSlot(EquipmentSlot.FEET,  new ItemStack(boots));
        if (boots  != null) zombie.setItemSlot(EquipmentSlot.MAINHAND,  new ItemStack(boots));

        setNoGearDrop(zombie);
    }

    private static Item getItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("slu", itemId));
    }

    private static Item getEpicItem(String itemId) {
        return ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("epicfight", itemId));
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