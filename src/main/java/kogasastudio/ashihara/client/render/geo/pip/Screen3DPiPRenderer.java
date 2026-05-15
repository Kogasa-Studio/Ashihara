package kogasastudio.ashihara.client.render.geo.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.render.state.Screen3DPiPRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

/** GeckoLib guidebook PiP renderer. */

public class Screen3DPiPRenderer extends PictureInPictureRenderer<Screen3DPiPRenderState>
{
    public Screen3DPiPRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
    }

    @Override
    public Class<Screen3DPiPRenderState> getRenderStateClass()
    {
        return Screen3DPiPRenderState.class;
    }

    @Override
    protected void renderToTexture(Screen3DPiPRenderState renderState, PoseStack poseStack)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);
        FeatureRenderDispatcher featureRenderDispatcher = mc.gameRenderer.getFeatureRenderDispatcher();
        renderState.components().forEach(component ->
        {
            component.submitRenderPass().accept(poseStack, featureRenderDispatcher.getSubmitNodeStorage());
        });
        featureRenderDispatcher.renderAllFeatures();
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel()
    {
        return "guidebook";
    }
}
