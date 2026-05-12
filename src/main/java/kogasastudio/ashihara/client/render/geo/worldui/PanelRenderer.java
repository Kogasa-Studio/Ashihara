package kogasastudio.ashihara.client.render.geo.worldui;

import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.Nullable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;

public class PanelRenderer extends GeoObjectRenderer<UIPanelModel, Void, GUI3DComponentRenderState>
{
    //private static final Color COLOR = Color.ofARGB(255, 255, 255, 255);
    private AdditionalRenderer additionalRenderer = null;

    public PanelRenderer(GeoModel<UIPanelModel> model)
    {
        super(model);
    }

    public void setupInformationRenderer(AdditionalRenderer infoRenderer)
    {
        this.additionalRenderer = infoRenderer;
    }

    public void render(PoseStack poseStack, UIPanelModel animatable, @Nullable MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, int packedLight, float partialTick)
    {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().getCameraEntity().yRotO));
        poseStack.translate(-0.5, -0.5, -0.5);

        poseStack.pushPose();
        poseStack.translate(0.8, -0.51f, 0);
        //super.render(poseStack, animatable, bufferSource, renderType, buffer, packedLight, partialTick);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.8, -0.51f, 0);
        animatable.hemming_corner.render(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        if (animatable.showEdgeHemming) animatable.hemming_edge.render(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        animatable.edge.render(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo renderPassInfo, BoneSnapshots snapshots)
    {
        snapshots.ifPresent("main", bone ->
        {
            bone.setRotY(-Minecraft.getInstance().getCameraEntity().getYRot());
            bone.setTranslation(0.8f, -0.51f, 0);
        });
    }

    /*@Override
    public void renderRecursively(PoseStack poseStack, UIPanelModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        if (bone.getName().equals("main"))
        {
            animatable.hemming_corner.syncMain(bone);
            if (animatable.showEdgeHemming) animatable.hemming_edge.syncMain(bone);
            animatable.edge.syncMain(bone);
        }
        if (bone.getName().equals("scale_sim"))
        {
            float xStart = bone.getPosX();
            float yStart = bone.getPosY();
            float xEnd = bone.getPosX() + (bone.getScaleX() / 16f);
            float yEnd = bone.getPosY() + (bone.getScaleY() / 16f);

            poseStack.pushPose();
            poseStack.translate(-0.5,-0.5,-0.5);
            /*RenderHelper.blit
            (
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(animatable.getBackground())),
                bone.getPosX(),
                bone.getPosX() + UIPanelModel.BG_WIDTH / 16f,
                bone.getPosY(),
                bone.getPosY() + UIPanelModel.BG_HEIGHT / 16f,
                0, 0, animatable.progress, 0, animatable.progress,
                packedOverlay,
                packedLight
            ); *
            RenderHelper.blitTiles
            (
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(animatable.getBackground())),
                xStart,
                xEnd,
                yStart,
                yEnd,
                0,
                UIPanelModel.BG_WIDTH,
                UIPanelModel.BG_HEIGHT,
                packedOverlay,
                packedLight
            );
            if (this.additionalRenderer != null)
            {
                poseStack.pushPose();
                poseStack.translate(xEnd + 0.5, yEnd + 0.5, 0.499);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                this.additionalRenderer.render(poseStack, animatable, bufferSource, renderType, buffer, packedLight, partialTick);
                poseStack.popPose();
            }
            poseStack.popPose();
            animatable.hemming_corner.syncFrame(xStart*16, xEnd*16, yStart*16, yEnd*16);
            if (animatable.showEdgeHemming) animatable.hemming_edge.syncFrame(xStart*16, xEnd*16, yStart*16, yEnd*16, 0.5f);
            animatable.edge.syncFrame(xStart * 16, xEnd * 16, yStart * 16, yEnd * 16, bone.getScaleX(), bone.getScaleY());
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }*/

    @FunctionalInterface
    public interface AdditionalRenderer
    {
        void render(PoseStack poseStack, UIPanelModel animatable, @Nullable MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, int packedLight, float partialTick);
    }
}
