package net.yaseruxd.scuffedsouls.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientBuildupData {

    private static final Map<ResourceLocation, Float> DATA = new HashMap<>();

    public static void set(ResourceLocation effectId, float amount) {
        DATA.put(effectId, amount);
    }

    public static float get(ResourceLocation effectId) {
        return DATA.getOrDefault(effectId, 0.0F);
    }

    public static Map<ResourceLocation, Float> getAll() {
        return Collections.unmodifiableMap(DATA);
    }

    public static void clear() {
        DATA.clear();
    }
}