package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;

public class FermentationDisplayModel extends SimpleInternalControlGeoModel
{
    public static final String FLUID_LEVEL_SYNC = "fluid_level_sync";
    public static final String ITEM_FLOAT_SYNC = "item_float_sync";
    public static final String ITEM_FLOAT_IDLE = "item_float_idle";

    public static final RawAnimation ANIM_FLUID_LEVEL_SYNC = RawAnimation.begin().thenPlay(FLUID_LEVEL_SYNC);
    public static final RawAnimation ANIM_ITEM_FLOAT_SYNC = RawAnimation.begin().thenPlay(ITEM_FLOAT_SYNC);
    public static final RawAnimation ANIM_ITEM_FLOAT_IDLE = RawAnimation.begin().thenPlay(ITEM_FLOAT_IDLE);

    private float fluid_level_cur = 0;
    private float fluid_level_tgt = 0;

    public FermentationDisplayModel(String modelPrefix, String texturePrefix, String animationsPrefix)
    {
        super(modelPrefix, texturePrefix, animationsPrefix);
    }

    public float getLevelCurrent() {return fluid_level_cur;}
    public float getLevelTarget() {return fluid_level_tgt;}

    public void syncFluid(float cur, float tgt)
    {
        this.fluid_level_cur = cur;
        this.fluid_level_tgt = tgt;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add
        (
            new AnimationController<>(FLUID_LEVEL_SYNC, animatable -> PlayState.STOP)
            .triggerableAnim(FLUID_LEVEL_SYNC, ANIM_FLUID_LEVEL_SYNC)
        );
        controllers.add
        (
            new AnimationController<>(ITEM_FLOAT_SYNC, animatable -> PlayState.STOP)
            .triggerableAnim(ITEM_FLOAT_SYNC, ANIM_ITEM_FLOAT_SYNC)
            .additiveAnimations()
        );
        controllers.add
        (
            new AnimationController<>(ITEM_FLOAT_IDLE, animatable -> PlayState.STOP)
            .triggerableAnim(ITEM_FLOAT_IDLE, ANIM_ITEM_FLOAT_IDLE)
            .additiveAnimations()
        );
    }
}
