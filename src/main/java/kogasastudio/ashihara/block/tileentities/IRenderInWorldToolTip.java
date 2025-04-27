package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.ToolTipController;
import net.minecraft.world.entity.player.Player;

public interface IRenderInWorldToolTip
{
    ToolTipController<?> getToolTipController();

    void reScale(Player player);
}
