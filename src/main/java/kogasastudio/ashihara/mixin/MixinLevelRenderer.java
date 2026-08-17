package kogasastudio.ashihara.mixin;

import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Redirects the per-frame {@code BlockEntityRenderDispatcher.tryExtractRenderState} calls
 * made from {@code LevelRenderer.extractVisibleBlockEntities}. Pure chunk-buffer
 * {@link MultiBuiltBlockEntity}s report {@code hasDynamicRender() == false} and are
 * short-circuited to a null render state, so they never enter the dispatcher at all:
 * no isValid/getBlockState, no shouldRender, and no entry in the per-frame render state list.
 */
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Redirect
    (
        method = "extractVisibleBlockEntities(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/culling/Frustum;)V",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryExtractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/renderer/culling/Frustum;)Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;"
        )
    )
    private BlockEntityRenderState ashihara$skipIfNotDynamic
    (
        BlockEntityRenderDispatcher dispatcher,
        BlockEntity blockEntity,
        float partialTicks,
        ModelFeatureRenderer.CrumblingOverlay breakProgress,
        Frustum frustum
    )
    {
        if (blockEntity instanceof MultiBuiltBlockEntity mbe && !mbe.hasDynamicRender())
        {
            return null;
        }
        return dispatcher.tryExtractRenderState(blockEntity, partialTicks, breakProgress, frustum);
    }
}
