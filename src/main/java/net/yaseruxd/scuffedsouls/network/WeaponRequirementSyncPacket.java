package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.weapon.WeaponRequirement;
import net.yaseruxd.scuffedsouls.weapon.WeaponRequirementManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Sent PLAY_TO_CLIENT on login (and again after any server datapack reload) so the client's
 * own copy of WeaponRequirementManager's static map gets populated — custom data like this
 * is never auto-synced by Forge the way recipes/tags/advancements are, so without this packet
 * the client's REQUIREMENTS map stays permanently empty when connecting to a real dedicated
 * server (as opposed to singleplayer, where client and integrated server share the same JVM
 * and therefore the same static memory).
 */
public class WeaponRequirementSyncPacket {

    private final List<WeaponRequirement> requirements;

    public WeaponRequirementSyncPacket(List<WeaponRequirement> requirements) {
        this.requirements = requirements;
    }

    public static void encode(WeaponRequirementSyncPacket msg, FriendlyByteBuf buf) {
        // Filter nulls before writing count
        List<WeaponRequirement> valid = msg.requirements.stream()
                .filter(r -> r != null && r.item != null && r.stats != null)
                .toList();

        buf.writeVarInt(valid.size());
        for (WeaponRequirement req : valid) {
            buf.writeResourceLocation(req.item);
            buf.writeVarInt(req.stats.size());
            for (WeaponRequirement.StatRequirement stat : req.stats) {
                buf.writeUtf(stat.stat());
                buf.writeDouble(stat.min());
            }
        }
    }

    public static WeaponRequirementSyncPacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<WeaponRequirement> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ResourceLocation item = buf.readResourceLocation();
            int statCount = buf.readVarInt();
            List<WeaponRequirement.StatRequirement> stats = new ArrayList<>();

            for (int j = 0; j < statCount; j++) {
                String stat = buf.readUtf();
                double min = buf.readDouble();
                stats.add(new WeaponRequirement.StatRequirement(stat, min));
            }

            list.add(new WeaponRequirement(item, stats));
        }

        return new WeaponRequirementSyncPacket(list);
    }

    public static void handle(WeaponRequirementSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WeaponRequirementManager.clear();
            for (WeaponRequirement req : msg.requirements) {
                if (req == null || req.item == null) continue;
                WeaponRequirementManager.register(req);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
