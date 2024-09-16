package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class Column extends BuildingComponent
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public Column(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = modelIn;
        this.SHAPE = Block.box(3d, 0d, 3d, 13d, 16d, 13d);
    }

    public Column(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, VoxelShape shapeIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = modelIn;
        this.SHAPE = shapeIn;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, 0, 0),
            0, 0, 0,
            SHAPE,
            MODEL,
            Occupation.CENTER_ALL
        );
    }
}
