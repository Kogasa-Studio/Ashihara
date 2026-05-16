package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.inventory.container.PotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public class PotScreen extends AbstractContainerScreen<PotMenu>
{
    public final PotScreen3D potScreen3D;
    public PotScreen(PotMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.potScreen3D = new PotScreen3D(this.menu, this.minecraft.player.getInventory(), Component.empty());
    }

    @Override
    protected void init()
    {
        super.init();
        this.potScreen3D.init(this.width, this.height);
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
        this.potScreen3D.extractRenderState(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        return this.potScreen3D.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy)
    {
        return this.potScreen3D.mouseDragged(event, dx, dy);
    }

    @Override
    public void onClose()
    {
        this.potScreen3D.onClose();
        super.onClose();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        return this.potScreen3D.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        return this.potScreen3D.keyPressed(event);
    }

    @Override
    protected void containerTick()
    {
        this.potScreen3D.tick();
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        return this.potScreen3D.keyReleased(event);
    }

    @Override
    public void mouseMoved(double x, double y)
    {
        this.potScreen3D.mouseMoved(x, y);
    }
}
