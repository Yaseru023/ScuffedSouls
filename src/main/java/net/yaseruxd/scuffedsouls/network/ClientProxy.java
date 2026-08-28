package net.yaseruxd.scuffedsouls.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yaseruxd.scuffedsouls.client.gui.ClassSelectionScreen;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {

    public static void openClassScreen() {
        Minecraft.getInstance().setScreen(new ClassSelectionScreen());
    }
}
