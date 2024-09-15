package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static kogasastudio.ashihara.block.CuttingBoardBlock.FACING;
import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class CuttingBoardTER implements BlockEntityRenderer<CuttingBoardTE>, WithLevelRenderer<CuttingBoardTE>
{
    public CuttingBoardTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public void render(CuttingBoardTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer modelRenderer)
    {
        BlockEntity blockEntity = context.blockEntity();
        if (!(blockEntity instanceof CuttingBoardTE be) || be.getContent().isEmpty()) return;
        ItemStack stack = be.getContent();
        PoseStack matrixStackIn = context.poseStack();
        Level level = be.getLevel();

        int combinedLightIn = getPackedLight(be);

        if (!stack.isEmpty())
        {
            matrixStackIn.pushPose();
            resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_TRANSLUCENT, matrixStackIn);

            Direction facing = be.getBlockState().getValue(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof ItemNameBlockItem);

            matrixStackIn.translate(0.5, 0.5, 0.5);
            if (!isBlock)
            {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0f));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(facing.toYRot()));
            } else matrixStackIn.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
            matrixStackIn.translate(-0.5,-0.5,-0.5);

            float tHeight = isBlock ? 1.0f : 4f;
            matrixStackIn.translate(XTP(4f), XTP(tHeight), XTP(isBlock ? 4.0f : -2.75f));
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0) matrixStackIn.translate(XTP(0.0f), XTP(isBlock ? 16.0f : 0.0f), XTP(isBlock ? 0.0f : 1.2f));
                renderer.renderModelLists
                (
                    renderer.getModel(stack, level, null, 0),
                    stack,
                    combinedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    matrixStackIn,
                    context.consumerFunction().apply(AshiharaRenderTypes.CHUNK_ENTITY_TRANSLUCENT)
                );
            }
            matrixStackIn.popPose();
        }
    }
}