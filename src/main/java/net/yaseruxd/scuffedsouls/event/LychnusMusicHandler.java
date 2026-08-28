package net.yaseruxd.scuffedsouls.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.yaseruxd.scuffedsouls.network.LychnusMusicPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LychnusMusicHandler {
    private static final int TICK_INTERVAL = 10; // 0.5 seconds
    private static final double PROXIMITY_RADIUS = 100.0D;
    private static final String LYCHNUS_ENTITY_ID = "the_faint_radiance:lychnus";

    private static final Set<UUID> MUSIC_PLAYING = new HashSet<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || server.getTickCount() % TICK_INTERVAL != 0) return;

        Set<UUID> onlinePlayers = new HashSet<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            onlinePlayers.add(uuid);

            // Dead players should never hear the boss music.
            if (!player.isAlive()) {
                if (MUSIC_PLAYING.remove(uuid)) {
                    sendMusicPacket(player, false, player.blockPosition());
                }
                continue;
            }

            ServerLevel level = player.serverLevel();
            AABB searchArea = player.getBoundingBox().inflate(PROXIMITY_RADIUS);

            Entity nearestLychnus = level.getEntities(player, searchArea,
                    LychnusMusicHandler::isLychnus)
                    .stream()
                    .filter(Entity::isAlive)
                    .min((a, b) -> Double.compare(
                            a.distanceToSqr(player), b.distanceToSqr(player)))
                    .orElse(null);

            boolean currentlyPlaying = MUSIC_PLAYING.contains(uuid);

            if (nearestLychnus != null) {
                sendMusicPacket(player, true, nearestLychnus.blockPosition());
                MUSIC_PLAYING.add(uuid);
            } else if (currentlyPlaying) {
                // Also handles leaving the dimension, leaving the 100-block range,
                // or the boss dying/disappearing.
                sendMusicPacket(player, false, player.blockPosition());
                MUSIC_PLAYING.remove(uuid);
            }
        }

        MUSIC_PLAYING.removeIf(uuid -> !onlinePlayers.contains(uuid));
    }

    private static boolean isLychnus(Entity entity) {
        var key = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null && key.toString().equals(LYCHNUS_ENTITY_ID);
    }

    private static void sendMusicPacket(ServerPlayer player, boolean playing, net.minecraft.core.BlockPos pos) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LychnusMusicPacket(playing, pos)
        );
    }
}
