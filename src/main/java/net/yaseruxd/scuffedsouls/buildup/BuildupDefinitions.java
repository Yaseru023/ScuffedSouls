package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BuildupDefinitions {

    // Keyed by effect ID e.g. "minecraft:poison"
    private static final Map<ResourceLocation, BuildupDefinition> BY_EFFECT = new HashMap<>();

    public static void clear() {
        BY_EFFECT.clear();
    }

    public static void register(ResourceLocation effectId, BuildupDefinition definition) {
        BY_EFFECT.put(effectId, definition);
    }

    public static BuildupDefinition getByEffect(ResourceLocation effectId) {
        return BY_EFFECT.get(effectId);
    }

    public static Collection<BuildupDefinition> getAll() {
        return BY_EFFECT.values();
    }

    public static Map<ResourceLocation, BuildupDefinition> getAllByEffect() {
        return BY_EFFECT;
    }
}