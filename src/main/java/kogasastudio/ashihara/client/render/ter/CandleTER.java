package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.client.models.CandleModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class CandleTER extends TileEntityRenderer<CandleTE>
{
    public CandleTER(TileEntityRendererDispatcher dispatcherIn) {super(dispatcherIn);}

    private static final ResourceLocation tex = new ResourceLocation("ashihara:textures/block/candle_java.png");
    private final CandleModel candleSingle = new CandleModel();

    @Override
    public void render(CandleTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntitySolid(tex));

        for (double[] d : tileEntityIn.getPosList())
        {
            double x = d[0];
            double z = d[1];
            double y = d[2];

            matrixStackIn.push();
            GlStateManager.enableBlend();
            matrixStackIn.translate(x, y + 1.5d, z);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
            candleSingle.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
        }
    }
}
