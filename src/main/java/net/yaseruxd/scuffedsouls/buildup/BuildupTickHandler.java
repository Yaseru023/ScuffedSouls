package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildupTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;

        BuildupManager.decayAndSync(event.player);
    }
}