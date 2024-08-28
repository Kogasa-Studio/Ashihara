package kogasastudio.ashihara.mixin.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.client.renderer.RenderType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static kogasastudio.ashihara.compat.sodium.AshiharaTerrainRenderPasses.ASHIHARA_PASSES;

@Pseudo
@Mixin(DefaultTerrainRenderPasses.class)
public class MixinDefaultTerrainRenderPasses
{
    @Mutable @Shadow @Final public static TerrainRenderPass[] ALL;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/DefaultTerrainRenderPasses;SOLID:Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;"))
    private static void onInit(CallbackInfo ci)
    {
        for (RenderType type : AshiharaRenderTypes.AFTER_ENTITIES)
        {
            ASHIHARA_PASSES.put(type, new TerrainRenderPass(type, false, false));
        }
    }

    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/DefaultTerrainRenderPasses;ALL:[Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;"))
    private static void onAddAll(TerrainRenderPass[] value, Operation<Void> original)
    {
        TerrainRenderPass[] passes = Arrays.copyOf(value, value.length + AshiharaRenderTypes.ALL.size());
        int i = 0;
        for (TerrainRenderPass pass : value)
        {
            passes[i] = pass;
            i += 1;
        }
        for (RenderType type : AshiharaRenderTypes.ALL)
        {
            passes[i] = ASHIHARA_PASSES.get(type);
            i += 1;
        }
        original.call((Object) passes);
    }
}
