package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerProxyModel extends GeoModel<PlayerProxyModel> implements SingletonGeoAnimatable
{
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/entity/player_proxy.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/empty.png");
    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animations/entity/player_proxy.animation.json");
    public final GeoObjectRenderer<PlayerProxyModel> RENDERER = new GeoObjectRenderer<>(this);

    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final Player player;

    public static final String TEST = "test_tekoki";

    public static final RawAnimation ANIM_TEST = RawAnimation.begin().thenPlay(TEST);

    public PlayerProxyModel(Player player)
    {
        this.player = player;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new ProxiedPlayerAnimationController(this, TEST, h -> PlayState.STOP, p -> p.getOffhandItem().is(ItemRegistryHandler.CUCUMBER.get())).triggerableAnim(TEST, ANIM_TEST));
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
    public ResourceLocation getModelResource(PlayerProxyModel animatable)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PlayerProxyModel animatable)
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PlayerProxyModel animatable)
    {
        return ANIMATION;
    }

    public class ProxiedPlayerAnimationController extends AnimationController<PlayerProxyModel>
    {
        public final Function<Player, Boolean> triggerCondition;
        public final BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> outroAnim;

        public static final BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> OUTRO_DEFAULT = (model, animationController) -> animationController.forceAnimationReset();

        public ProxiedPlayerAnimationController
        (
            PlayerProxyModel animatable,
            String name,
            AnimationStateHandler<PlayerProxyModel> animationHandler,
            Function<Player, Boolean> triggerCondition,
            BiConsumer<PlayerProxyModel, ProxiedPlayerAnimationController> outroAnim
        )
        {
            super(animatable, name, animationHandler);
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
