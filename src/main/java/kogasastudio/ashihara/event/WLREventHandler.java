package kogasastudio.ashihara.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class WLREventHandler
{
    static <T> T cast(Object o)
    {
        return (T) o;
    }

    /**
     * Copied from io.github.tt432.eyelib.client.render.level.AddSectionGeometryEventListener.java under MIT License
     * @author TT432
     */
    public static final class WithLevelRendererAdditionalSectionRenderer implements AddSectionGeometryEvent.AdditionalSectionRenderer
    {
        private final BlockPos origin;

        public WithLevelRendererAdditionalSectionRenderer(AddSectionGeometryEvent event) {this.origin = event.getSectionOrigin().immutable();}

        @Override
        public void render(AddSectionGeometryEvent.SectionRenderingContext context)
        {
            BlockAndTintGetter region = context.getRegion();
            BlockPos posSource = this.origin;
            BlockPos posTarget = posSource.offset(15, 15, 15);

            for (BlockPos pos : BlockPos.betweenClosed(posSource, posTarget))
            {
                BlockEntity blockEntity = region.getBlockEntity(pos);

                if (blockEntity == null) continue;

                BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                BlockEntityRenderer<BlockEntity> renderer = dispatcher.getRenderer(blockEntity);
                Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

                if (renderer instanceof WithLevelRenderer<?> r /*&& r.needRender(cast(blockEntity), cameraPos)*/)
                {
                    r.renderStatic(new SectionRenderContext(region, pos, blockEntity, context.getPoseStack(), context::getOrCreateChunkBuffer));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWLR(AddSectionGeometryEvent event)
    {
        event.addRenderer(new WithLevelRendererAdditionalSectionRenderer(event));
    }

    @SubscribeEvent
    public static void onRenderSectionRenderType(RenderLevelStageEvent event)
    {
        List<RenderType> types = AshiharaRenderTypes.STAGE_TO_TYPE.get(event.getStage());
        if (types == null || types.isEmpty()) return;
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        double x = camera.getPosition().x;
        double y = camera.getPosition().y;
        double z = camera.getPosition().z;
        for (RenderType type : types)
        {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(camera.getRoll()));
            poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180));

            if (ModList.get().isLoaded("sodium"))
            {poseStack.translate(x, y, z);}
            poseStack.translate(-x, -y, -z);
            event.getLevelRenderer().renderSectionLayer(type, x, y, z, poseStack.last().pose(), event.getProjectionMatrix());
            event.getLevelRenderer().renderBuffers.bufferSource().endBatch(type);
            poseStack.popPose();
        }
    }
}
