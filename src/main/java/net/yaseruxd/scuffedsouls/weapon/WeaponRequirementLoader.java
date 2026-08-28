package net.yaseruxd.scuffedsouls.weapon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponRequirementLoader extends SimplePreparableReloadListener<List<WeaponRequirement>> {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new WeaponRequirementLoader());
    }

    @Override
    protected List<WeaponRequirement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<WeaponRequirement> loaded = new ArrayList<>();

        Map<ResourceLocation, Resource> resources = manager.listResources(
                "weapon_requirements",
                path -> path.getPath().endsWith(".json"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().open())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray requirements = json.getAsJsonArray("requirements");

                for (JsonElement element : requirements) {
                    JsonObject obj = element.getAsJsonObject();
                    ResourceLocation itemId = new ResourceLocation(obj.get("item").getAsString());

                    List<WeaponRequirement.StatRequirement> stats = new ArrayList<>();

                    for (JsonElement statEl : obj.getAsJsonArray("stats")) {
                        JsonObject statObj = statEl.getAsJsonObject();
                        stats.add(new WeaponRequirement.StatRequirement(
                                statObj.get("stat").getAsString(),
                                statObj.get("min").getAsDouble()
                        ));
                    }

                    loaded.add(new WeaponRequirement(itemId, stats));
                }
            } catch (Exception e) {
                System.err.println("[ScuffedSouls] Failed to load weapon requirements from "
                        + entry.getKey() + ": " + e.getMessage());
            }
        }

        return loaded;
    }

    @Override
    protected void apply(List<WeaponRequirement> data, ResourceManager manager, ProfilerFiller profiler) {
        // This runs on the main thread — safe to write to the manager here
        WeaponRequirementManager.clear();
        for (WeaponRequirement req : data) {
            WeaponRequirementManager.register(req);
        }
        System.out.println("[ScuffedSouls] Loaded " + data.size() + " weapon requirements.");
    }
}
