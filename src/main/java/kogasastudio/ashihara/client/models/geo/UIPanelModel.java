package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.render.geo.worldui.PanelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Optional;

public class UIPanelModel extends InternalControlGeoModel<UIPanelModel>
{
    public final Player player;
    public boolean showEdgeHemming = false;
    public boolean trackPlayerView = true;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/panel.geo.json");
    public static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public static final ResourceLocation DEFAULT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/nihil.png");

    public static final String MODEL_GOLDEN_HEMMING_CORNER = "geo/golden_hemming_corner.geo.json";
    public static final String MODEL_GOLDEN_HEMMING_EDGE = "geo/golden_hemming_edge.geo.json";
    public static final String MODEL_LIGHT_WOOD_EDGE = "geo/light_wood_edge.geo.json";

    public static final String TEX_GOLDEN_HEMMING = "textures/geo/golden_hemming.png";
    public static final String TEX_LIGHT_WOOD_EDGE = "textures/geo/wooden_edge.png";

    public static final int BG_WIDTH = 8;
    public static final int BG_HEIGHT = 8;

    public HemmingCornerModel hemming_corner;
    public HemmingEdgeModel hemming_edge;
    public EdgeModel edge;

    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animations/gui_panel_general.animation.json");
    public final PanelRenderer RENDERER = new PanelRenderer(this);

    public static final String INTRO = "intro";
    public static final String FLOAT = "float";
    public static final String OUTRO = "outro";

    public static final RawAnimation ANIM_INTRO = RawAnimation.begin().thenPlay(INTRO);
    public static final RawAnimation ANIM_FLOAT = RawAnimation.begin().thenLoop(FLOAT);
    public static final RawAnimation ANIM_OUTRO = RawAnimation.begin().thenPlay(OUTRO);

    public UIPanelModel(Player player)
    {
        this.player = player;

        hemming_corner = new HemmingCornerModel(MODEL_GOLDEN_HEMMING_CORNER, TEX_GOLDEN_HEMMING, player);
        hemming_edge = new HemmingEdgeModel(MODEL_GOLDEN_HEMMING_EDGE, TEX_GOLDEN_HEMMING, player);
        edge = new EdgeModel(MODEL_LIGHT_WOOD_EDGE, TEX_LIGHT_WOOD_EDGE, player, 2f, 2f);
    }

    public UIPanelModel(Player player, String hemming_model_corner, String hemming_model_edge, String edge_model, String hemming_tex_corner, String hemming_tex_edge, String edge_tex)
    {
        this.player = player;

        hemming_corner = new HemmingCornerModel(hemming_model_corner, hemming_tex_corner, player);
        hemming_edge = new HemmingEdgeModel(hemming_model_edge, hemming_tex_edge, player);
        edge = new EdgeModel(edge_model, edge_tex, player, 2f, 2f);
    }

    public UIPanelModel showHemmingEdge(boolean b)
    {
        this.showEdgeHemming = b;
        return this;
    }

    public UIPanelModel trackPlayerView(boolean b)
    {
        this.trackPlayerView = b;
        return this;
    }

    public float getScaleX()
    {
        Optional<GeoBone> boneOptional = this.getBone("scale_sim");
        return boneOptional.map(GeoBone::getScaleX).orElse(0f);
    }

    public float getScaleY()
    {
        Optional<GeoBone> boneOptional = this.getBone("scale_sim");
        return boneOptional.map(GeoBone::getScaleY).orElse(0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, INTRO, state -> PlayState.STOP).triggerableAnim(INTRO, ANIM_INTRO));
        controllers.add(new AnimationController<>(this, OUTRO, state -> PlayState.STOP).triggerableAnim(OUTRO, ANIM_OUTRO));
        controllers.add(new AnimationController<>(this, FLOAT, state -> PlayState.CONTINUE).triggerableAnim(FLOAT, ANIM_FLOAT));
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
        return RenderUtil.getCurrentTick();
    }

    @Override
    public ResourceLocation getModelResource(UIPanelModel animatable)
    {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(UIPanelModel animatable)
    {
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(UIPanelModel animatable)
    {
        return ANIMATION;
    }

    public ResourceLocation getBackground()
    {
        return DEFAULT_BACKGROUND;
    }
}
