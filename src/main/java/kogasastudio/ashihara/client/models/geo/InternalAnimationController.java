package kogasastudio.ashihara.client.models.geo;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.Map;

public class InternalAnimationController<T extends GeoAnimatable> extends AnimationController<T>
{
    protected final Map<String, Animation> internalAnimations = new Object2ObjectOpenHashMap<>();
    protected Animation currentInternalAnimation;

    public InternalAnimationController(T animatable, String name, int transitionTickTime, AnimationStateHandler<T> animationHandler)
    {
        super(animatable, name, transitionTickTime, animationHandler);
    }

    public void triggerInternal(String name)
    {
    }

    @Override
    public void forceAnimationReset()
    {
        super.forceAnimationReset();
    }

    @Override
    protected PlayState handleAnimationState(AnimationState<T> state)
    {
        if (this.triggeredAnimation != null) {
            if (this.currentRawAnimation != this.triggeredAnimation)
                this.currentAnimation = null;

            setAnimation(this.triggeredAnimation);

            if (!hasAnimationFinished() && (!this.handlingTriggeredAnimations || this.stateHandler.handle(state) == PlayState.CONTINUE))
                return PlayState.CONTINUE;

            this.triggeredAnimation = null;
            this.needsAnimationReload = true;
        }

        return this.stateHandler.handle(state);
    }
}
