package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.blockentity.util.RenderSwitch;
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
