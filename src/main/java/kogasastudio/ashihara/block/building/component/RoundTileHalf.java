package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Function;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class RoundTileHalf extends RoundTile
{
    public RoundTileHalf
    (String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation model, VoxelShape shape, Function<Double, Integer> floorFuncIn, float xRotationIn, float yRotationIn, float yOffsetIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, model, shape, floorFuncIn, xRotationIn, yRotationIn, yOffsetIn, dropsIn);
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
        double x;
        double z;
        double y = inBlockPos.y();

        if (direction.getAxis().equals(Direction.Axis.Z))
        {
            x = 0;
            z = inBlockPos.z() <= XTP(8) ? XTP(-4) : XTP(4);
        }
        else
        {
            z = 0;
            x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
        }

        int floor = this.floorFunc.apply(y);
        y = XTP((float) (floor * 4));

        Occupation occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, x, y + yOffset, z);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y + yOffset, z),
            -xRotation, r - yRotation, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }
}
