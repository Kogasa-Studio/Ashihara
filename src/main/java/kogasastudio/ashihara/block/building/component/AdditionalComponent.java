package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public abstract class AdditionalComponent extends BuildingComponent
{
    public AdditionalComponent(String idIn, BuildingComponents.Type typeIn, Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, materialIn, dropsIn);
    }
}
