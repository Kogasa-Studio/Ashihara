package kogasastudio.ashihara.client.gui.modules;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class Sprite extends AbstractWidget
{
    private ResourceLocation IMAGE;
    private int u;
    private int v;

    public Sprite(int pX, int pY, int pU, int pV, int pWidth, int pHeight, ResourceLocation pImage, Component pMessage)
    {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.u = pU;
        this.v = pV;
        this.IMAGE = pImage;
    }

    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick)
    {
        gui.blit(this.IMAGE, this.getX(), this.getY(), this.u, this.v, this.width, this.height);
        super.render(gui, pMouseX, pMouseY, pPartialTick);
    }

    public ResourceLocation getImage() {return IMAGE;}

    public void setImage(ResourceLocation ImageIn) {this.IMAGE = ImageIn;}
}