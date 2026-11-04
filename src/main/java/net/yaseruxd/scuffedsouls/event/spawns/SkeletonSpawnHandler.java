
package net.yaseruxd.scuffedsouls.event.spawns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Skeleton;
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
public class SkeletonSpawnHandler {

    private record ItemRef(String namespace, String path) {}

    private static final Random RANDOM = new Random();

    // 60% chance to spawn with ScuffedSouls equipment
    private static final float EQUIPMENT_CHANCE = 0.60f;

    private static final String ME = "magistuarmory";

    // Skeleton armor
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


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSkeletonSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;

        // Not every skeleton receives ScuffedSouls equipment
        if (RANDOM.nextFloat() >= EQUIPMENT_CHANCE) return;

        equip(skeleton, EquipmentSlot.HEAD, pick(HELMETS));
        equip(skeleton, EquipmentSlot.CHEST, pick(CHESTS));
        equip(skeleton, EquipmentSlot.LEGS, pick(LEGS));
        equip(skeleton, EquipmentSlot.FEET, pick(BOOTS));

        setNoGearDrop(skeleton);
    }

    private static ItemRef pick(List<ItemRef> list) {
        return list.isEmpty()
                ? null
                : list.get(RANDOM.nextInt(list.size()));
    }

    private static void equip(
            Skeleton skeleton,
            EquipmentSlot slot,
            ItemRef ref
    ) {
        Item item = getItem(ref);

        if (item != null) {
            skeleton.setItemSlot(slot, new ItemStack(item));
        }
    }

    private static Item getItem(ItemRef ref) {
        if (ref == null) return null;

        Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(ref.namespace(), ref.path())
        );

        return (item == null || item == Items.AIR) ? null : item;
    }

    private static void setNoGearDrop(Skeleton skeleton) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            skeleton.setDropChance(slot, 0f);
        }
    }
}
