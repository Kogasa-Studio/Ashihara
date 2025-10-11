package kogasastudio.ashihara.client.gui3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class Screen3D extends Screen
{
    public Screen3D(Component title)
    {
        super(title);
    }

    protected List<AbstractComponent> components;

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        for (AbstractComponent component : components)
        {
            component.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public static double mouseX()
    {
        return Minecraft.getInstance().mouseHandler.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth();
    }

    public static double mouseY()
    {
        return Minecraft.getInstance().mouseHandler.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight();
    }
}
