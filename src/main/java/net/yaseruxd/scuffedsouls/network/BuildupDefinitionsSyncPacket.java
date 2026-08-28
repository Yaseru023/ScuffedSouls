package net.yaseruxd.scuffedsouls.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.yaseruxd.scuffedsouls.buildup.BuildupDefinition;
import net.yaseruxd.scuffedsouls.buildup.BuildupDefinitions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BuildupDefinitionsSyncPacket {

    private final Map<ResourceLocation, BuildupDefinition> definitions;

    public BuildupDefinitionsSyncPacket(Map<ResourceLocation, BuildupDefinition> definitions) {
        this.definitions = definitions;
    }

    public static void encode(BuildupDefinitionsSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.definitions.size());
        for (Map.Entry<ResourceLocation, BuildupDefinition> entry : packet.definitions.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            BuildupDefinition def = entry.getValue();
            buf.writeFloat(def.getMaxBuildup());
            buf.writeFloat(def.getDecayPerTick());
            buf.writeFloat(def.getBuildupPerApplication());
            buf.writeInt(def.getColor());
            buf.writeUtf(def.getEffectId());
            buf.writeInt(def.getDuration());
            buf.writeInt(def.getAmplifier());
        }
    }

    public static BuildupDefinitionsSyncPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<ResourceLocation, BuildupDefinition> definitions = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation effectId = buf.readResourceLocation();
            float max = buf.readFloat();
            float decay = buf.readFloat();
            float perApp = buf.readFloat();
            int color = buf.readInt();
            String effectStr = buf.readUtf();
            int duration = buf.readInt();
            int amplifier = buf.readInt();
            definitions.put(effectId, new BuildupDefinition(
                    max, decay, perApp, color, effectStr, duration, amplifier));
        }
        return new BuildupDefinitionsSyncPacket(definitions);
    }

    public static void handle(BuildupDefinitionsSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BuildupDefinitions.clear();
            for (Map.Entry<ResourceLocation, BuildupDefinition> entry : packet.definitions.entrySet()) {
                BuildupDefinitions.register(entry.getKey(), entry.getValue());
            }
            net.yaseruxd.scuffedsouls.ScuffedSouls.LOGGER.info(
                    "[ScuffedSouls] Received {} buildup definitions from server",
                    packet.definitions.size());
        });
        ctx.get().setPacketHandled(true);
    }
}