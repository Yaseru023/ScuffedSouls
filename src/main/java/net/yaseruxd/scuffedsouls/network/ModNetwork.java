package net.yaseruxd.scuffedsouls.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ScuffedSouls.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {

        CHANNEL.registerMessage(
                packetId++,
                LychnusMusicPacket.class,
                LychnusMusicPacket::encode,
                LychnusMusicPacket::decode,
                LychnusMusicPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        CHANNEL.registerMessage(
                packetId++,
                WeaponRequirementSyncPacket.class,
                WeaponRequirementSyncPacket::encode,
                WeaponRequirementSyncPacket::decode,
                WeaponRequirementSyncPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        CHANNEL.registerMessage(
                packetId++,
                BuildupSyncPacket.class,
                BuildupSyncPacket::encode,
                BuildupSyncPacket::decode,
                BuildupSyncPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        CHANNEL.registerMessage(
                packetId++,
                BuildupDefinitionsSyncPacket.class,
                BuildupDefinitionsSyncPacket::encode,
                BuildupDefinitionsSyncPacket::decode,
                BuildupDefinitionsSyncPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        CHANNEL.registerMessage(
                packetId++,
                HollowSyncPacket.class,
                HollowSyncPacket::encode,
                HollowSyncPacket::decode,
                HollowSyncPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

    }
}