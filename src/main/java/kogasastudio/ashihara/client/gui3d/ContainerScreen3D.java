package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.components.ItemSlotComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public abstract class ContainerScreen3D<S extends WrappedContainerScreen3D<? extends AbstractContainerMenu>> extends Screen3D
{
    protected final S containerScreen;
    public static final int INV_BG_WIDTH = 176;
    public static final int INV_BG_HEIGHT = 90;
    public static final int BG_OFFSET_X = 0;
    public static final int BG_OFFSET_Y = 39;
    public static final float u1 = INV_BG_WIDTH / 256f, v1 = INV_BG_HEIGHT / 256f;
    public int x0, y0, x1, y1;

    protected ContainerScreen3D(S containerScreen, Component title)
    {
        super(title);
        this.containerScreen = containerScreen;
    }

    public S getContainerScreen()
    {
        return this.containerScreen;
    }

    public void sendSlotClick(int slotId, int button, ClickAction clickType)
    {
    }

    @Override
    public void init()
    {
        x0 = this.width / 2 - INV_BG_WIDTH / 2 + BG_OFFSET_X;
        x1 = this.width / 2 + INV_BG_WIDTH / 2 + BG_OFFSET_X;
        y0 = this.height / 2 - INV_BG_HEIGHT / 2 + BG_OFFSET_Y;
        y1 = this.height / 2 + INV_BG_HEIGHT / 2 + BG_OFFSET_Y;
        super.init();
    }

    /**
     * Optional bridge hook for custom hover pipelines (e.g. 3D ray picking).
     *
     * <p>Return null to keep vanilla container hover behavior for this query.
     */

    public @Nullable Slot findCustomHoveredSlot(double mouseX, double mouseY)
    {
        AbstractComponent hit = this.findTopHoverComponent(mouseX, mouseY);
        while (hit != null)
        {
            if (hit instanceof ItemSlotComponent itemSlot)
            {
                return itemSlot.getMenuSlot();
            }

            hit = hit.parent;
        }

        return null;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}

