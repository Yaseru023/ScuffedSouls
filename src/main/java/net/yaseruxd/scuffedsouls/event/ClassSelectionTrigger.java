package net.yaseruxd.scuffedsouls.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.network.OpenClassScreenPacket;
import net.yaseruxd.scuffedsouls.playerclass.ClassManager;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassSelectionTrigger {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Only send the screen packet if the player hasn't picked a class yet
        if (ClassManager.hasClass(player)) return;

        // Small delay so the client finishes loading the world before the screen opens
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
            player.getServer().execute(() ->
                    ModNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new OpenClassScreenPacket()
                    )
            );
        }).start();
    }
}