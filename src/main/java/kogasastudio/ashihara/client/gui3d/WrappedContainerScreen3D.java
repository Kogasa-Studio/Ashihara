package kogasastudio.ashihara.client.gui3d;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public abstract class WrappedContainerScreen3D<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
    public WrappedContainerScreen3D(T menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    public abstract ContainerScreen3D<?> getWrappedScreen();

    @Override
    protected void init()
    {
        super.init();
        this.getWrappedScreen().init(this.width, this.height);
    }

    @Override
    public @Nullable Slot getHoveredSlot()
    {
        return super.getHoveredSlot();
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput)
    {
        super.slotClicked(slot, slotId, buttonNum, containerInput);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        this.getWrappedScreen().extractRenderState(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        return this.getWrappedScreen().mouseScrolled(x, y, scrollX, scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        return  this.getWrappedScreen().mouseReleased(event) || super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy)
    {
        return this.getWrappedScreen().mouseDragged(event, dx, dy) || super.mouseDragged(event, dx, dy);
    }

    @Override
    public void onClose()
    {
        this.getWrappedScreen().onClose();
        super.onClose();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        return this.getWrappedScreen().mouseClicked(event, doubleClick) || super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        return this.getWrappedScreen().keyPressed(event) || super.keyPressed(event);
    }

    @Override
    protected void containerTick()
    {
        this.getWrappedScreen().tick();
        super.containerTick();
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        return this.getWrappedScreen().keyReleased(event) || super.keyReleased(event);
    }

    @Override
    public void mouseMoved(double x, double y)
    {
        this.getWrappedScreen().mouseMoved(x, y);
        super.mouseMoved(x, y);
    }
}
