package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.event.ClientEventSubscribeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;

public class MultiBuiltBlockRenderer implements BlockEntityRenderer<MultiBuiltBlockEntity, BlockEntityRenderState>, WithLevelRenderer<MultiBuiltBlockEntity>
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
                resetToBlock000(be, matrixStackIn);

                // 1) 先把方块内部坐标系转到 FACING（绕方块中心）
                translateCoordinateSystem(tileEntityIn, matrixStackIn);

                // 2) 再放到组件在方块内的位置（这个位移不能被组件自旋带走）
                Vec3 pos = model.inBlockPos();
                matrixStackIn.translate(pos.x, pos.y, pos.z);

                // 3) 最后做组件自身旋转（绕组件局部中心）
                matrixStackIn.translate(0.5, 0, 0.5);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(model.rotationY()));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(model.rotationX()));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(model.rotationZ()));
                matrixStackIn.translate(-0.5, 0, -0.5);

                BlockStateModel bakedModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(model.model().id()));
                modelRenderer.renderBlockStateModel(bakedModel, matrixStackIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
                    //if (!(bakedModel instanceof MultiPartBakedModel)) BakedModels.render(bakedModel, consumer, context.lighter(), matrixStackIn, tileEntityIn.getLevel(), tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), RenderType.cutoutMipped());

            }
        }
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return null;
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
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
