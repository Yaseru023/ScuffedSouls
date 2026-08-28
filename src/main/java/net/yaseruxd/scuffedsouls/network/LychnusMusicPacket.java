package net.yaseruxd.scuffedsouls.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.client.LychnusMusicClientHandler;

import java.util.function.Supplier;

public class LychnusMusicPacket {
    private final boolean playing;
    private final BlockPos position;

    public LychnusMusicPacket(boolean playing, BlockPos position) {
        this.playing = playing;
        this.position = position;
    }

    public static void encode(LychnusMusicPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.playing);
        buf.writeBlockPos(msg.position);
    }

    public static LychnusMusicPacket decode(FriendlyByteBuf buf) {
        return new LychnusMusicPacket(buf.readBoolean(), buf.readBlockPos());
    }

    public static void handle(LychnusMusicPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> LychnusMusicClientHandler.setMusic(msg.playing, msg.position));
        context.setPacketHandled(true);
    }
}
