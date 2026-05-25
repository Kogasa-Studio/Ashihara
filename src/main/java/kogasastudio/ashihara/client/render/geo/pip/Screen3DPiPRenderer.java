package kogasastudio.ashihara.client.render.geo.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.client.render.state.Screen3DPiPRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

import java.util.ArrayList;
import java.util.List;

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
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        FeatureRenderDispatcher featureRenderDispatcher = mc.gameRenderer.getFeatureRenderDispatcher();
        List<GUI3DComponentRenderState> translucent = new ArrayList<>();
        renderState.components().forEach(component ->
        {
            if (component.translucent()) translucent.add(component);
            else component.submitRenderPass().accept(poseStack, featureRenderDispatcher.getSubmitNodeStorage());
        });
        featureRenderDispatcher.renderAllFeatures();
        mc.renderBuffers().bufferSource().endBatch();
        translucent.forEach(component ->
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
