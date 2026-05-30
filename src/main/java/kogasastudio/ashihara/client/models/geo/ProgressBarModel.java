package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import kogasastudio.ashihara.helper.MathHelper;
import net.minecraft.world.entity.Entity;

public class ProgressBarModel extends SimpleInternalControlGeoModel
{
    public static final String PROGRESS = "progress";

    public static final RawAnimation ANIM_PROGRESS = RawAnimation.begin().thenPlay(PROGRESS);

    public ProgressBarModel(String modelPrefix, String texturePrefix)
    {
        super(modelPrefix, texturePrefix);
    }

    public ProgressBarModel(String modelPrefix, String texturePrefix, String animationsPrefix)
    {
        super(modelPrefix, texturePrefix, animationsPrefix);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(PROGRESS, s -> PlayState.STOP).triggerableAnim(PROGRESS, ANIM_PROGRESS));
    }

    public void init(Entity entity)
    {
        this.triggerAnim(entity, this.hashCode(), PROGRESS, PROGRESS);
        this.setAnimSpeed(PROGRESS, 0.00001);
    }

    public void setProgress(float progress)
    {
        double fixed = Math.clamp(progress, 0, 1);
        fixed = MathHelper.simplifyDouble(fixed, 5);
        this.setAnimTime(PROGRESS, fixed);
    }
}
