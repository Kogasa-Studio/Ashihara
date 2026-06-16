package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.inventory.container.PotMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PotScreen extends WrappedContainerScreen3D<PotMenu>
{
    public final PotScreen3D potScreen3D;
    public PotScreen(PotMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.potScreen3D = new PotScreen3D(this, this.menu, this.minecraft.player.getInventory(), Component.empty());
    }

    @Override
    public Component getTitle()
    {
        return Component.empty();
    }

    @Override
    public ContainerScreen3D<?> getWrappedScreen()
    {
        return this.potScreen3D;
    }
}
