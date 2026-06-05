package kogasastudio.ashihara.client.render.state;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.render.ber.MultiBuiltBlockRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Immutable snapshot produced by a furniture component's BER collector.
 * <p>
 * The {@link #renderFunction} lambda is invoked by MBER.submit() to let the
 * component submit its own geometry — no coupling between the generic
 * renderer and component-specific transform / model logic.
 */
public record FurnitureRenderState(Consumer<Holder> renderFunction)
{
    public record Holder(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, MultiBuiltBlockRenderer.MultiBuiltBlockRenderState state, @Nullable CameraRenderState camera) {}
}