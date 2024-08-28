package kogasastudio.ashihara.mixin.iris;

import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(WorldRenderingPhase.class)
public class MixinWorldRenderingPhase
{
    @Inject(method = "fromTerrainRenderType", at = @At(value = "HEAD"), cancellable = true)
    private static void initAshiharaSpecialRenderTypes(RenderType renderType, CallbackInfoReturnable<WorldRenderingPhase> cir)
    {
        if (AshiharaRenderTypes.AFTER_SKY.contains(renderType) || AshiharaRenderTypes.AFTER_ENTITIES.contains(renderType)) cir.setReturnValue(WorldRenderingPhase.TERRAIN_SOLID);
        else if (AshiharaRenderTypes.AFTER_BLOCK_ENTITIES.contains(renderType)) cir.setReturnValue(WorldRenderingPhase.TERRAIN_CUTOUT);
        else if (AshiharaRenderTypes.AFTER_PARTICLES.contains(renderType)) cir.setReturnValue(WorldRenderingPhase.TERRAIN_CUTOUT_MIPPED);
        else if (AshiharaRenderTypes.AFTER_WEATHER.contains(renderType)) cir.setReturnValue(WorldRenderingPhase.TERRAIN_TRANSLUCENT);
    }
}
