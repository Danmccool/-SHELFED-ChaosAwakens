package io.github.chaosawakens.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.client.entity.model.RubyBugEntityModel;
import io.github.chaosawakens.entity.RubyBugEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RubyBugEntityRender extends GeoEntityRenderer<RubyBugEntity> {

    private IRenderTypeBuffer renderTypeBuffer;
    private RubyBugEntity rubyBugEntity;

    public RubyBugEntityRender(EntityRendererManager renderManager) {
        super(renderManager, new RubyBugEntityModel());
        this.shadowSize = 0.4F;
    }

    @Override
    public void renderEarly(RubyBugEntity animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.rubyBugEntity = animatable;
        this.renderTypeBuffer = renderTypeBuffer;

        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(RubyBugEntity entity) {
        return new ResourceLocation(ChaosAwakens.MODID, "textures/entity/ruby_bug.png");
    }

    @Override
    public RenderType getRenderType(RubyBugEntity animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntitySmoothCutout(getTextureLocation(animatable));
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack matrixStack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.renderRecursively(bone, matrixStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}