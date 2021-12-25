package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.MillTE;
import kogasastudio.ashihara.client.models.MillStoneModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import static kogasastudio.ashihara.block.BlockMill.FACING;

public class MillTER extends TileEntityRenderer<MillTE>
{
    public MillTER(TileEntityRendererDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    private static final ResourceLocation tex = new ResourceLocation("ashihara:textures/block/mill_stone.png");
    private final MillStoneModel millStone = new MillStoneModel();

    @Override
    public void render(MillTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        float facing = tileEntityIn.getBlockState().get(FACING).getHorizontalAngle();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(facing));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(tileEntityIn.getMillStoneRotation()));
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getEntitySolid(tex));
        millStone.render(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
    }
}
