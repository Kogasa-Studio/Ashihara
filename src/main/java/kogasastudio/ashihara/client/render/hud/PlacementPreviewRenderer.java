package kogasastudio.ashihara.client.render.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.block.furniture.SnappedUseOnContext;
import kogasastudio.ashihara.utils.EatingModeHelper;
import kogasastudio.ashihara.utils.GridSnapHelper;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.event.ClientEventSubscribeHandler;
import kogasastudio.ashihara.item.block.BuildingComponentItem;
import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Thanks to ZhuRuoLing for debugging.
 */
public class PlacementPreviewRenderer
{
    private static ModelBlockRenderer blockRenderer;

    private static ModelBlockRenderer getBlockRenderer()
    {
        if (blockRenderer == null)
        {
            blockRenderer = new ModelBlockRenderer(false, false,
                Minecraft.getInstance().getBlockColors());
        }
        return blockRenderer;
    }

    public static void onRenderLevel(RenderLevelStageEvent.AfterLevel event)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        BuildingComponent component = getComponent(held);
        if (component == null) return;

        if (EatingModeHelper.isEnabled(player) && (!ContainerComponent.getContent(held).isEmpty() || !ContainerComponent.getFluidContent(held).isEmpty())) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS) return;

        Level level = mc.level;
        if (level == null) return;

        BlockPos pos = blockHit.getBlockPos();
        BlockState hitState = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        ComponentStateDefinition def;
        if (!player.isShiftKeyDown()
            && hitState.getBlock() instanceof BaseMultiBuiltBlock
            && be instanceof MultiBuiltBlockEntity mbe
            && coordsInRange(mbe, blockHit))
        {
            UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, held, blockHit);
            context = maybeWrapSnap(context, player);
            def = component.definite(mbe, context);
            if (def != null && canPlace(component, def, mbe))
            {
                renderPreview(event, mc, level, pos, mbe.getBlockState(), def, hitState);
                return;
            }
        }

        BlockPos placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (level.getBlockEntity(placePos) instanceof MultiBuiltBlockEntity mbe)
        {
            UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, held, blockHit);
            context = maybeWrapSnap(context, player);
            def = component.definite(mbe, context);
            if (def != null && canPlace(component, def, mbe)) renderPreview(event, mc, level, placePos, mbe.getBlockState(), def, hitState);
        }
        // Fallback: preview as new MBB placement.  BlockItem.place() creates
        // the block at clickedPos.relative(clickedFace), not at clickedPos.
        else if (level.getBlockState(placePos).canBeReplaced())
        {
            MultiBuiltBlockEntity phantom = makePhantom(placePos);
            UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, held, blockHit);
            context = maybeWrapSnap(context, player);
            def = component.definite(phantom, context);
            if (def != null) renderPreview(event, mc, level, placePos, phantom.getBlockState(), def, hitState);
        }
    }

    private static MultiBuiltBlockEntity makePhantom(BlockPos pos)
    {
        return new MultiBuiltBlockEntity(pos, Blocks.MULTI_BUILT_BLOCK.get().defaultBlockState());
    }

    /** Same logic as BaseMultiBuiltBlock.useItemOn: hit must be within block bounds. */
    private static boolean coordsInRange(MultiBuiltBlockEntity be, BlockHitResult hit)
    {
        return kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedX(
            hit.getDirection(), hit.getLocation().x - be.getBlockPos().getX(), 0, 1)
            && kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY(
            hit.getDirection(), hit.getLocation().y - be.getBlockPos().getY(), 0, 1)
            && kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedZ(
            hit.getDirection(), hit.getLocation().z - be.getBlockPos().getZ(), 0, 1);
    }

    private static boolean canPlace(BuildingComponent component, ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        if (component instanceof FurnitureComponent)
            return true;
        if (component instanceof AdditionalComponent)
        {
            for (ComponentStateDefinition existing : be.ADDITIONAL_COMPONENTS)
            {
                if (existing.occupation().hashCode() == def.occupation().hashCode() && existing.equals(def))
                    return false;
            }
            return true;
        }
        return Occupation.join(def.occupation(), be.occupationCache);
    }

    private static void renderPreview(RenderLevelStageEvent.AfterLevel event, Minecraft mc, Level level,
        BlockPos pos, BlockState state, ComponentStateDefinition def, BlockState hitState)
    {
        BlockStateModel model = mc.getModelManager()
            .getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(def.model().id()));

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        poseStack.mulPose(event.getModelViewMatrix());
        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());

        Vec3 p = def.inBlockPos();
        poseStack.translate(p.x, p.y, p.z);
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(def.rotationY()));
        poseStack.mulPose(Axis.XP.rotationDegrees(def.rotationX()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(def.rotationZ()));
        poseStack.translate(-0.5, 0, -0.5);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = new AlphaMaskConsumer(
            bufferSource.getBuffer(RenderTypes.translucentMovingBlock())
        );

        getBlockRenderer().tesselateBlock(
            (x, y, z, quad, instance) ->
                consumer.putBakedQuad(poseStack.last(), quad, instance),
            0, 0, 0,
            (BlockAndTintGetter) level, pos, state, model, 42L
        );

        poseStack.popPose();
        bufferSource.endBatch(RenderTypes.translucentMovingBlock());
    }

    @Nullable
    private static BuildingComponent getComponent(ItemStack stack)
    {
        if (stack.getItem() instanceof BuildingComponentItem bci)
            return bci.getComponent();
        if (stack.getItem() instanceof FurnitureComponentItem fci)
            return fci.getComponent();
        return null;
    }

    /** Wraps the context with grid snapping if the player has an active grid. */
    private static UseOnContext maybeWrapSnap(UseOnContext context, Player player)
    {
        if (player == null) return context;
        int gridStep = GridSnapHelper.getGridStep(player);
        if (gridStep == GridSnapHelper.GRID_NONE) return context;
        return new SnappedUseOnContext(context, gridStep, true);
    }

    private record AlphaMaskConsumer(VertexConsumer delegate) implements VertexConsumer
    {
        @Override
        public VertexConsumer setColor(int r, int g, int b, int a)
        {
            return delegate.setColor(r, g, b, a / 2);
        }

        @Override
        public VertexConsumer setColor(int rgba)
        {
            int a = (rgba >> 24) & 0xFF;
            return delegate.setColor((rgba & 0xFFFFFF) | ((a / 2) << 24));
        }

        @Override public VertexConsumer addVertex(float x, float y, float z) { return delegate.addVertex(x, y, z); }
        @Override public VertexConsumer setUv(float u, float v) { return delegate.setUv(u, v); }
        @Override public VertexConsumer setUv1(int u, int v) { return delegate.setUv1(u, v); }
        @Override public VertexConsumer setUv2(int u, int v) { return delegate.setUv2(u, v); }
        @Override public VertexConsumer setNormal(float x, float y, float z) { return delegate.setNormal(x, y, z); }
        @Override public VertexConsumer setLineWidth(float width) { return delegate.setLineWidth(width); }
    }
}
