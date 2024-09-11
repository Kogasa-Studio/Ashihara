package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class Beam extends BuildingComponent implements Connectable
{
    private final BuildingComponentModelResourceLocation NONE_CONNECTED;
    private final BuildingComponentModelResourceLocation LEFT_CONNECTED;
    private final BuildingComponentModelResourceLocation RIGHT_CONNECTED;
    private final BuildingComponentModelResourceLocation BOTH_CONNECTED;

    private VoxelShape SHAPE_NONE_CONNECTED;
    private VoxelShape SHAPE_LEFT_CONNECTED;
    private VoxelShape SHAPE_RIGHT_CONNECTED;
    private VoxelShape SHAPE_BOTH_CONNECTED;

    public Beam(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation n, BuildingComponentModelResourceLocation l, BuildingComponentModelResourceLocation r, BuildingComponentModelResourceLocation a, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.NONE_CONNECTED = n;
        this.LEFT_CONNECTED = l;
        this.RIGHT_CONNECTED = r;
        this.BOTH_CONNECTED = a;
        initShape();
    }

    protected void initShape()
    {
        this.SHAPE_NONE_CONNECTED = Shapes.box(0, 0, 0.3125, 1, 0.5, 0.6875);
        this.SHAPE_LEFT_CONNECTED = Shapes.box(0, 0, 0.3125, 1.5, 0.5, 0.6875);
        this.SHAPE_RIGHT_CONNECTED = Shapes.box(-0.5, 0, 0.3125, 1, 0.5, 0.6875);
        this.SHAPE_BOTH_CONNECTED = Shapes.box(-0.5, 0, 0.3125, 1.5, 0.5, 0.6875);
    }

    @Override
    public ModelStateDefinition getModelDefinition(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 clickLocation = context.getClickLocation();
        clickLocation = beIn.transformVec3(clickLocation);

        float r = direction.getAxis().equals(Direction.Axis.X) ? 0 : 90;
        double y = clickLocation.y() - context.getClickedPos().getY();
        if (context.getClickedFace().equals(Direction.UP))
        {
            y = (y >= 0 && y < XTP(8)) ? 0 : XTP(8);
        }
        else y = (y > 0 && y <= XTP(8)) ? 0 : XTP(8);

        int floor = y == 0 ? 0 : 2;
        Direction left = r == 0 ? Direction.WEST : Direction.NORTH;
        Direction right = left.getOpposite();

        Occupation leftOccupation = Occupation.getEdged(left).get(floor);
        Occupation rightOccupation = Occupation.getEdged(right).get(floor);
        Occupation centerOccupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE_NONE_CONNECTED;
        if (r == 90) shape = ShapeHelper.rotateShape(shape, 90);
        if (y == XTP(8)) shape = ShapeHelper.offsetShape(shape, 0, 0.5, 0);

        ModelStateDefinition init = new ModelStateDefinition
        (
            BuildingComponents.RED_BEAM,
            new Vec3(0, y, 0),
            r,
            shape,
            NONE_CONNECTED,
            List.of(leftOccupation, centerOccupation, rightOccupation)
        );
        return applyConnection(beIn, init);
    }

    @Override
    public ModelStateDefinition applyConnection(MultiBuiltBlockEntity be, ModelStateDefinition definition)
    {
        if (be.getLevel() == null) return definition;
        if (!(definition.component() instanceof Beam)) throw new IllegalStateException("Wrong component type for connection");

        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        float r = definition.rotation();

        Direction left;
        Direction right;

        left = be.toAbsolute(r == 0 ? Direction.EAST : Direction.NORTH);
        right = left.getOpposite();

        boolean connectL;
        boolean connectR;

        if (level.getBlockEntity(pos.relative(left)) instanceof MultiBuiltBlockEntity mbe)
        {
            connectL = !mbe.occupationCache.contains(Occupation.getEdged(mbe.fromAbsolute(right)).get(definition.inBlockPos().y < XTP(8) ? 0 : 2));
        }
        else connectL = !level.getBlockState(pos.relative(left)).isAir() && !level.getBlockState(pos.relative(left)).isFaceSturdy(level, pos.relative(left), right);


        if (level.getBlockEntity(pos.relative(right)) instanceof MultiBuiltBlockEntity mbe)
        {
            connectR = !mbe.occupationCache.contains(Occupation.getEdged(mbe.fromAbsolute(left)).get(definition.inBlockPos().y < XTP(8) ? 0 : 2));
        }
        else connectR = !level.getBlockState(pos.relative(right)).isAir() && !level.getBlockState(pos.relative(right)).isFaceSturdy(level, pos.relative(right), left);

        BuildingComponentModelResourceLocation rl;
        VoxelShape shape;
        if (connectL)
        {
            rl = connectR ? BOTH_CONNECTED : LEFT_CONNECTED;
            shape = connectR ? SHAPE_BOTH_CONNECTED : SHAPE_LEFT_CONNECTED;
        }
        else
        {
            rl = connectR ? RIGHT_CONNECTED : NONE_CONNECTED;
            shape = connectR ? SHAPE_RIGHT_CONNECTED : SHAPE_NONE_CONNECTED;
        }
        if (r == 90) shape = ShapeHelper.rotateShape(shape, -90);
        if (definition.inBlockPos().y() == XTP(8)) shape = ShapeHelper.offsetShape(shape, 0, 0.5, 0);

        return new ModelStateDefinition(definition.component(), definition.inBlockPos(), r, shape, rl, definition.occupation());
    }
}
