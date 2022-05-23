package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.helper.RenderHelper;
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
