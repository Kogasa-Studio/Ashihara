package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.client.models.CandleModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CandleTER implements BlockEntityRenderer<CandleTE> {
    private static final ResourceLocation tex = new ResourceLocation("ashihara:textures/block/candle_java.png");
    private final CandleModel candleSingle = new CandleModel();
    public CandleTER(BlockEntityRendererProvider.Context dispatcherIn) {

    }

    @Override
    public void render(CandleTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        VertexConsumer builder = bufferIn.getBuffer(RenderType.entitySolid(tex));

        for (double[] d : tileEntityIn.getPosList()) {
            double x = d[0];
            double z = d[1];
            double y = d[2];

            matrixStackIn.pushPose();
            GlStateManager._enableBlend();
            matrixStackIn.translate(x, y + 1.5d, z);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
            candleSingle.renderToBuffer(matrixStackIn, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
    }
}
