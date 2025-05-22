package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.blockentity.PailBE;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PailTER implements BlockEntityRenderer<PailBE>
{
    public PailTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public void render(PailBE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
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
