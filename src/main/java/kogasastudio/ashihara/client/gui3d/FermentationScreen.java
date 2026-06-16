package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.inventory.container.FermentationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FermentationScreen extends WrappedContainerScreen3D<FermentationMenu>
{
    public final FermentationScreen3D fermentationScreen;
    public FermentationScreen(FermentationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        fermentationScreen = new FermentationScreen3D(this, this.menu, this.minecraft.player.getInventory(), Component.empty());
    }

    @Override
    public ContainerScreen3D<?> getWrappedScreen()
    {
        return this.fermentationScreen;
    }
}
