
package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(
        modid = "scuffedsouls",
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ZombieSpawnHandler {

    private record ItemRef(String namespace, String path) {}

    private static final Random RANDOM = new Random();

    private static final float ELITE_CHANCE = 0.05f;

    private static final String ME = "magistuarmory";
    private static final String MEA = "magistuarmoryaddon";

    // Normal zombie equipment
    private static final List<ItemRef> HELMETS = List.of(
            new ItemRef(ME, "rustedchainmail_helmet"),
            new ItemRef(ME, "rustedkettlehat"),
            new ItemRef(ME, "rustedbarbute")
    );

    private static final List<ItemRef> CHESTS = List.of(
            new ItemRef(ME, "rustedhalfarmor_chestplate"),
            new ItemRef(ME, "rustedchainmail_chestplate")
    );

    private static final List<ItemRef> LEGS = List.of(
            new ItemRef(ME, "rustedchainmail_leggings")
    );

    private static final List<ItemRef> BOOTS = List.of(
            new ItemRef(ME, "rustedchainmail_boots")
    );

    private static final List<ItemRef> WEAPONS = List.of(
            new ItemRef(ME, "rusted_heavymace"),
            new ItemRef(ME, "club"),
            new ItemRef(ME, "rusted_bastardsword"),
            new ItemRef(ME, "barbedclub")
    );

    // Elite zombie equipment
    private static final ItemRef ELITE_HELMET =
            new ItemRef(ME, "rustedgreathelm");

    private static final ItemRef ELITE_CHESTPLATE =
            new ItemRef(ME, "rustedcrusader_chestplate");

    private static final ItemRef ELITE_LEGGINGS =
            new ItemRef(ME, "rustedchainmail_leggings");

    private static final ItemRef ELITE_BOOTS =
            new ItemRef(ME, "rustedcrusader_boots");

    private static final ItemRef ELITE_WEAPON =
            new ItemRef(ME, "stone_flamebladedsword");

    private static final String ELITE_TAG = "scuffedsouls_elite_zombie";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        if (RANDOM.nextFloat() < ELITE_CHANCE) {
            spawnEliteZombie(zombie);
        } else {
            spawnNormalZombie(zombie);
        }
    }

    private static void spawnNormalZombie(Zombie zombie) {
        equip(zombie, EquipmentSlot.HEAD, pick(HELMETS));
        equip(zombie, EquipmentSlot.CHEST, pick(CHESTS));
        equip(zombie, EquipmentSlot.LEGS, pick(LEGS));
        equip(zombie, EquipmentSlot.FEET, pick(BOOTS));
        equip(zombie, EquipmentSlot.MAINHAND, pick(WEAPONS));

        setNoGearDrop(zombie);
    }

    private static void spawnEliteZombie(Zombie zombie) {
        zombie.getPersistentData().putBoolean(ELITE_TAG, true);

        equip(zombie, EquipmentSlot.HEAD, ELITE_HELMET);
        equip(zombie, EquipmentSlot.CHEST, ELITE_CHESTPLATE);
        equip(zombie, EquipmentSlot.LEGS, ELITE_LEGGINGS);
        equip(zombie, EquipmentSlot.FEET, ELITE_BOOTS);
        equip(zombie, EquipmentSlot.MAINHAND, ELITE_WEAPON);

        setNoGearDrop(zombie);
    }

    private static ItemRef pick(List<ItemRef> list) {
        return list.isEmpty()
                ? null
                : list.get(RANDOM.nextInt(list.size()));
    }

    private static void equip(Zombie zombie, EquipmentSlot slot, ItemRef ref) {
        Item item = getItem(ref);

        if (item != null) {
            zombie.setItemSlot(slot, new ItemStack(item));
        }
    }

    private static Item getItem(ItemRef ref) {
        if (ref == null) return null;

        Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(ref.namespace(), ref.path())
        );

        return (item == null || item == Items.AIR) ? null : item;
    }

    private static void setNoGearDrop(Zombie zombie) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            zombie.setDropChance(slot, 0f);
        }
    }
}
