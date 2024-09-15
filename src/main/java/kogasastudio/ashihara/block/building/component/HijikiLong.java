package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class HijikiLong extends BuildingComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    private final VoxelShape SHAPE;

    public HijikiLong
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape_a,
        List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape_a;
    }

    public HijikiLong
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, model, Shapes.box(0.375, -0.03125, 0, 0.625, 0.25, 1), dropsIn);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };

        double x = inBlockPos.x();
        double y = inBlockPos.y();
        double z = inBlockPos.z();

        double occx;
        double occz;

        int floor = (int) Math.clamp(Math.floor(y * 4), 0, 3);

        if (direction.getAxis() == Direction.Axis.Z)
        {
            if (x >= 0 && x < XTP(4)) {x = -0.5; occx = 0;}
            else if (x >= XTP(4) && x < XTP(12)) {x = 0; occx = 0.5;}
            else {x = 0.5; occx = 1;}
            z = 0;
            occz = direction == Direction.SOUTH ? 0 : 1;
        }
        else
        {
            if (z >= 0 && z < XTP(4)) {z = -0.5; occz = 0;}
            else if (z >= XTP(4) && z < XTP(12)) {z = 0; occz = 0.5;}
            else {z = 0.5; occz = 1;}
            x = 0;
            occx = direction == Direction.EAST ? 0 : 1;
        }

        y = XTP((float) (floor * 4));

        Occupation occupation = Occupation.mapPosition(occx, y, occz);
        Occupation otherSide = direction.getAxis() == Direction.Axis.Z ? Occupation.mapPosition(occx, y, 1 - occz) : Occupation.mapPosition(1 - occx, y, occz);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, x, y, z);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, r, 0,
            shape,
            MODEL,
            List.of(occupation, otherSide)
        );
    }
}
