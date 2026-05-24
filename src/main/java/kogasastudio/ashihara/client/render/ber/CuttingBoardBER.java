package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.blockentity.CuttingBoardBE;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static kogasastudio.ashihara.block.CuttingBoardBlock.FACING;
import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class CuttingBoardBER implements BlockEntityRenderer<CuttingBoardBE, BlockEntityRenderState>, WithLevelRenderer<CuttingBoardBE>
{
    public CuttingBoardBER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer modelRenderer)
    {
        BlockEntity blockEntity = context.blockEntity();
        if (!(blockEntity instanceof CuttingBoardBE be) || be.getContent().isEmpty()) return;
        ItemStack stack = be.getContent();
        PoseStack matrixStackIn = context.poseStack();
        Level level = be.getLevel();

        int combinedLightIn = getPackedLight(be);

        if (!stack.isEmpty())
        {
            matrixStackIn.pushPose();
            //resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_TRANSLUCENT, matrixStackIn);

            Direction facing = be.getBlockState().getValue(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem;

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
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0) matrixStackIn.translate(XTP(0.0f), XTP(isBlock ? 16.0f : 0.0f), XTP(isBlock ? 0.0f : 1.2f));
                //modelRenderer.renderBlockStateModel(renderer.getModel(stack, level, null, 0), matrixStackIn, RenderType.translucent(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
                /*renderer.renderModelLists
                (
                    renderer.getModel(stack, level, null, 0),
                    stack,
                    combinedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    matrixStackIn,
                    context.consumerFunction().apply(AshiharaRenderTypes.CHUNK_ENTITY_TRANSLUCENT)
                );*/
            }
            matrixStackIn.popPose();
        }
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {

    }
}