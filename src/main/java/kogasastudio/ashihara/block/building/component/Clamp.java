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

import java.util.ArrayList;
import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class Clamp extends BuildingComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    protected VoxelShape SHAPE;

    public Clamp
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape;
    }

    public Clamp
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        List<ItemStack> dropsIn
    )
    {
        this(idIn, typeIn, model, null, dropsIn);
        initShape();
    }

    private void initShape()
    {
        this.SHAPE = Shapes.box(0, 0, 0.125, 1, 0.5, 0.875);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r = direction.getAxis().equals(Direction.Axis.X) ? 90 : 0;
        double y = inBlockPos.y();
        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);

        int floor = y == 0 ? 0 : 2;

        Direction left = r == 0 ? Direction.WEST : Direction.NORTH;
        Direction right = left.getOpposite();

        List<Occupation> occupations = new ArrayList<>();
        occupations.add(Occupation.getEdged(left).get(floor));
        occupations.add(Occupation.getEdged(right).get(floor));

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, r, 0,
            shape,
            MODEL,
            occupations
        );
    }
}
