package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.resources.ResourceLocation;

public class BuildupDefinition {

    private final float maxBuildup;
    private final float decayPerTick;
    private final float buildupPerApplication;
    private final int color;
    private final String effectId;
    private final int duration;
    private final int amplifier;

    public BuildupDefinition(
            float maxBuildup,
            float decayPerTick,
            float buildupPerApplication,
            int color,
            String effectId,
            int duration,
            int amplifier
    ) {
        this.maxBuildup = maxBuildup;
        this.decayPerTick = decayPerTick;
        this.buildupPerApplication = buildupPerApplication;
        this.color = color;
        this.effectId = effectId;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public float getMaxBuildup() { return maxBuildup; }
    public float getDecayPerTick() { return decayPerTick; }
    public float getBuildupPerApplication() { return buildupPerApplication; }
    public int getColor() { return color; }
    public String getEffectId() { return effectId; }
    public int getDuration() { return duration; }
    public int getAmplifier() { return amplifier; }
}