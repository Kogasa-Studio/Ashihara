package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AdditionalComponent extends BuildingComponent
{
    public AdditionalComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
    }
}
