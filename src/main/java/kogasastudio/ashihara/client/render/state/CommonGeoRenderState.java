package kogasastudio.ashihara.client.render.state;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

public class CommonGeoRenderState implements GeoRenderState
{
    private final Map<DataTicket<?>, Object> dataMap = GUI3DComponentRenderState.newDataMap();

    @Override
    public Map<DataTicket<?>, Object> getDataMap()
    {
        return this.dataMap;
    }
}
