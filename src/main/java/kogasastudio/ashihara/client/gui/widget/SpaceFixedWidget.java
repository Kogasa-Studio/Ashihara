package kogasastudio.ashihara.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import software.bernie.geckolib.cache.object.GeoBone;

public abstract class SpaceFixedWidget extends AbstractWidget
{
    public GeoBone parent;
    public SpaceFixedWidget(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }
}
