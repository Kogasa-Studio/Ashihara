package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public abstract class BuildingComponent
{

    public final String id;
    public final BuildingComponents.Type type;
    public final List<ItemStack> drops;

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn)
    {
        this.id = idIn;
        this.type = typeIn;
        this.drops = dropsIn;
    }

    public abstract ModelStateDefinition getModelDefinition(MultiBuiltBlockEntity beIn, UseOnContext context);
}
