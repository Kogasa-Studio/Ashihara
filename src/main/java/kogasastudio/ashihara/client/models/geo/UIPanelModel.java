package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.render.geo.WorldUIPanelRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class UIPanelModel extends InternalControlGeoModel<UIPanelModel>
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/panel.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/nihil.png");
    public static final int BG_WIDTH = 8;
    public static final int BG_HEIGHT = 8;
    public float progress = 0.9f;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;

    public SimpleInternalControlGeoModel corner_hemming = new SimpleInternalControlGeoModel("geo/golden_hemming_corner.geo.json", "textures/geo/golden_hemming.png");
    public SimpleInternalControlGeoModel edge_up = new SimpleInternalControlGeoModel("geo/light_wood_edge.geo.json", "textures/geo/wooden_edge.png");
    public SimpleInternalControlGeoModel edge_left = new SimpleInternalControlGeoModel("geo/light_wood_edge.geo.json", "textures/geo/wooden_edge.png");

    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animations/gui_panel_general.animation.json");
    public final WorldUIPanelRenderer RENDERER = new WorldUIPanelRenderer(this);

    public static final String INTRO = "intro";

    public static final RawAnimation ANIM_INTRO = RawAnimation.begin().thenPlay(INTRO);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, INTRO, state -> PlayState.STOP).triggerableAnim(INTRO, ANIM_INTRO));
        super.registerControllers(controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    @Override
    public double getTick(Object object)
    {
        return 0;
    }

    @Override
    public ResourceLocation getModelResource(UIPanelModel animatable)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(UIPanelModel animatable)
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(UIPanelModel animatable)
    {
        return ANIMATION;
    }

    public ResourceLocation getBackground()
    {
        return BACKGROUND;
    }
}
