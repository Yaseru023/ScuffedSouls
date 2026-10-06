package net.yaseruxd.scuffedsouls.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.client.renderer.MemorCustosRenderer;
import net.yaseruxd.scuffedsouls.registry.ModEntities;

@Mod.EventBusSubscriber(
        modid = ScuffedSouls.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ModRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.MEMORCUSTOS.get(),
                MemorCustosRenderer::new
        );
    }
}