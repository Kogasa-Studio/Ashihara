package kogasastudio.ashihara.client.models.geo;

import com.geckolib.renderer.base.GeoRenderState;
import kogasastudio.ashihara.Ashihara;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.resources.Identifier;

public class TestButtonModel extends InternalControlGeoModel<TestButtonModel>
{
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "geo/item/highlight_outline.geo.json");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public final GeoObjectRenderer<TestButtonModel, ?, ?> RENDERER = new GeoObjectRenderer<>(this);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public Identifier getModelResource(GeoRenderState animatable)
    {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState animatable)
    {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(TestButtonModel animatable)
    {
        return null;
    }
}
