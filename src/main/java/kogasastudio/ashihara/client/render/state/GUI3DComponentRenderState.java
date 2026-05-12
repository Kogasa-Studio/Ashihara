package kogasastudio.ashihara.client.render.state;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;


public record GUI3DComponentRenderState(BiConsumer<PoseStack, SubmitNodeCollector> submitRenderPass) implements GeoRenderState
{
    private static final Map<DataTicket<?>, Object> dataMap = new Reference2ObjectOpenHashMap<>();

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
            renderer.performRenderPass
            (
                model,
                relatedObject,
                poseStack,
                submitNodeCollector,
                cameraState,
                packedLight,
                partialTick
            )
        );
    }
}