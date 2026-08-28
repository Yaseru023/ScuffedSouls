package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.block.SoulAnchorBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ScuffedSouls.MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ScuffedSouls.MODID);

    public static final RegistryObject<Block> SOUL_ANCHOR =
            BLOCKS.register("soul_anchor", () -> new SoulAnchorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(-1.0f, 3600000.0f) // unbreakable by players
                            .sound(SoundType.SOUL_SAND)
                            .lightLevel(s -> 15)           // glows slightly
                            .noOcclusion()
            ));

    // BlockItem so it exists as an item too (needed for placement)
    public static final RegistryObject<Item> SOUL_ANCHOR_ITEM =
            BLOCK_ITEMS.register("soul_anchor", () -> new BlockItem(
                    SOUL_ANCHOR.get(), new Item.Properties()));
}
