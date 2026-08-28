package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ScuffedSouls.MODID);

    public static final RegistryObject<SoundEvent> LYCHNUS_THEME =
            SOUNDS.register("lychnus_theme", () ->
                    SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(ScuffedSouls.MODID, "lychnus_theme")
                    ));
}
