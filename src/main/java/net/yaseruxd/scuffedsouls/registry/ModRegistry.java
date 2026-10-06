package net.yaseruxd.scuffedsouls.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.yaseruxd.scuffedsouls.entity.MemorCustos;
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
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModRecipeTypes.SERIALIZERS.register(modEventBus);

        // Add this line
        modEventBus.addListener(ModRegistry::registerAttributes);
    }

    // Add this method
    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MEMORCUSTOS.get(),
                MemorCustos.createAttributes().build());
    }
}