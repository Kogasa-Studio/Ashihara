package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import java.util.Optional;

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
    }

    public void render(BlockPos pos, BlockPos origin, AddSectionGeometryEvent.SectionRenderingContext context)
    {
        Optional<CuttingBoardTE> te = context.getRegion().getBlockEntity(pos, TERegistryHandler.CUTTING_BOARD_TE.get());
        if (te.isEmpty()) return;
        CuttingBoardTE tileEntityIn = te.get();
        ItemStack stack = tileEntityIn.getContent();
        PoseStack matrixStackIn = context.getPoseStack();
        Level level = tileEntityIn.getLevel();

        int combinedLightIn = level == null ? 15728880 : LevelRenderer.getLightColor(level, pos);

        if (!stack.isEmpty())
        {
            matrixStackIn.pushPose();

            int offsetX = pos.getX() - origin.getX();
            int offsetY = pos.getY() - origin.getY();
            int offsetZ = pos.getZ() - origin.getZ();

            Direction facing = tileEntityIn.getBlockState().getValue(FACING);
            boolean isBlock = stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof ItemNameBlockItem);

            float tHeight = isBlock ? 1.0f : 6.0f;
            matrixStackIn.translate(offsetX, offsetY, offsetZ);
            matrixStackIn.translate(XTP(12f), XTP(tHeight), XTP(isBlock ? 12f : 4f));
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            if (!isBlock)
            {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0f));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(facing.toYRot()));
            } else matrixStackIn.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0) matrixStackIn.translate(XTP(0.0f), XTP(isBlock ? 16.0f : 0.0f), XTP(isBlock ? 0.0f : -1.2f));
                renderer.renderModelLists
                (
                    renderer.getModel(stack, level, null, 0),
                    stack,
                    combinedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    matrixStackIn,
                    context.getOrCreateChunkBuffer(RenderType.cutoutMipped())
                );
            }
            matrixStackIn.popPose();
        }
    }
}