package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.client.ClientBuildupData;

import java.util.function.Supplier;

public class BuildupSyncPacket {

    private final ResourceLocation effectId;
    private final float amount;

    public BuildupSyncPacket(ResourceLocation effectId, float amount) {
        this.effectId = effectId;
        this.amount = amount;
    }

    public static void encode(BuildupSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.effectId);
        buf.writeFloat(packet.amount);
    }

    public static BuildupSyncPacket decode(FriendlyByteBuf buf) {
        return new BuildupSyncPacket(buf.readResourceLocation(), buf.readFloat());
    }

    public static void handle(BuildupSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                ClientBuildupData.set(packet.effectId, packet.amount));
        ctx.get().setPacketHandled(true);
    }
}