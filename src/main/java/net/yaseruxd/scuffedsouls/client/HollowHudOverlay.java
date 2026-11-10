
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

@Mod.EventBusSubscriber(
        modid = "scuffedsouls",
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class HollowHudOverlay {

    private static final ResourceLocation HOLLOW_ICON =
            new ResourceLocation("scuffedsouls", "textures/hud/hollow.png");

    private static final int ICON_SIZE = 16;

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {

        // Bind to the crosshair overlay
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int level = ClientHollowData.get();
        if (level <= 0) return;

        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Original position — unchanged
        int x = (screenWidth / 2) - (ICON_SIZE / 2);
        int y = screenHeight - 70;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        graphics.blit(
                HOLLOW_ICON,
                x, y,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        RenderSystem.disableBlend();

        // Hollow counter below the icon
        String text = String.valueOf(level);

        int textWidth = mc.font.width(text);
        int textX = x + (ICON_SIZE - textWidth) / 2;
        int textY = y + ICON_SIZE + 2;

        graphics.drawString(mc.font, text, textX + 1, textY + 1, 0x000000, false);
        graphics.drawString(mc.font, text, textX, textY, 0xFFFFFF, false);
    }
}
