package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.client.gui.GuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ClientUtil
{
    public static void setGuideBookScreen(Player player)
    {
        Minecraft.getInstance().setScreen(new GuideBookScreen(Component.empty(), player));
    }
}
