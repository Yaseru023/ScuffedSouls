package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.network.BuildupDefinitionsSyncPacket;
import net.yaseruxd.scuffedsouls.network.BuildupSyncPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildupEventHandler {

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (BuildupManager.isApplying(player.getUUID())) return;

        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(
                event.getEffectInstance().getEffect());
        if (effectId == null) return;

        BuildupDefinition def = BuildupDefinitions.getByEffect(effectId);
        if (def == null) return;

        event.setResult(Event.Result.DENY);
        BuildupManager.handleIncomingEffect(player, effectId, def);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()) {

            BuildupData data = BuildupStorage.get(player);

            data.resetAll();

            BuildupStorage.save(player, data);

            if (player instanceof ServerPlayer serverPlayer) {

                for (ResourceLocation buildupId :
                        BuildupDefinitions.getAllByEffect().keySet()) {

                    ModNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new BuildupSyncPacket(
                                    buildupId,
                                    0.0F
                            )
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        // Sync buildup definitions to client
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new BuildupDefinitionsSyncPacket(BuildupDefinitions.getAllByEffect())
        );
    }
}