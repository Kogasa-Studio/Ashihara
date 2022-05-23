package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class PailTER extends BlockEntityRenderer<PailTE>
{
    public PailTER(BlockEntityRenderDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    @Override
    public void render(PailTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderHelper.renderLeveledFluidStack
        (
            tileEntityIn, matrixStackIn, bufferIn,
            combinedLightIn, combinedOverlayIn,
            0.25f, 0.09375f, 0.25f,
            0.75f, 0.5f, 0.75f,
            tileEntityIn.getLevel(), tileEntityIn.getBlockPos()
        );
    }
}
