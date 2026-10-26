package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.playerclass.ClassManager;
import net.yaseruxd.scuffedsouls.playerclass.PlayerClass;

import java.util.function.Supplier;

/**
 * Sent CLIENT -> SERVER when the player confirms their class selection.
 * Server validates the player hasn't already picked a class, then calls
 * ClassManager.assignClass() to grant items, stats, and spells.
 */
public class ClassSelectionPacket {

    private final PlayerClass playerClass;

    public ClassSelectionPacket(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public static void encode(ClassSelectionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.playerClass);
    }

    public static ClassSelectionPacket decode(FriendlyByteBuf buf) {
        return new ClassSelectionPacket(buf.readEnum(PlayerClass.class));
    }

    public static void handle(ClassSelectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // Prevent re-selecting a class once already assigned
            if (ClassManager.hasClass(player)) return;

            ClassManager.assignClass(player, msg.playerClass);
        });
        ctx.get().setPacketHandled(true);
    }
}