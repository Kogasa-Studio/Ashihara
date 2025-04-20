package kogasastudio.ashihara.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.Color;

public class WorldUIPanelRenderer extends GeoObjectRenderer<UIPanelModel>
{
    private static final Color COLOR = Color.ofARGB(255, 255, 255, 255);

    public WorldUIPanelRenderer(GeoModel<UIPanelModel> model)
    {
        super(model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, UIPanelModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        if (bone.getName().equals("scale_sim"))
        {
            animatable.edge_up.getBone("main").ifPresent
            (b ->
             {
                 b.setScaleX(2);
                 b.setScaleY(2);
                 b.setScaleZ(2);
             });
            animatable.edge_up.getBone("expansion_sim").ifPresent(b -> b.setScaleX(bone.getScaleX()));
            animatable.edge_left.getBone("main").ifPresent
            (b ->
             {
                 b.setScaleX(2);
                 b.setScaleY(2);
                 b.setScaleZ(2);
             });
            animatable.edge_left.getBone("expansion_sim").ifPresent(b -> b.setScaleX(bone.getScaleY()));

            float xStart = bone.getPosX();
            float yStart = bone.getPosY();
            float xEnd = bone.getPosX() + (bone.getScaleX() / 16f);
            float yEnd = bone.getPosY() + (bone.getScaleY() / 16f);

            poseStack.pushPose();
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

            poseStack.pushPose();
            poseStack.translate(xEnd, yEnd, 0);
            animatable.edge_up.render(poseStack, bufferSource, packedLight, packedOverlay);
            animatable.corner_hemming.render(poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xStart, yEnd, 0);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.translate(-0.5, -0.5, -0.5);
            animatable.edge_left.render(poseStack, bufferSource, packedLight, packedOverlay);
            animatable.corner_hemming.render(poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xStart, yStart, 0);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(-0.5, -0.5, -0.5);
            animatable.edge_up.render(poseStack, bufferSource, packedLight, packedOverlay);
            animatable.corner_hemming.render(poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xEnd, yStart, 0);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.ZP.rotationDegrees(270));
            poseStack.translate(-0.5, -0.5, -0.5);
            animatable.edge_left.render(poseStack, bufferSource, packedLight, packedOverlay);
            animatable.corner_hemming.render(poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public Color getRenderColor(UIPanelModel animatable, float partialTick, int packedLight)
    {
        return COLOR;
    }
}
