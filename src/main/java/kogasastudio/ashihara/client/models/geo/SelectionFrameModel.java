package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import net.minecraft.world.entity.player.Player;

public class SelectionFrameModel extends SimpleInternalControlGeoModel
{
    public double thickness = 1d;
    public OBB targetOBB;
    public static final String EXPAND = "expand";
    public static final RawAnimation ANIM_EXPAND = RawAnimation.begin().thenPlay(EXPAND);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(EXPAND, animatable -> PlayState.STOP).triggerableAnim(EXPAND, ANIM_EXPAND));
    }

    public SelectionFrameModel(String modelPrefix, String texturePrefix)
    {
        super(modelPrefix, texturePrefix, "gui/cubic_selection_frame");
    }

    public double getThickness()
    {
        return thickness;
    }

    public void setThickness(double thickness)
    {
        this.thickness = thickness;
    }

    public OBB getOBB()
    {
        return targetOBB;
    }

    public void setTarget(OBB targetOBB)
    {
        this.targetOBB = targetOBB;
    }
}
