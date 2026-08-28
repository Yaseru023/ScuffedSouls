package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenClassScreenPacket {

    public static void encode(OpenClassScreenPacket packet, FriendlyByteBuf buf) {}

    public static OpenClassScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenClassScreenPacket();
    }

    public static void handle(OpenClassScreenPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientProxy::openClassScreen)
        );
        ctx.get().setPacketHandled(true);
    }
}