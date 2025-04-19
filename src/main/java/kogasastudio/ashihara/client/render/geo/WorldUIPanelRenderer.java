package kogasastudio.ashihara.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
        if (bone.getName().equals("pos_sim"))
        {
            poseStack.pushPose();
            RenderHelper.blit
            (
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(animatable.getBackground())),
                bone.getPosX(),
                bone.getPosX() + UIPanelModel.BG_WIDTH / 16f,
                bone.getPosY(),
                bone.getPosY() + UIPanelModel.BG_HEIGHT / 16f,
                0, 0, animatable.progress, 0, animatable.progress,
                true,
                packedOverlay,
                packedLight
            );
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
