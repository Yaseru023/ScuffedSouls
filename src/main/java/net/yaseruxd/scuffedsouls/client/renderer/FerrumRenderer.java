package net.yaseruxd.scuffedsouls.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.entity.FerrumEntity;

public class FerrumRenderer extends HumanoidMobRenderer<FerrumEntity, HumanoidModel<FerrumEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ScuffedSouls.MODID, "textures/entity/hollow_skin.png");

    public FerrumRenderer(EntityRendererProvider.Context context) {
        super(context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                0.5F
        );
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    protected void scale(FerrumEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(FerrumEntity entity) {
        return TEXTURE;
    }
}