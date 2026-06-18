package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.blockentity.CuttingBoardBE;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import static kogasastudio.ashihara.block.CuttingBoardBlock.FACING;
import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class CuttingBoardBER implements BlockEntityRenderer<CuttingBoardBE, BlockEntityRenderState>
{
    public CuttingBoardBER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public boolean shouldRender(CuttingBoardBE blockEntity, Vec3 cameraPosition)
    {
        return !blockEntity.getContent().isEmpty();
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        if (Minecraft.getInstance().level == null) return;
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(state.blockPos);
        if (!(blockEntity instanceof CuttingBoardBE be) || be.getContent().isEmpty()) return;
        ItemStack stack = be.getContent();

        if (!stack.isEmpty())
        {
            poseStack.pushPose();
            //resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_TRANSLUCENT, poseStack);

            Direction facing = be.getBlockState().getValue(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem;

            poseStack.translate(0.5, 0.5, 0.5);
            if (!isBlock)
            {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(facing.toYRot()));
            } else poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
            poseStack.translate(-0.5,-0.5,-0.5);

            float tHeight = isBlock ? 4.0f : 8f;
            poseStack.translate(XTP(8f), XTP(tHeight), XTP(isBlock ? 8.0f : 2.25f));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0) poseStack.translate(XTP(0.0f), XTP(isBlock ? 8.0f : 0.0f), XTP(isBlock ? 0.0f : 1.2f));
                poseStack.mulPose(isBlock ? Axis.YP.rotationDegrees(be.displayRot[i] * 360) : Axis.ZP.rotationDegrees(be.displayRot[i] * 360));
                RenderHelper.renderItem(poseStack, submitNodeCollector, stack, ItemDisplayContext.FIXED, Minecraft.getInstance().level, null, 42, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            }
            poseStack.popPose();
        }
    }
}