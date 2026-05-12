package kogasastudio.ashihara.client.render.geo;

import com.geckolib.constant.dataticket.DataTicket;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class GuideBookRenderer extends GeoObjectRenderer<GuideBookModel, Void, GuideBookRenderer.GuideBookRenderState>
{
    public GuideBookRenderer(GeoModel<GuideBookModel> model)
    {
        super(model);
    }

    @Override
    public GuideBookRenderState createRenderState(GuideBookModel animatable, @Nullable Void relatedObject)
    {
        return new GuideBookRenderState();
    }

    // Temporary compatibility shim for old call sites (GuideBookScreen) during stage-2 migration.
    public void render(PoseStack poseStack,
                       GuideBookModel animatable,
                       MultiBufferSource bufferSource,
                       RenderType renderType,
                       VertexConsumer buffer,
                       int packedLight,
                       float partialTick)
    {
        this.renderCompat(poseStack, animatable, bufferSource, renderType, buffer, packedLight, partialTick);
    }

    // Stage 2.1 fallback entrypoint used by GuideBookScreen while we migrate to full render-pass submission.
    public void renderCompat(PoseStack poseStack,
                             GuideBookModel animatable,
                             MultiBufferSource bufferSource,
                             RenderType renderType,
                             VertexConsumer buffer,
                             int packedLight,
                             float partialTick)
    {
        // Minimal stage2 bridge: reuse the same render-pass path as PiP so old call-sites are no longer no-op.
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        FeatureRenderDispatcher featureRenderDispatcher = mc.gameRenderer.getFeatureRenderDispatcher();
        CameraRenderState cameraRenderState = new CameraRenderState();

        this.performRenderPass
        (
                animatable,
                null,
                poseStack,
                featureRenderDispatcher.getSubmitNodeStorage(),
                cameraRenderState,
                packedLight,
                partialTick
        );
        featureRenderDispatcher.renderAllFeatures();
    }

    public static class GuideBookRenderState implements GeoRenderState
    {
        private final Map<DataTicket<?>, Object> dataMap = new Reference2ObjectOpenHashMap<>();

        @Override
        public Map<DataTicket<?>, Object> getDataMap()
        {
            return this.dataMap;
        }
    }
}
