package kogasastudio.ashihara.mixin.sodium;

import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.compat.sodium.AshiharaMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultMaterials.class)
public class MixinDefaultMaterials
{
    @Inject(method = "forRenderLayer", at = @At(value = "HEAD"), cancellable = true)
    private static void forRenderLayer(RenderType layer, CallbackInfoReturnable<Material> cir)
    {
        if (AshiharaRenderTypes.ALL.contains(layer))
        {
            Material material = AshiharaMaterials.MATERIALS.get(layer);
            cir.setReturnValue(material);
        }
    }
}
