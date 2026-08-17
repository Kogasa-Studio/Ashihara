package kogasastudio.ashihara.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla {@code tryExtractRenderState} checks {@code BlockEntityType.isValid(getBlockState())}
 * before {@code BlockEntityRenderer.shouldRender}. For the thousands of pure chunk-buffer
 * {@code MultiBuiltBlockEntity}s in a dense build, that per-frame isValid call (and its
 * getBlockState) dominates the render-dispatcher profile. Hoisting the (cheap) shouldRender
 * gate to the head skips isValid entirely for anything that will not render this frame.
 */
@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher
{
    @Shadow
    private Vec3 cameraPos;

    @Inject
    (
        method = "tryExtractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/renderer/culling/Frustum;)Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;",
        at = @At("HEAD"),
        cancellable = true
    )
    private <E extends BlockEntity, S extends BlockEntityRenderState> void ashihara$skipNonRendering
    (
        E blockEntity,
        float partialTicks,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress,
        @Nullable Frustum frustum,
        CallbackInfoReturnable<S> cir
    )
    {
        BlockEntityRenderer<E, S> renderer = ((BlockEntityRenderDispatcher) (Object) this).getRenderer(blockEntity);
        if (renderer != null && !renderer.shouldRender(blockEntity, this.cameraPos))
        {
            cir.setReturnValue(null);
        }
    }
}
