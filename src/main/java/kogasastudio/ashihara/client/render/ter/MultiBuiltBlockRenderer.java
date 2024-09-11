package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.client.models.baked.BakedModels;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class MultiBuiltBlockRenderer implements BlockEntityRenderer<MultiBuiltBlockEntity>, WithLevelRenderer<MultiBuiltBlockEntity>
{
    public MultiBuiltBlockRenderer(BlockEntityRendererProvider.Context dispatcherIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof MultiBuiltBlockEntity tileEntityIn) || tileEntityIn.getLevel() == null) return;
        PoseStack matrixStackIn = context.poseStack();
        RandomSource random = tileEntityIn.getLevel().getRandom();

        int combinedLightIn = getPackedLight(be);

        for (ComponentStateDefinition model : tileEntityIn.getComponents(MultiBuiltBlockEntity.OPCODE_READALL))
        {
            if (model.component().type.equals(BuildingComponents.Type.BAKED_MODEL))
            {
                matrixStackIn.pushPose();
                resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_SOLID, matrixStackIn);
                translateCoordinateSystem(tileEntityIn, matrixStackIn);

                matrixStackIn.translate(0.5, 0, 0.5);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(model.rotation()));
                matrixStackIn.translate(-0.5, 0, -0.5);

                Vec3 pos = model.inBlockPos();
                matrixStackIn.translate(pos.x, pos.y, pos.z);
                VertexConsumer consumer = context.consumerFunction().apply(AshiharaRenderTypes.CHUNK_ENTITY_SOLID);
                BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(model.model().toModelResourceLocation());
                if (!(bakedModel instanceof MultiPartBakedModel)) BakedModels.render(matrixStackIn.last(), consumer, bakedModel, AshiharaRenderTypes.CHUNK_ENTITY_SOLID, be.getBlockState(), combinedLightIn);
                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public void render(MultiBuiltBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
    }

    @Override
    public boolean shouldRender(MultiBuiltBlockEntity pBlockEntity, Vec3 pCameraPos)
    {
        return false;
    }

    private void translateCoordinateSystem(MultiBuiltBlockEntity be, PoseStack poseStack)
    {
        float rotation = switch (be.getBlockState().getValue(BaseMultiBuiltBlock.FACING))
        {
            case WEST -> 90;
            case SOUTH -> 180;
            case EAST -> 270;
            default -> 0;
        };
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5, 0, -0.5);
    }
}
