package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class MortarTER extends TileEntityRenderer<MortarTE>
{
    public MortarTER(TileEntityRendererDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    @Override
    public void render(MortarTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderHelper.renderLeveledFluidStack
        (
            tileEntityIn, matrixStackIn, bufferIn,
            combinedLightIn, combinedOverlayIn,
            0.1875f, 0.1875f, 0.1875f,
            0.8125f, 0.625f, 0.8125f,
            tileEntityIn.getWorld(), tileEntityIn.getPos()
        );
    }
}
