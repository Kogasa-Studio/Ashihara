package kogasastudio.ashihara.client.render.geo.worldui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.Color;

public class PanelRenderer extends GeoObjectRenderer<UIPanelModel>
{
    private static final Color COLOR = Color.ofARGB(255, 255, 255, 255);

    public PanelRenderer(GeoModel<UIPanelModel> model)
    {
        super(model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, UIPanelModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        if (bone.getName().equals("main"))
        {
            animatable.hemming_corner.syncMain(bone);
            if (animatable.showEdgeHemming) animatable.hemming_edge.syncMain(bone);
            animatable.edge.syncMain(bone);

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().cameraEntity.yRotO));
            poseStack.translate(-0.5, -0.5, -0.5);

            poseStack.pushPose();
            poseStack.translate(0, 0.8 - 0.16/16f, 0);
            animatable.hemming_corner.render(poseStack, bufferSource, packedLight, packedOverlay);
            if (animatable.showEdgeHemming) animatable.hemming_edge.render(poseStack, bufferSource, packedLight, packedOverlay);
            animatable.edge.render(poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.popPose();
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
            );*/
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
            poseStack.popPose();
            animatable.hemming_corner.syncFrame(xStart*16, xEnd*16, yStart*16, yEnd*16);
            if (animatable.showEdgeHemming) animatable.hemming_edge.syncFrame(xStart*16, xEnd*16, yStart*16, yEnd*16, 0.5f);
            animatable.edge.syncFrame(xStart * 16, xEnd * 16, yStart * 16, yEnd * 16, bone.getScaleX(), bone.getScaleY());
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public Color getRenderColor(UIPanelModel animatable, float partialTick, int packedLight)
    {
        return COLOR;
    }
}
