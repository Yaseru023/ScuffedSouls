package net.yaseruxd.scuffedsouls.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class LychnusMusicSoundInstance extends AbstractTickableSoundInstance {

    public LychnusMusicSoundInstance(SoundEvent sound, BlockPos position) {
        super(sound, SoundSource.RECORDS, RandomSource.create());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.85F;
        this.pitch = 1.0F;
        this.attenuation = Attenuation.LINEAR;
        this.setPosition(position);
    }

    public void setPosition(BlockPos position) {
        this.x = (double) position.getX() + 0.5D;
        this.y = (double) position.getY() + 0.5D;
        this.z = (double) position.getZ() + 0.5D;
    }

    @Override
    public void tick() {
        // Ticking logic can be added here if needed (e.g. tracking player distance/fading)
    }
}