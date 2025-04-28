package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.RenderSwitch;
import kogasastudio.ashihara.block.tileentities.util.ToolTipController;
import net.minecraft.world.entity.player.Player;

public interface IRenderInWorldToolTip extends IRenderSwitchable
{
    ToolTipController<?> getToolTipController();

    void reScale(Player player);

    @Override
    default RenderSwitch getSwitch()
    {
        return this.getToolTipController().getRenderSwitch();
    }

    @Override
    default void switchRender(Player player)
    {
        this.getToolTipController().getRenderSwitch().switchRender(player);
    }

    @Override
    default boolean checkRender()
    {
        return this.getToolTipController() != null && this.getToolTipController().getRenderSwitch().checkRender();
    }
}
