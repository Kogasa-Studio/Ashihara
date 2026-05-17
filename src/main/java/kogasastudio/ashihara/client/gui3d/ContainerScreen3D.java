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

