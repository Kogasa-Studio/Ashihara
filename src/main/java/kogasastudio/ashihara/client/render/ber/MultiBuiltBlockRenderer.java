package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import org.joml.Matrix4f;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.client.render.quad.QuadBaker;
import kogasastudio.ashihara.event.ClientEventSubscribeHandler;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

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
                Matrix4f transform = buildQuadTransform(model);
                BlockStateModel bakedModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(model.model().id()));
                QuadBaker.renderModel(bakedModel, context.level(), context.pos(), tileEntityIn.getBlockState(), transform, matrixStackIn, context.consumerFunction());
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

    private static Matrix4f buildQuadTransform(ComponentStateDefinition def)
    {
        return new Matrix4f()
            .translate((float) def.inBlockPos().x, (float) def.inBlockPos().y, (float) def.inBlockPos().z)
            .translate(0.5f, 0, 0.5f)
            .rotateY((float) Math.toRadians(def.rotationY()))
            .rotateX((float) Math.toRadians(def.rotationX()))
            .rotateZ((float) Math.toRadians(def.rotationZ()))
            .translate(-0.5f, 0, -0.5f);
    }

    @Override
    public boolean shouldRender(MultiBuiltBlockEntity pBlockEntity, Vec3 pCameraPos)
    {
        return false;
    }
}
