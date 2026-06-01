package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;

import java.util.List;
import java.util.function.Supplier;

public abstract class FurnitureComponent extends BuildingComponent
{
    public final FurnitureRendererType rendererType;

    public FurnitureComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        List<ItemStack> dropsIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        SoundType soundIn,
        FurnitureRendererType rendererTypeIn
    )
    {
        super(idIn, typeIn, dropsIn, materialIn, soundIn);
        this.rendererType = rendererTypeIn;
    }

    public FurnitureComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        FurnitureRendererType rendererTypeIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.rendererType = rendererTypeIn;
    }

    @Override
    public abstract ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context);
}
