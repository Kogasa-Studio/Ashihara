package kogasastudio.ashihara.client.gui.modules;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class Screen extends net.minecraft.client.gui.screens.Screen
{
    private List<ResourceLocation> BG_IMAGES = Lists.newArrayList();
    private List<Page> pages = Lists.newArrayList();
    private List<Sprite> children_constant = Lists.newArrayList();
    private final boolean isPauseScreen;

    private int c_width; //c means current
    private int c_height;

    protected Screen(Component pTitle, boolean pause)
    {
        super(pTitle);
        this.isPauseScreen = pause;
    }

    public int getStartX() {return (this.width - this.c_width) / 2;}
    public int getStartY() {return (this.height - this.c_height) / 2;}

    public int getEndX() {return this.getStartX() + this.c_width;}
    public int getEndY() {return this.getStartY() + this.c_height;}

    public int getWidth()
    {
        return c_width;
    }
    public int getHeight()
    {
        return c_height;
    }

    public void setWidth(int width)
    {
        this.c_width = width;
    }
    public void setHeight(int height)
    {
        this.c_height = height;
    }

    @Override
    public boolean isPauseScreen() {return this.isPauseScreen;}
}
