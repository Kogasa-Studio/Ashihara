package kogasastudio.ashihara.client.render.state;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public class CommonGeoRenderState implements GeoRenderState
{
    private final Map<DataTicket<?>, Object> dataMap = new Reference2ObjectOpenHashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap()
    {
        return this.dataMap;
    }
}
