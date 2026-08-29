package net.yaseruxd.scuffedsouls;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yaseruxd.scuffedsouls.registry.ModRegistry;
import org.slf4j.Logger;
import net.minecraftforge.common.MinecraftForge;


@Mod(ScuffedSouls.MODID)
public class ScuffedSouls {

    public static final String MODID = "scuffedsouls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ScuffedSouls() {
        IEventBus modEventBus =
                FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistry.init(modEventBus);

        LOGGER.info("ScuffedSouls initializing...");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Client ready: {}", Minecraft.getInstance().getUser().getName());
        }
    }
}