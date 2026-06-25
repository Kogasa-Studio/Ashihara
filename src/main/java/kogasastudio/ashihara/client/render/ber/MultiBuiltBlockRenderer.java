package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.joml.Matrix4f;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.FurnitureProxyComponent;
import kogasastudio.ashihara.block.furniture.ICustomRender;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.client.render.ber.dispatch.FurnitureRenderDispatcher;
import kogasastudio.ashihara.client.render.state.FurnitureRenderState;
import kogasastudio.ashihara.client.render.quad.QuadBaker;
import kogasastudio.ashihara.event.ClientEventSubscribeHandler;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MultiBuiltBlockRenderer implements
    BlockEntityRenderer<MultiBuiltBlockEntity, MultiBuiltBlockRenderer.MultiBuiltBlockRenderState>,
    WithLevelRenderer<MultiBuiltBlockEntity>
{
    public MultiBuiltBlockRenderer(BlockEntityRendererProvider.Context d) {}

    // ── Chunk Buffer ──

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer modelRenderer)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof MultiBuiltBlockEntity tileEntityIn) || tileEntityIn.getLevel() == null) return;
        PoseStack matrixStackIn = context.poseStack();

        for (ComponentStateDefinition model : tileEntityIn.getComponents(MultiBuiltBlockEntity.OPCODE_READALL))
        {
            if (model.component().type.equals(BuildingComponents.Type.BAKED_MODEL) && !(model.component() instanceof FurnitureProxyComponent))
            {
                resetToBlock000(be, matrixStackIn);
                float scale = model.component() instanceof FurnitureComponent fc ? fc.modelScale() : 1f;
                Matrix4f aoTransform = buildQuadTransform(model, 1f);
                Matrix4f finalTransform = scale != 1f ? buildQuadTransform(model, scale) : aoTransform;
                BlockStateModel bakedModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(model.model().id()));
                QuadBaker.renderModel(bakedModel, context.level(), context.pos(), tileEntityIn.getBlockState(), aoTransform, finalTransform, matrixStackIn, context.consumerFunction());
            }
        }
    }

    /** Full transform with optional scale baked in. */
    private static Matrix4f buildQuadTransform(ComponentStateDefinition def, float scale)
    {
        return new Matrix4f()
            .translate((float) def.inBlockPos().x, (float) def.inBlockPos().y, (float) def.inBlockPos().z)
            .translate(0.5f, 0, 0.5f)
            .rotateY((float) Math.toRadians(def.rotationY()))
            .rotateX((float) Math.toRadians(def.rotationX()))
            .rotateZ((float) Math.toRadians(def.rotationZ()))
            .scale(scale)
            .translate(-0.5f, 0, -0.5f);
    }

    // ── BER gating ──

    @Override
    public boolean needRender(MultiBuiltBlockEntity be, Vec3 cameraPos)
    {
        for (ComponentStateDefinition def : be.getComponents(MultiBuiltBlockEntity.OPCODE_FURNITURE))
        {
            if (def.component() instanceof FurnitureComponent fc
                && fc instanceof ICustomRender cr && cr.doRender(be, def))
                return true;
        }
        return false;
    }

    // ── BER submit ──

    @Override public MultiBuiltBlockRenderState createRenderState() { return new MultiBuiltBlockRenderState(); }

    @Override
    public void submit(MultiBuiltBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        MultiBuiltBlockEntity be = state.be;
        if (be == null || be.getLevel() == null) return;

        for (ComponentStateDefinition def : be.getComponents(MultiBuiltBlockEntity.OPCODE_FURNITURE))
        {
            if (!(def.component() instanceof FurnitureComponent fc)) continue;
            if (!(fc instanceof ICustomRender cr) || !cr.doRender(be, def)) continue;

            FurnitureRenderState frs = FurnitureRenderDispatcher.dispatchCollect(fc, def, be);
            frs.renderFunction().accept(new FurnitureRenderState.Holder(poseStack, submitNodeCollector, state, camera));
        }
    }

    @Override
    public void extractRenderState(MultiBuiltBlockEntity blockEntity, MultiBuiltBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        state.be = blockEntity;
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }

    public static class MultiBuiltBlockRenderState extends BlockEntityRenderState
    {
        public MultiBuiltBlockEntity be;
    }
}