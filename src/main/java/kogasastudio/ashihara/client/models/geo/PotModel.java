package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import net.minecraft.world.entity.player.Player;

public class PotModel extends SimpleInternalControlGeoModel
{
    public static final String LID_STATE_CONTROLLER = "lid_state";
    public static final String LID_OPEN = "lid_open";
    public static final String LID_CLOSE = "lid_close";

    public static final RawAnimation ANIM_LID_OPEN = RawAnimation.begin().thenPlay(LID_OPEN);
    public static final RawAnimation ANIM_LID_CLOSE = RawAnimation.begin().thenPlay(LID_CLOSE);

    public PotModel(String modelPrefix, String texturePrefix, String animationsPrefix, Player player)
    {
        super(modelPrefix, texturePrefix, animationsPrefix, player);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add
        (
            new AnimationController<>("lid_state", animatable -> PlayState.STOP)
            .triggerableAnim(LID_OPEN, ANIM_LID_OPEN)
            .triggerableAnim(LID_CLOSE, ANIM_LID_CLOSE)
        );
    }
}
