package net.yaseruxd.scuffedsouls.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.client.gui.ClassSelectionScreen;

import java.util.function.Supplier;

/**
 * PLAY_TO_CLIENT. Sent by the server to a specific player telling them to open
 * the class selection screen. Fired on first login when no class is assigned yet.
 * Uses DistExecutor to safely reference client-only classes (Screen, Minecraft)
 * without crashing on a dedicated server.
 */
public class OpenClassScreenPacket {

    public OpenClassScreenPacket() {}

    public static void encode(OpenClassScreenPacket msg, FriendlyByteBuf buf) {
        // No payload needed — just the signal to open the screen
    }

    public static OpenClassScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenClassScreenPacket();
    }

    public static void handle(OpenClassScreenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        Minecraft.getInstance().setScreen(new ClassSelectionScreen())));
        ctx.get().setPacketHandled(true);
    }
}