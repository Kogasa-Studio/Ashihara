package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public abstract class AbstractComponent
{
    public AbstractComponent(Screen p)
    {
        this.parent = p;
    }

    protected final Screen parent;
    protected List<AbstractComponent> children;
    protected InternalControlGeoModel<?> model;

    public Screen getParent()
    {
        return parent;
    }

    public List<AbstractComponent> getChildren()
    {
        return children;
    }

    public void setChildren(List<AbstractComponent> children)
    {
        this.children = children;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        for (AbstractComponent c : children)
        {
            c.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
