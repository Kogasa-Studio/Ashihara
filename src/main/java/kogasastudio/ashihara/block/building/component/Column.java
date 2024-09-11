package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class Column extends BuildingComponent
{
    public Column(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
    }

    @Override
    public ModelStateDefinition getModelDefinition(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        return new ModelStateDefinition
        (
            BuildingComponents.RED_THICK_COLUMN,
            new Vec3(0, 0, 0),
            0,
            Block.box(3d, 0d, 3d, 13d, 16d, 13d),
            AdditionalModels.RED_THICK_COLUMN,
            Occupation.CENTER_ALL
        );
    }
}
