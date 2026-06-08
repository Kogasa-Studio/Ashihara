package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.world.entity.player.Player;

public final class EatingModeHelper
{
    private EatingModeHelper() {}

    public static boolean isEnabled(Player player)
    {
        if (player == null) return false;
        return player.getData(DataAttachmentTypes.EATING_MODE.get());
    }

    public static void setEnabled(Player player, boolean enabled)
    {
        if (player == null) return;
        player.setData(DataAttachmentTypes.EATING_MODE.get(), enabled);
    }
}