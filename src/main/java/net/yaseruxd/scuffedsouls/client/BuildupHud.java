package net.yaseruxd.scuffedsouls.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.buildup.BuildupDefinition;
import net.yaseruxd.scuffedsouls.buildup.BuildupDefinitions;

import java.util.Map;

@Mod.EventBusSubscriber(
        modid = ScuffedSouls.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class BuildupHud {

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 4;  // thinner bar
    private static final int ICON_PADDING = 3; // gap between icon and bar
    private static final int SPACING = 14; // vertical spacing between bars
    private static final int Y_OFFSET = 20; // below crosshair

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        GuiGraphics gui = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int baseY = screenHeight / 2 + Y_OFFSET;

        int index = 0;

        for (Map.Entry<ResourceLocation, Float> entry : ClientBuildupData.getAll().entrySet()) {
            ResourceLocation effectId = entry.getKey();
            float amount = entry.getValue();

            if (amount <= 0.0F) continue;

            BuildupDefinition def = BuildupDefinitions.getByEffect(effectId);
            if (def == null) continue;

            float percentage = Math.min(1.0F, amount / def.getMaxBuildup());
            int color = def.getColor();
            int bgColor = 0xAA000000;
            int outlineColor = 0xAABBBBBB;

            int iconSize = BAR_HEIGHT;
            int totalWidth = iconSize + ICON_PADDING + BAR_WIDTH;

            // Center the whole thing horizontally
            int startX = centerX - totalWidth / 2;
            int y = baseY + index * SPACING;

            // --- Draw Icon ---
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            if (effect != null) {
                TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
                if (sprite != null) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    gui.blit(startX, y, 0, iconSize, iconSize, sprite);
                }
            }

            // --- Draw Bar ---
            int barX = startX + iconSize + ICON_PADDING;
            int barY = y;

            // Outline
            gui.fill(barX - 1, barY - 1, barX + BAR_WIDTH + 1, barY, outlineColor);               // top
            gui.fill(barX - 1, barY + BAR_HEIGHT, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, outlineColor); // bottom
            gui.fill(barX - 1, barY - 1, barX, barY + BAR_HEIGHT + 1, outlineColor);              // left
            gui.fill(barX + BAR_WIDTH, barY - 1, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, outlineColor); // right

            // Background
            gui.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, bgColor);

            // Filled portion with rounded right cap effect
            int fillWidth = (int)(BAR_WIDTH * percentage);
            if (fillWidth > 0) {
                gui.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, color);
            }

            index++;
        }
    }
}