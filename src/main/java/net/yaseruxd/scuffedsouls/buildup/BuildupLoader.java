package net.yaseruxd.scuffedsouls.buildup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ScuffedSouls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildupLoader extends SimpleJsonResourceReloadListener {

    public BuildupLoader() {
        super(new Gson(), "buildup");
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new BuildupLoader());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects,
                         ResourceManager manager, ProfilerFiller profiler) {
        BuildupDefinitions.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            try {
                JsonObject json = entry.getValue().getAsJsonObject();

                float maxBuildup = json.get("max_buildup").getAsFloat();
                float decayPerTick = json.get("decay_per_tick").getAsFloat();
                float buildupPerApplication = json.get("buildup_per_application").getAsFloat();

                int color = 0xFFFFFFFF;
                if (json.has("color")) {
                    color = 0xFF000000 | Integer.parseInt(json.get("color").getAsString(), 16);
                }

                JsonObject activation = json.getAsJsonObject("activation");
                String effectId = activation.get("effect").getAsString();
                int duration = activation.has("duration") ? activation.get("duration").getAsInt() : 100;
                int amplifier = activation.has("amplifier") ? activation.get("amplifier").getAsInt() : 0;

                BuildupDefinition def = new BuildupDefinition(
                        maxBuildup, decayPerTick, buildupPerApplication,
                        color, effectId, duration, amplifier
                );

                BuildupDefinitions.register(new ResourceLocation(effectId), def);
                ScuffedSouls.LOGGER.info("[ScuffedSouls] Loaded buildup for effect: {}", effectId);

            } catch (Exception e) {
                ScuffedSouls.LOGGER.error("[ScuffedSouls] Failed to load buildup from {}: {}",
                        entry.getKey(), e.getMessage());
            }
        }
    }
}