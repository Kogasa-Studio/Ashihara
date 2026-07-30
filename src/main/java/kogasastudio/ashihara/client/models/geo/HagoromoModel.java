package kogasastudio.ashihara.client.models.geo;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.item.armor.HagoromoItem;
import net.minecraft.resources.Identifier;

public class HagoromoModel extends GeoModel<HagoromoItem>
{
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "armor/hagoromo");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/armors/hagoromo.png");

    @Override
    public Identifier getModelResource(GeoRenderState state)
    {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state)
    {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(HagoromoItem animatable)
    {
        return HagoromoItem.ANIMATION;
    }
}