package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.playerclass.ClassManager;
import net.yaseruxd.scuffedsouls.playerclass.PlayerClass;

import java.util.function.Supplier;

public class ClassSelectionPacket {

    private final PlayerClass selectedClass;

    public ClassSelectionPacket(PlayerClass selectedClass) {
        this.selectedClass = selectedClass;
    }

    // --- Encode (client → buffer) ---
    public static void encode(ClassSelectionPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.selectedClass);
    }

    // --- Decode (buffer → packet) ---
    public static ClassSelectionPacket decode(FriendlyByteBuf buf) {
        return new ClassSelectionPacket(buf.readEnum(PlayerClass.class));
    }

    // --- Handle (server side) ---
    public static void handle(ClassSelectionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (ClassManager.hasClass(player)) return;

            ClassManager.assignClass(player, packet.selectedClass);
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);

            ctx.get().setPacketHandled(true);
        });
    }
}