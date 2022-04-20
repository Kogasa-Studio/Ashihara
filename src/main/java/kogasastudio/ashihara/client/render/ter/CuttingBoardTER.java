package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import static kogasastudio.ashihara.block.BlockCuttingBoard.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.XTP;

public class CuttingBoardTER extends TileEntityRenderer<CuttingBoardTE>
{
    public CuttingBoardTER(TileEntityRendererDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    @Override
    public void render(CuttingBoardTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ItemStack stack = tileEntityIn.getContent();
        if (!stack.isEmpty())
        {
            matrixStackIn.push();
            Direction facing = tileEntityIn.getBlockState().get(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof BlockNamedItem);

            float tHeight = isBlock ? 3.0f : 1.5f;
            matrixStackIn.translate(XTP(8.0f), XTP(tHeight), XTP(8.0f));
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            if (!isBlock)
            {
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(facing.getHorizontalAngle()));
            }
            else matrixStackIn.rotate(Vector3f.YP.rotationDegrees(facing.getHorizontalAngle()));

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0 ) matrixStackIn.translate(XTP(0.0f), XTP(isBlock ? 8.0f : 0.0f), XTP(isBlock ? 0.0f : -1.2f));
                renderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            }
            matrixStackIn.pop();
        }
    }
}