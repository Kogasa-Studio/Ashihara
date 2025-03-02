package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GuideBookModel extends GeoModel<GuideBookModel> implements SingletonGeoAnimatable
{
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/item/guidebook.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/guidebook.png");
    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animations/item/guidebook.animation.json");
    public static final RawAnimation OPEN = RawAnimation.begin().thenPlay("use.open");

    public final GeoObjectRenderer<GuideBookModel> RENDERER = new GeoObjectRenderer<>(this);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "Open", animationState -> PlayState.STOP).triggerableAnim("open", OPEN));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public double getTick(Object o)
    {
        return 0;
    }

    @Override
    public @Nullable Animation getAnimation(GuideBookModel animatable, String name)
    {
        return super.getAnimation(animatable, name);
    }

    @Override
    public ResourceLocation getModelResource(GuideBookModel guideBookModel)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GuideBookModel guideBookModel)
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GuideBookModel guideBookModel)
    {
        return ANIMATION;
    }
}
