
package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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
public class HuskSpawnHandler {

    private record ItemRef(String namespace, String path) {}

    private static final Random RANDOM = new Random();

    private static final float ELITE_CHANCE = 0.10f;
    private static final float SEAL_CRYSTAL_DROP_CHANCE = 0.02f;
    private static final float SEAL_CRYSTAL_ELITE_DROP_CHANCE = 0.05f;

    private static final String ELITE_TAG = "scuffedsouls_elite_husk";

    private static final String ME = "magistuarmory";
    private static final String MEA = "magistuarmoryaddon";

    // Normal husk equipment
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

    // Elite husk equipment
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

    // Run after vanilla's equipment roll.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHuskSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Husk husk)) return;

        // Every husk gets either normal or elite equipment.
        if (RANDOM.nextFloat() < ELITE_CHANCE) {
            spawnEliteHusk(husk);
        } else {
            spawnNormalHusk(husk);
        }
    }

    @SubscribeEvent
    public static void onHuskDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Husk husk)) return;

        Item sealCrystal = getItem(
                new ItemRef("nether_remastered", "seal_crystal")
        );

        if (sealCrystal == null) return;

        boolean elite = husk.getPersistentData().getBoolean(ELITE_TAG);

        float chance = elite
                ? SEAL_CRYSTAL_ELITE_DROP_CHANCE
                : SEAL_CRYSTAL_DROP_CHANCE;

        if (RANDOM.nextFloat() < chance) {
            husk.spawnAtLocation(new ItemStack(sealCrystal));
        }
    }

    private static void spawnNormalHusk(Husk husk) {
        equip(husk, EquipmentSlot.HEAD, pick(HELMETS));
        equip(husk, EquipmentSlot.CHEST, pick(CHESTS));
        equip(husk, EquipmentSlot.LEGS, pick(LEGS));
        equip(husk, EquipmentSlot.FEET, pick(BOOTS));
        equip(husk, EquipmentSlot.MAINHAND, pick(WEAPONS));

        setNoGearDrop(husk);
    }

    private static void spawnEliteHusk(Husk husk) {
        husk.getPersistentData().putBoolean(ELITE_TAG, true);

        equip(husk, EquipmentSlot.HEAD, ELITE_HELMET);
        equip(husk, EquipmentSlot.CHEST, ELITE_CHESTPLATE);
        equip(husk, EquipmentSlot.LEGS, ELITE_LEGGINGS);
        equip(husk, EquipmentSlot.FEET, ELITE_BOOTS);
        equip(husk, EquipmentSlot.MAINHAND, ELITE_WEAPON);

        setNoGearDrop(husk);
    }

    private static ItemRef pick(List<ItemRef> list) {
        return list.isEmpty()
                ? null
                : list.get(RANDOM.nextInt(list.size()));
    }

    private static void equip(Husk husk, EquipmentSlot slot, ItemRef ref) {
        Item item = getItem(ref);

        if (item != null) {
            husk.setItemSlot(slot, new ItemStack(item));
        }
    }

    private static Item getItem(ItemRef ref) {
        if (ref == null) return null;

        Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(ref.namespace(), ref.path())
        );

        return (item == null || item == Items.AIR) ? null : item;
    }

    private static void setNoGearDrop(Husk husk) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            husk.setDropChance(slot, 0f);
        }
    }
}
