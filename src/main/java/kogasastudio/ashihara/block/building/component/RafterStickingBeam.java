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
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class RafterStickingBeam extends BuildingComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    private final VoxelShape SHAPE;

    public RafterStickingBeam
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

    public RafterStickingBeam
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, model, Shapes.box(0.25, 0, 0, 0.75, 0.5, 1), dropsIn);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r = direction.getAxis().equals(Direction.Axis.X) ? 90 : 0;

        double x = inBlockPos.x();
        double z = inBlockPos.z();
        double y = inBlockPos.y();

        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);

        int floor = y == 0 ? 0 : 2;

        Direction left = r == 0 ? Direction.WEST : Direction.NORTH;
        Direction right = left.getOpposite();

        Occupation leftOccupation = Occupation.getEdged(left).get(floor);
        Occupation rightOccupation = Occupation.getEdged(right).get(floor);
        Occupation centerOccupation = Occupation.CENTER_ALL.get(floor);

        if (direction.getAxis() == Direction.Axis.Z)
        {
            if (x >= 0 && x < XTP(4)) {x = -0.5;}
            else if (x >= XTP(4) && x < XTP(12)) {x = 0;}
            else {x = 0.5;}
            z = 0;
        }
        else
        {
            if (z >= 0 && z < XTP(4)) {z = -0.5;}
            else if (z >= XTP(4) && z < XTP(12)) {z = 0;}
            else {z = 0.5;}
            x = 0;
        }

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
            List.of(leftOccupation, centerOccupation, rightOccupation)
        );
    }
}
