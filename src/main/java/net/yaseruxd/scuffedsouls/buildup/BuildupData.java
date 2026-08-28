package net.yaseruxd.scuffedsouls.buildup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BuildupData {

    private final Map<ResourceLocation, Float> amounts = new HashMap<>();

    public float get(ResourceLocation effectId) {
        return amounts.getOrDefault(effectId, 0.0F);
    }

    public void set(ResourceLocation effectId, float amount) {
        amounts.put(effectId, Math.max(0.0F, amount));
    }

    public void reset(ResourceLocation effectId) {
        amounts.put(effectId, 0.0F);
    }

    public void resetAll() {
        amounts.clear();
    }

    public Map<ResourceLocation, Float> getAll() {
        return amounts;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Float> entry : amounts.entrySet()) {
            tag.putFloat(entry.getKey().toString(), entry.getValue());
        }
        return tag;
    }

    public static BuildupData fromNBT(CompoundTag tag) {
        BuildupData data = new BuildupData();
        for (String key : tag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null) {
                data.set(id, tag.getFloat(key));
            }
        }
        return data;
    }
}