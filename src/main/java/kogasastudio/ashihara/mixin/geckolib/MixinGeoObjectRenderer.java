package kogasastudio.ashihara.mixin.geckolib;

import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.utils.mixin.GeoRendererPoseSyncProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import com.geckolib.renderer.GeoObjectRenderer;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = GeoObjectRenderer.class, remap = false)
public class MixinGeoObjectRenderer implements GeoRendererPoseSyncProvider
{
    @Unique
    List<BoneTracer> ashihara_1_21$tracers = new ArrayList<>();

    @Override
    public List<BoneTracer> ashihara_1_21$getTracers()
    {
        return ashihara_1_21$tracers;
    }
}
