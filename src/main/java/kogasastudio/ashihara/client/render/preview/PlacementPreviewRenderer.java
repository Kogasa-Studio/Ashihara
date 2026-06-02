package kogasastudio.ashihara.client.render.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponentItem;
import kogasastudio.ashihara.event.ClientEventSubscribeHandler;
import kogasastudio.ashihara.item.block.BuildingComponentItem;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/**
 * Thanks to ZhuRuoLing for debugging.
 */
public class PlacementPreviewRenderer
{
    private static ModelBlockRenderer blockRenderer;

    public static void register()
    {
        NeoForge.EVENT_BUS.addListener(PlacementPreviewRenderer::onRenderLevel);
    }

    private static ModelBlockRenderer getBlockRenderer()
    {
        if (blockRenderer == null)
        {
            blockRenderer = new ModelBlockRenderer(false, false,
                Minecraft.getInstance().getBlockColors());
        }
        return blockRenderer;
    }

    private static void onRenderLevel(RenderLevelStageEvent.AfterLevel event)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        BuildingComponent component = getComponent(held);
        if (component == null) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS) return;

        Level level = mc.level;
        if (level == null) return;
        BlockPos pos = blockHit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof BaseMultiBuiltBlock)) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MultiBuiltBlockEntity mbe)) return;

        UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, held, blockHit);
        ComponentStateDefinition def = component.definite(mbe, context);
        if (def == null) return;

        BlockStateModel model = mc.getModelManager()
            .getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(def.model().id()));

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        poseStack.mulPose(event.getModelViewMatrix());
        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());
        translateCoordinateSystem(mbe, poseStack);
        translateCoordinateSystem(mbe, poseStack);

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
            (BlockAndTintGetter) level, pos, mbe.getBlockState(), model, 42L
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

    private static void translateCoordinateSystem(MultiBuiltBlockEntity be, PoseStack poseStack)
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
