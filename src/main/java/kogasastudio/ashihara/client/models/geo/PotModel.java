package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;

public class PotModel extends SimpleInternalControlGeoModel
{
    public static final String LID_STATE_CONTROLLER = "lid_state";
    public static final String LID_OPEN = "lid_open";
    public static final String LID_CLOSE = "lid_close";
    public static final String FLUID_LEVEL_SYNC = "fluid_level_sync";
    public static final String ITEM_FLOAT_SYNC = "item_float_sync";
    public static final String ITEM_FLOAT_IDLE = "item_float_idle";

    public static final RawAnimation ANIM_LID_OPEN = RawAnimation.begin().thenPlay(LID_OPEN);
    public static final RawAnimation ANIM_LID_CLOSE = RawAnimation.begin().thenPlay(LID_CLOSE);

    public static final RawAnimation ANIM_FLUID_LEVEL_SYNC = RawAnimation.begin().thenPlay(FLUID_LEVEL_SYNC);
    public static final RawAnimation ANIM_ITEM_FLOAT_SYNC = RawAnimation.begin().thenPlay(ITEM_FLOAT_SYNC);
    public static final RawAnimation ANIM_ITEM_FLOAT_IDLE = RawAnimation.begin().thenPlay(ITEM_FLOAT_IDLE);

    private float fluid_level_cur = 0;
    private float fluid_level_tgt = 0;

    public float getLevelCurrent() {return fluid_level_cur;}
    public float getLevelTarget() {return fluid_level_tgt;}

    public PotModel(String modelPrefix, String texturePrefix, String animationsPrefix)
    {
        super(modelPrefix, texturePrefix, animationsPrefix);
    }

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
            new AnimationController<>(LID_STATE_CONTROLLER, animatable -> PlayState.STOP)
            .triggerableAnim(LID_OPEN, ANIM_LID_OPEN)
            .triggerableAnim(LID_CLOSE, ANIM_LID_CLOSE)
        );
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
