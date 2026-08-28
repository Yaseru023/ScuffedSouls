package net.yaseruxd.scuffedsouls.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class SoulAnchorBlock extends Block {

    public SoulAnchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        UUID owner = SoulAnchorData.getOwner(level, pos);

        // Block interaction if not the owner
        if (owner != null && !owner.equals(player.getUUID())) {
            player.sendSystemMessage(Component.literal(
                    "§cThis soul does not belong to you..."));
            return InteractionResult.FAIL;
        }

        int storedXp = SoulAnchorData.getXp(level, pos);
        if (storedXp <= 0) {
            level.removeBlock(pos, false);
            return InteractionResult.SUCCESS;
        }

        // Give XP back
        player.giveExperiencePoints(storedXp);
        player.sendSystemMessage(Component.literal(
                "§6You reclaim your lost experience... §e(" + storedXp + " XP)"));

        // Spawn particles before removing the block
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            // Blue wispy soul smoke
            serverLevel.sendParticles(
                    ParticleTypes.SOUL,
                    x, y, z,
                    30,           // count
                    0.3, 0.3, 0.3, // spread
                    0.05          // speed
            );

            // Golden enchant sparkles
            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    x, y, z,
                    50,           // more sparkles for impact
                    0.4, 0.4, 0.4,
                    0.15          // faster so they shoot outward
            );
        }

        // Clear data and remove block
        SoulAnchorData.clearXp(level, pos);
        level.removeBlock(pos, false);

        return InteractionResult.SUCCESS;
    }
}