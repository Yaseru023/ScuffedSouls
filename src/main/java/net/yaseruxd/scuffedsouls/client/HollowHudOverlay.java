package net.yaseruxd.scuffedsouls.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HollowHudOverlay {

    private static final ResourceLocation WITHER_SKULL = new ResourceLocation(
            "minecraft", "textures/entity/wither/wither_skull.png");

    private static final int ICON_SIZE = 16;

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;

        int level = ClientHollowData.get();
        if (level <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = (screenWidth / 2) - 7;
        int y = (screenHeight / 2) - ICON_SIZE - 15;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.blit(WITHER_SKULL, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        RenderSystem.disableBlend();

        String text = String.valueOf(level);
        int textX = x + ICON_SIZE - mc.font.width(text) + 2;
        int textY = y + ICON_SIZE - 6;

        graphics.drawString(mc.font, text, textX + 1, textY + 1, 0x000000, false);
        graphics.drawString(mc.font, text, textX, textY, 0xFFFFFF, false);
    }
}