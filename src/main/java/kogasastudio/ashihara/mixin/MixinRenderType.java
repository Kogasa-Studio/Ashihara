package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RenderType.class)
public class MixinRenderType
{
    @ModifyReturnValue(method = "chunkBufferLayers", at = @At(value = "RETURN"))
    private static List<RenderType> registerAshiharaSectionRenderTypes(List<RenderType> original)
    {
        List<RenderType> modified = Lists.newArrayList();
        for (RenderType renderType : original)
        {
            if (renderType.equals(RenderType.SOLID))
            {
                modified.addAll(AshiharaRenderTypes.ALL);
            }
            modified.add(renderType);
        }
        return modified;
    }
}
