package kogasastudio.ashihara.client.gui.modules;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Page extends Sprite
{
    public Page(int pX, int pY, int pU, int pV, int pWidth, int pHeight, ResourceLocation pImage, Component pMessage)
    {
        super(pX, pY, pU, pV, pWidth, pHeight, pImage, pMessage);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {
    }
}