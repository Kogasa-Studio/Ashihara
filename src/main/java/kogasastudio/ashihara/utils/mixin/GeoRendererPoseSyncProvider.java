package kogasastudio.ashihara.utils.mixin;

import kogasastudio.ashihara.client.gui3d.util.BoneTracer;

import java.util.List;

public interface GeoRendererPoseSyncProvider
{
    List<BoneTracer> ashihara_1_21$getTracers();

    default void ashihara_1_21$addTracer(BoneTracer tracer)
    {
        ashihara_1_21$getTracers().add(tracer);
    }
}
