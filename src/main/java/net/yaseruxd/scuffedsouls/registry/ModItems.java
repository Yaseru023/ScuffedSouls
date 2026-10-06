package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ScuffedSouls.MODID);

    /* ---------------- WOM Components ---------------- */

    public static final RegistryObject<Item> TORMENTED_SOUL =
            ITEMS.register("tormented_soul",
                    () -> new Item(
                            new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> WEEPING_CORE =
            ITEMS.register("weeping_core",
                    () -> new Item(
                            new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    // In ModItems.java
    public static final RegistryObject<Item> FERRUM_SPAWN_EGG =
            ITEMS.register("ferrum_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.MEMORCUSTOS, 0x2C2C2C, 0x8B0000,
                            new Item.Properties())
            );

}

