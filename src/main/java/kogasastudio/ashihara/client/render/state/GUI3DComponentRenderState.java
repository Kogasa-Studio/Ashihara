package kogasastudio.ashihara.client.render.state;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;


/**
 * 每次构造实例时携带自己的 dataMap，避免多组件 DataTicket 互相污染。
 */
public record GUI3DComponentRenderState(
        BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass,
        Map<DataTicket<?>, Object> dataMap
) implements GeoRenderState
{
    public static Map<DataTicket<?>, Object> newDataMap()
    {
        return new Reference2ObjectOpenHashMap<>();
    }

    /** 便捷构造器：自动创建新的空 dataMap。 */
    public GUI3DComponentRenderState(BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass)
    {
        this(submitRenderPass, newDataMap());
    }

    @Override
    public Map<DataTicket<?>, Object> getDataMap()
    {
        return dataMap;
    }

    public static <T extends GeoAnimatable, O, R extends GeoRenderState> GUI3DComponentRenderState of
    (
        T model,
        GeoObjectRenderer<T, O, R> renderer,
        @Nullable O relatedObject,
        CameraRenderState cameraState,
        int packedLight,
        float partialTick
    )
    {
        return new GUI3DComponentRenderState(
        (poseStack, submitNodeCollector) ->
            {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotation(45));
                poseStack.mulPose(Axis.YP.rotation(-45));
                poseStack.mulPose(Axis.ZP.rotation(0));
                poseStack.scale(64f, -64f, 64f);
                poseStack.translate(-0.5, -1, -0.5);
                renderer.performRenderPass
                (
                    model,
                    relatedObject,
                    poseStack,
                    submitNodeCollector,
                    cameraState,
                    packedLight,
                    partialTick
                );
                poseStack.popPose();
            }
        );
    }
}