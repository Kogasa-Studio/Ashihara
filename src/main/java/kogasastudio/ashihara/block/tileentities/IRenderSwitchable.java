package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.RenderSwitch;
import net.minecraft.world.entity.player.Player;

public interface IRenderSwitchable
{
    RenderSwitch getSwitch();

    default void switchRender(Player player)
    {
        getSwitch().switchRender(player);
    }

    default boolean checkRender()
    {
        return getSwitch().checkRender();
    }
}
