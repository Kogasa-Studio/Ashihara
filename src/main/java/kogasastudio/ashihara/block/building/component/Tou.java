package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class Tou extends BuildingComponent
{
    protected VoxelShape SHAPE;
    protected final BuildingComponentModelResourceLocation model;

    public Tou(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.model = modelIn;
        initShape();
    }

    protected void initShape()
    {
        this.SHAPE = Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.3125, 0.6875);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        double x = inBlockPos.x();
        double y = inBlockPos.y();
        double z = inBlockPos.z();

        Occupation occupation = Occupation.mapPosition(x, y, z);

        int xIndex = (int) Math.clamp(Math.floor(x * 3), 0, 2);
        int yIndex = (int) Math.clamp(Math.floor(y * 4), 0, 3);
        int zIndex = (int) Math.clamp(Math.floor(z * 3), 0, 2);

        x = (xIndex - 1) * 0.5;
        y = yIndex * 0.25;
        z = (zIndex - 1) * 0.5;

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.offsetShape(shape, x, y, z);


        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, 0, 0,
            shape,
            model,
            List.of(occupation)
        );
    }
}
