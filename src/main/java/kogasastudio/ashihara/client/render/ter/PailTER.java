package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.PailTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class PailTER extends TileEntityRenderer<PailTE>
{
    public PailTER(TileEntityRendererDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    @Override
    public void render(PailTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getTranslucentNoCrumbling());

        tileEntityIn.getTank().ifPresent
        (
            bucket ->
            {
                if (!bucket.isEmpty())
                {
                    FluidStack fluid = bucket.getFluid();
                    TextureAtlasSprite FLUID =
                        Minecraft.getInstance()
                        .getBlockRendererDispatcher()
                        .getBlockModelShapes()
                        .getTexture(fluid.getFluid().getDefaultState().getBlockState(), Objects.requireNonNull(tileEntityIn.getWorld()), tileEntityIn.getPos());
                    int color = fluid.getFluid().getAttributes().getColor();
                    float height = ((float) fluid.getAmount() / bucket.getCapacity()) * 0.5f;

                    matrixStackIn.push();
                    GlStateManager.enableBlend();

                    matrixStackIn.translate(0.0f, 0.0f, 0.0f);
                    Matrix4f wtf = matrixStackIn.getLast().getMatrix();
                    //主渲染
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.25f, FLUID.getMinU(), FLUID.getMinV(), combinedOverlayIn, color, 1.0f, combinedLightIn);
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.75f, FLUID.getMinU(), FLUID.getMaxV(), combinedOverlayIn, color, 1.0f, combinedLightIn);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.75f, FLUID.getMaxU(), FLUID.getMaxV(), combinedOverlayIn, color, 1.0f, combinedLightIn);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.25f, FLUID.getMaxU(), FLUID.getMinV(), combinedOverlayIn, color, 1.0f, combinedLightIn);
                    matrixStackIn.pop();
                }
            }
        );
    }
}
