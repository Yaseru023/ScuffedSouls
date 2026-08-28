package net.yaseruxd.scuffedsouls.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Shares a portion of kill XP with nearby players, without requiring a party/group system.
 * Leverages ESR's own "Exp Exchange" config option (already enabled in your setup) — granting
 * vanilla XP points to nearby players lets ESR's existing vanilla-XP-to-custom-EXP conversion
 * handle the rest automatically, the same way it already does for the killer's own XP drop.
 *
 * The killer's own drop is untouched — this only adds bonus XP for OTHER nearby players.
 */
@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XpShareHandler {

    private static final double SHARE_RADIUS = 24.0;
    private static final float SHARE_PERCENT = 0.5f; // nearby players get 50% of the kill's XP each

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        Player killer = event.getAttackingPlayer();
        if (killer == null) return;
        if (!(killer.level() instanceof ServerLevel serverLevel)) return;

        int baseXp = event.getDroppedExperience();
        if (baseXp <= 0) return;

        int sharedAmount = Math.max(1, Math.round(baseXp * SHARE_PERCENT));

        AABB searchArea = event.getEntity().getBoundingBox().inflate(SHARE_RADIUS);
        List<ServerPlayer> nearby = serverLevel.getEntitiesOfClass(
                ServerPlayer.class, searchArea, p -> p != killer);

        for (ServerPlayer player : nearby) {
            player.giveExperiencePoints(sharedAmount);
        }
    }
}
