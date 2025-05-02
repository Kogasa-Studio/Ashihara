package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.RenderSwitch;
import kogasastudio.ashihara.block.tileentities.util.ToolTipController;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

public interface IRenderInWorldToolTip extends IRenderSwitchable
{
    /**
     * A ToolTipController must be constructed when this method is invoked.
     */
    void init(Player player);

    ToolTipController<?> getToolTipController();

    default void reScale(float scaleX, float scaleY, Player player)
    {
        getToolTipController().reScale(scaleX, scaleY, player);
    }

    @Override
    default RenderSwitch getSwitch()
    {
        return this.getToolTipController().getRenderSwitch();
    }

    @Override
    default void switchRender(Player player)
    {
        if (this.getToolTipController() == null) this.init(player);
        if (this.getToolTipController() == null)
        {
            throw new RuntimeException("Tooltip controller is still null after initialized, please check if you have constructed and returned the correct ToolTipController.");
        }
        this.getToolTipController().getRenderSwitch().switchRender(player);
    }

    @Override
    default boolean checkRender()
    {
        return this.getToolTipController() != null && this.getToolTipController().getRenderSwitch().checkRender();
    }
}
