package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.client.ClientHollowData;

import java.util.function.Supplier;

public class HollowSyncPacket {

    private final int level;

    public HollowSyncPacket(int level) {
        this.level = level;
    }

    public static void encode(HollowSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.level);
    }

    public static HollowSyncPacket decode(FriendlyByteBuf buf) {
        return new HollowSyncPacket(buf.readInt());
    }

    public static void handle(HollowSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                ClientHollowData.set(packet.level));
        ctx.get().setPacketHandled(true);
    }
}