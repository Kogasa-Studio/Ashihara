package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import net.minecraft.world.entity.player.Player;

public class PlayerInventoryModel extends SimpleInternalControlGeoModel
{
    public static final RawAnimation INTRO = RawAnimation.begin().thenPlay("intro");
    public PlayerInventoryModel(String modelPrefix, String texturePrefix, String animationsPrefix, Player player)
    {
        super(modelPrefix, texturePrefix, animationsPrefix, player);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>("intro", animatable -> PlayState.STOP).triggerableAnim("intro", INTRO));
    }
}
