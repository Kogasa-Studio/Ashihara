package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.render.geo.worldui.PanelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class UIPanelModel extends InternalControlGeoModel<UIPanelModel>
{
    public final Player player;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/panel.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/highlight_outline.png");
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/nihil.png");
    public static final int BG_WIDTH = 8;
    public static final int BG_HEIGHT = 8;

    public HemmingModel hemming;
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

        hemming = new HemmingModel("geo/golden_hemming_corner.geo.json", "textures/geo/golden_hemming.png", player);
        edge = new EdgeModel("geo/light_wood_edge.geo.json", "textures/geo/wooden_edge.png", player, 2f, 2f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, INTRO, state -> PlayState.STOP).triggerableAnim(INTRO, ANIM_INTRO).triggerableAnim(OUTRO, ANIM_OUTRO));
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
