package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.renderer.base.GeoRenderState;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.item.Otsuchi;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerProxyModel extends GeoModel<PlayerProxyModel> implements SingletonGeoAnimatable
{
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "entity/player_proxy");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/empty.png");
    public static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(Ashihara.MODID, "entity/player_proxy");
    public final GeoObjectRenderer<PlayerProxyModel, PlayerModel, ?> RENDERER = new GeoObjectRenderer<>(this);

    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final Player player;

    public static final String TEST = "test_tekoki";
    public static final String OTSUCHI_HOLD = "otsuchi.hold";
    public static final String OTSUCHI_SMASH = "otsuchi.smash";

    public static final RawAnimation ANIM_TEST = RawAnimation.begin().thenPlay(TEST);
    public static final RawAnimation ANIM_OTSUCHI_HOLD = RawAnimation.begin().thenPlay(OTSUCHI_HOLD);
    public static final RawAnimation ANIM_OTSUCHI_SMASH = RawAnimation.begin().thenPlay(OTSUCHI_SMASH);

    public PlayerProxyModel(Player player)
    {
        this.player = player;
        //SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new ProxiedPlayerAnimationController(this, TEST, h -> PlayState.STOP, p -> p.getOffhandItem().is(Items.CUCUMBER.get())).triggerableAnim(TEST, ANIM_TEST).additiveAnimations());
        controllers.add(new ProxiedPlayerAnimationController(this, OTSUCHI_HOLD, h -> PlayState.STOP, p -> p.getMainHandItem().getItem() instanceof Otsuchi).triggerableAnim(OTSUCHI_HOLD, ANIM_OTSUCHI_HOLD).additiveAnimations());
        controllers.add(new ProxiedPlayerAnimationController(this, OTSUCHI_SMASH, h -> PlayState.STOP, p -> p.getMainHandItem().getItem() instanceof Otsuchi).triggerableAnim(OTSUCHI_SMASH, ANIM_OTSUCHI_SMASH).additiveAnimations());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
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
    public Identifier getAnimationResource(PlayerProxyModel animatable)
    {
        return ANIMATION;
    }

    public static class ProxiedPlayerAnimationController extends AnimationController<PlayerProxyModel>
    {
        public final Function<Player, Boolean> triggerCondition;
        public final BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> outroAnim;

        public static final BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> OUTRO_DEFAULT = (model, animationController) -> animationController.stopTriggeredAnimation();

        public ProxiedPlayerAnimationController
        (
            PlayerProxyModel animatable,
            String name,
            AnimationStateHandler<PlayerProxyModel> animationHandler,
            Function<Player, Boolean> triggerCondition,
            BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> outroAnim
        )
        {
            super(name, animationHandler);
            this.triggerCondition = triggerCondition;
            this.outroAnim = outroAnim;
        }

        public ProxiedPlayerAnimationController(PlayerProxyModel animatable, String name, AnimationStateHandler<PlayerProxyModel> animationHandler, Function<Player, Boolean> triggerCondition)
        {
            this(animatable, name, animationHandler, triggerCondition, OUTRO_DEFAULT);
        }

        public void outro(PlayerProxyModel model)
        {
            this.outroAnim.accept(model, this);
        }

        public boolean check(Player player)
        {
            return this.triggerCondition.apply(player);
        }
    }
}
