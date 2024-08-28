package kogasastudio.ashihara.mixin.iris;

import kogasastudio.ashihara.compat.sodium.AshiharaTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SodiumPrograms.class)
public class MixinSodiumPrograms
{
    @Inject(method = "mapTerrainRenderPass", at = @At(value = "HEAD"), cancellable = true)
    private void mapTerrainRenderPass(TerrainRenderPass pass, CallbackInfoReturnable<SodiumPrograms.Pass> cir)
    {
        if (AshiharaTerrainRenderPasses.ASHIHARA_PASSES.containsValue(pass))
        {
            SodiumPrograms.Pass pass1 = ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? SodiumPrograms.Pass.SHADOW : SodiumPrograms.Pass.TERRAIN;
            cir.setReturnValue(pass1);
        }
    }
}
