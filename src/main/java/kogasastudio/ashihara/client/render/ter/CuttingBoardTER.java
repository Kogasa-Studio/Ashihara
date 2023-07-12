package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;

import static kogasastudio.ashihara.block.CuttingBoardBlock.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.XTP;

public class CuttingBoardTER implements BlockEntityRenderer<CuttingBoardTE>
{
    public CuttingBoardTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public void render(CuttingBoardTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ItemStack stack = tileEntityIn.getContent();
        if (!stack.isEmpty())
        {
            matrixStackIn.pushPose();
            Direction facing = tileEntityIn.getBlockState().getValue(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof ItemNameBlockItem);

            float tHeight = isBlock ? 3.0f : 1.5f;
            matrixStackIn.translate(XTP(8.0f), XTP(tHeight), XTP(8.0f));
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            if (!isBlock)
            {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0f));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(facing.toYRot()));
            } else matrixStackIn.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0) matrixStackIn.translate(XTP(0.0f), XTP(isBlock ? 8.0f : 0.0f), XTP(isBlock ? 0.0f : -1.2f));
                renderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, tileEntityIn.getLevel(), 0);
            }
            matrixStackIn.popPose();
        }
    }
}