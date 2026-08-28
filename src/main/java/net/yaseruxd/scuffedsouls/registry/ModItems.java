package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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

}

