package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeaponScalingSyncPacket {

    private final List<WeaponScaling> data;

    public WeaponScalingSyncPacket(List<WeaponScaling> data) { this.data = data; }

    public static WeaponScalingSyncPacket fromManager() {
        return new WeaponScalingSyncPacket(new ArrayList<>(WeaponScalingManager.getAll()));
    }

    public static void encode(WeaponScalingSyncPacket p, FriendlyByteBuf buf) {
        buf.writeVarInt(p.data.size());
        for (WeaponScaling s : p.data) {
            buf.writeResourceLocation(s.item);
            buf.writeVarInt(s.stats.size());
            for (WeaponScaling.Entry e : s.stats) {
                buf.writeUtf(e.stat());
                buf.writeDouble(e.perLevel());
                buf.writeVarInt(e.start());
                buf.writeVarInt(e.cap());
            }
        }
    }

    public static WeaponScalingSyncPacket decode(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<WeaponScaling> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            var item = buf.readResourceLocation();
            int m = buf.readVarInt();
            List<WeaponScaling.Entry> entries = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                entries.add(new WeaponScaling.Entry(buf.readUtf(), buf.readDouble(), buf.readVarInt(), buf.readVarInt()));
            }
            list.add(new WeaponScaling(item, entries));
        }
        return new WeaponScalingSyncPacket(list);
    }

    public static void handle(WeaponScalingSyncPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WeaponScalingManager.clear();
            p.data.forEach(WeaponScalingManager::register);
        });
        ctx.get().setPacketHandled(true);
    }
}