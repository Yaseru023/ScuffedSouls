package net.yaseruxd.scuffedsouls.registry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.recipe.ModRecipeTypes;


public class ModRegistry {

    public static void init(IEventBus modEventBus) {

        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ITEMS.register(modEventBus);
        ModNetwork.register();
        ModSounds.SOUNDS.register(modEventBus);

        ModRecipeTypes.SERIALIZERS.register(modEventBus);


    }
}