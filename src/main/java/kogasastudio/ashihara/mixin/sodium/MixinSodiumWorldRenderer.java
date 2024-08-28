package kogasastudio.ashihara.mixin.sodium;

import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.compat.sodium.AshiharaTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(SodiumWorldRenderer.class)
public class MixinSodiumWorldRenderer
{
    @Shadow private RenderSectionManager renderSectionManager;

    @Inject(method = "drawChunkLayer", at = @At(value = "HEAD"))
    private void drawChunkLayer(RenderType renderLayer, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci)
    {
        if (AshiharaRenderTypes.ALL.contains(renderLayer))
        {
            renderSectionManager.renderLayer(matrices, AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(renderLayer), x, y, z);
        }
    }
}
