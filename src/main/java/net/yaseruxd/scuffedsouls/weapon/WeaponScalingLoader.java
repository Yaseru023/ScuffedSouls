package net.yaseruxd.scuffedsouls.weapon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeaponScalingLoader extends SimpleJsonResourceReloadListener {

    public WeaponScalingLoader() {
        super(new Gson(), "weapon_scaling"); // data/<ns>/weapon_scaling/*.json
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager rm, ProfilerFiller profiler) {
        WeaponScalingManager.clear();

        for (Map.Entry<ResourceLocation, JsonElement> file : files.entrySet()) {
            try {
                JsonArray arr = GsonHelper.getAsJsonArray(file.getValue().getAsJsonObject(), "scaling");
                for (JsonElement el : arr) {
                    JsonObject obj = el.getAsJsonObject();
                    ResourceLocation item = new ResourceLocation(GsonHelper.getAsString(obj, "item"));

                    List<WeaponScaling.Entry> entries = new ArrayList<>();
                    for (JsonElement s : GsonHelper.getAsJsonArray(obj, "stats")) {
                        JsonObject so = s.getAsJsonObject();
                        entries.add(new WeaponScaling.Entry(
                                GsonHelper.getAsString(so, "stat").toLowerCase(),
                                GsonHelper.getAsDouble(so, "per_level"),
                                GsonHelper.getAsInt(so, "start", 0),
                                GsonHelper.getAsInt(so, "cap", 99)
                        ));
                    }
                    WeaponScalingManager.register(new WeaponScaling(item, entries));
                }
            } catch (Exception ex) {
                System.err.println("[ScuffedSouls] Failed to load weapon scaling " + file.getKey() + ": " + ex);
            }
        }
    }
}