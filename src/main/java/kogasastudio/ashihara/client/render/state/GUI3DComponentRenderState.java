package kogasastudio.ashihara.client.render.state;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public record GUI3DComponentRenderState(
        BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass,
        Map<DataTicket<?>, Object> dataMap, boolean translucent
) implements GeoRenderState
{
    public static Map<DataTicket<?>, Object> newDataMap()
    {
        return new Reference2ObjectOpenHashMap<>();
    }

    public GUI3DComponentRenderState(BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass, boolean translucent)
    {
        this(submitRenderPass, newDataMap(), translucent);
    }

    public GUI3DComponentRenderState(BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass)
    {
        this(submitRenderPass, newDataMap(), false);
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
        Matrix4f presetTransform,
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
                poseStack.last().pose().mul(presetTransform);
                poseStack.last().normal().identity();
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
        return of(model, renderer, new Matrix4f(), relatedObject, cameraState, packedLight, partialTick);
    }
}