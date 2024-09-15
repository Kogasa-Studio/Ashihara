package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class MultiBuiltBlockRenderer implements BlockEntityRenderer<MultiBuiltBlockEntity>, WithLevelRenderer<MultiBuiltBlockEntity>
{
    public MultiBuiltBlockRenderer(BlockEntityRendererProvider.Context dispatcherIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer modelRenderer)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof MultiBuiltBlockEntity tileEntityIn) || tileEntityIn.getLevel() == null) return;
        PoseStack matrixStackIn = context.poseStack();

        for (ComponentStateDefinition model : tileEntityIn.getComponents(MultiBuiltBlockEntity.OPCODE_READALL))
        {
            if (model.component().type.equals(BuildingComponents.Type.BAKED_MODEL))
            {
                matrixStackIn.pushPose();
                translateCoordinateSystem(tileEntityIn, matrixStackIn);

                Vec3 pos = model.inBlockPos();
                matrixStackIn.translate(pos.x, pos.y, pos.z);

                matrixStackIn.translate(0.5, 0, 0.5);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(model.rotationY()));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(model.rotationX()));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(model.rotationZ()));
                matrixStackIn.translate(-0.5, 0, -0.5);

                BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(model.model().toModelResourceLocation());
                if (!(bakedModel instanceof MultiPartBakedModel)) modelRenderer.renderModel(bakedModel, matrixStackIn, RenderType.cutoutMipped(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
                    //if (!(bakedModel instanceof MultiPartBakedModel)) BakedModels.render(bakedModel, consumer, context.lighter(), matrixStackIn, tileEntityIn.getLevel(), tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), RenderType.cutoutMipped());
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
