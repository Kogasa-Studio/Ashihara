package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.renderer.base.GeoRenderState;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.render.geo.worldui.PanelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.util.GeckoLibUtil;
import com.geckolib.util.RenderUtil;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class UIPanelModel extends InternalControlGeoModel<UIPanelModel>
{
    public final Player player;
    public boolean showEdgeHemming = false;
    public boolean trackPlayerView = true;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final Identifier DEFAULT_MODEL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "geo/panel.geo.json");
    public static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public static final Identifier DEFAULT_BACKGROUND = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/nihil.png");

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

    public static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(Ashihara.MODID, "animations/gui_panel_general.animation.json");
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

        hemming_corner = new HemmingCornerModel(MODEL_GOLDEN_HEMMING_CORNER, TEX_GOLDEN_HEMMING);
        hemming_edge = new HemmingEdgeModel(MODEL_GOLDEN_HEMMING_EDGE, TEX_GOLDEN_HEMMING);
        edge = new EdgeModel(MODEL_LIGHT_WOOD_EDGE, TEX_LIGHT_WOOD_EDGE, 2f, 2f);
    }

    public UIPanelModel(Player player, String hemming_model_corner, String hemming_model_edge, String edge_model, String hemming_tex_corner, String hemming_tex_edge, String edge_tex)
    {
        this.player = player;

        hemming_corner = new HemmingCornerModel(hemming_model_corner, hemming_tex_corner);
        hemming_edge = new HemmingEdgeModel(hemming_model_edge, hemming_tex_edge);
        edge = new EdgeModel(edge_model, edge_tex, 2f, 2f);
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

    @SuppressWarnings("UnstableApiUsage")
    public float getScaleX()
    {
        Optional<GeoBone> boneOptional = this.getBakedModel(DEFAULT_MODEL).getBone("scale_sim");

        return boneOptional
                .map(bone -> bone.frameSnapshot)
                .map(BoneSnapshot::getScaleX)
                .orElse(0f);
    }

    @SuppressWarnings("UnstableApiUsage")
    public float getScaleY()
    {
        Optional<GeoBone> boneOptional = this.getBakedModel(DEFAULT_MODEL).getBone("scale_sim");

        return boneOptional
                .map(bone -> bone.frameSnapshot)
                .map(BoneSnapshot::getScaleY)
                .orElse(0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(INTRO, state -> PlayState.STOP).triggerableAnim(INTRO, ANIM_INTRO));
        controllers.add(new AnimationController<>(OUTRO, state -> PlayState.STOP).triggerableAnim(OUTRO, ANIM_OUTRO));
        controllers.add(new AnimationController<>(FLOAT, state -> PlayState.CONTINUE).triggerableAnim(FLOAT, ANIM_FLOAT));
        super.registerControllers(controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState)
    {
        return DEFAULT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState)
    {
        return DEFAULT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(UIPanelModel animatable)
    {
        return ANIMATION;
    }

    public Identifier getBackground()
    {
        return DEFAULT_BACKGROUND;
    }
}
