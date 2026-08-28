package net.yaseruxd.scuffedsouls.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class LychnusMusicClientHandler {
    private static LychnusMusicSoundInstance currentSound;

    private LychnusMusicClientHandler() {
    }

    public static void setMusic(boolean playing, BlockPos position) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            stopMusic(mc);
        } else if (!playing) {
            stopMusic(mc);
        } else {
            SoundEvent theme = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("scuffedsouls", "lychnus_theme"));
            if (theme != null) {
                if (currentSound != null && !currentSound.isStopped()) {
                    currentSound.setPosition(position);
                } else {
                    currentSound = new LychnusMusicSoundInstance(theme, position);
                    mc.getSoundManager().play(currentSound);
                }
            }
        }
    }

    public static void stopMusic(Minecraft mc) {
        if (currentSound != null) {
            mc.getSoundManager().stop(currentSound);
            currentSound = null;
        }
    }

    public static void onClientDisconnect() {
        Minecraft mc = Minecraft.getInstance();
        stopMusic(mc);
    }
}