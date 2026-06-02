package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
                Vec3 pos = model.inBlockPos();
                matrixStackIn.translate(pos.x, pos.y, pos.z);
                matrixStackIn.translate(0.5, 0, 0.5);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(model.rotationY()));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(model.rotationX()));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(model.rotationZ()));
                matrixStackIn.translate(-0.5, 0, -0.5);
                BlockStateModel bakedModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(model.model().id()));
                modelRenderer.renderBlockStateModel(bakedModel, matrixStackIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
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
}
