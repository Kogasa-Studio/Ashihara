package kogasastudio.ashihara.client.gui.widget;

import software.bernie.geckolib.model.GeoModel;

public interface AnimatedWidget
{
    default GeoModel<?> getGeoModel() {return null;}
}
