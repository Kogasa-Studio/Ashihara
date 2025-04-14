package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TestButtonModel extends InternalControlGeoModel<TestButtonModel>
{
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/item/highlight_outline.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public final GeoObjectRenderer<TestButtonModel> RENDERER = new GeoObjectRenderer<>(this);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public double getTick(Object object)
    {
        return 0;
    }

    @Override
    public ResourceLocation getModelResource(TestButtonModel animatable)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TestButtonModel animatable)
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TestButtonModel animatable)
    {
        return null;
    }
}
