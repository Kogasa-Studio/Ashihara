package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import net.minecraft.world.entity.Entity;

public class BubbleModel extends SimpleInternalControlGeoModel
{
    public static final String INTRO = "intro";
    public static final String OUTRO = "outro";
    public static final String IDLE = "idle";

    public static final RawAnimation ANIM_INTRO = RawAnimation.begin().thenPlay(INTRO);
    public static final RawAnimation ANIM_OUTRO = RawAnimation.begin().thenPlay(OUTRO);
    public static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenPlay(IDLE);

    public BubbleModel(String modelPrefix, String texturePrefix, String animationsPrefix)
    {
        super(modelPrefix, texturePrefix, animationsPrefix);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(INTRO, a -> PlayState.STOP).triggerableAnim(INTRO, ANIM_INTRO).additiveAnimations());
        controllers.add(new AnimationController<>(OUTRO, a -> PlayState.STOP).triggerableAnim(OUTRO, ANIM_OUTRO).additiveAnimations());
        controllers.add(new AnimationController<>(IDLE, a -> PlayState.STOP).triggerableAnim(IDLE, ANIM_IDLE).additiveAnimations());
    }

    public void init(Entity entity, boolean instant)
    {
        this.triggerAnim(entity, this.hashCode(), INTRO, INTRO);
        this.setAnimTime(INTRO, instant ? Double.MAX_VALUE : 0);
        if (!instant) this.setAnimSpeed(INTRO, 0);
        this.triggerIdle(entity);
    }

    public void triggerIntro(Entity entity)
    {
        this.triggerAnim(entity, this.hashCode(), OUTRO, OUTRO);
        this.setAnimTime(OUTRO, 0);
        this.setAnimSpeed(OUTRO, 0);
        this.setAnimTime(INTRO, 0);
        this.setAnimSpeed(INTRO, 1);
        this.triggerAnim(entity, this.hashCode(), INTRO, INTRO);
    }

    public void triggerOutro(Entity entity)
    {
        this.setAnimTime(OUTRO, 0);
        this.setAnimSpeed(OUTRO, 1);
        this.triggerAnim(entity, this.hashCode(), OUTRO, OUTRO);
    }

    public void triggerIdle(Entity entity)
    {
        this.triggerAnim(entity, this.hashCode(), IDLE, IDLE);
    }
}
