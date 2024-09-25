package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class Hijiki extends AdditionalComponent implements Connectable
{
    private final BuildingComponentModelResourceLocation LEFT_CONNECTED;
    private final BuildingComponentModelResourceLocation RIGHT_CONNECTED;
    private final BuildingComponentModelResourceLocation BOTH_CONNECTED;

    private VoxelShape SHAPE_LEFT_CONNECTED;
    private VoxelShape SHAPE_RIGHT_CONNECTED;
    private VoxelShape SHAPE_BOTH_CONNECTED;

    public Hijiki
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation l,
        BuildingComponentModelResourceLocation r,
        BuildingComponentModelResourceLocation a,
        List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.LEFT_CONNECTED = l;
        this.RIGHT_CONNECTED = r;
        this.BOTH_CONNECTED = a;
        initShape();
    }

    public Hijiki
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation l,
        BuildingComponentModelResourceLocation r,
        BuildingComponentModelResourceLocation a,
        VoxelShape shape_l,
        VoxelShape shape_r,
        VoxelShape shape_a,
        List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, l, r, a, dropsIn);
        this.SHAPE_LEFT_CONNECTED = shape_l;
        this.SHAPE_RIGHT_CONNECTED = shape_r;
        this.SHAPE_BOTH_CONNECTED = shape_a;
    }

    protected void initShape()
    {
        this.SHAPE_LEFT_CONNECTED = Shapes.box(0.375, -0.03125, 0, 0.625, 0.25, 0.625);
        this.SHAPE_RIGHT_CONNECTED = Shapes.box(0.375, -0.03125, -0.125, 0.625, 0.25, 0.5);
        this.SHAPE_BOTH_CONNECTED = Shapes.box(0.375, -0.03125, 0, 0.625, 0.25, 0.5);
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
            if (z >= 0.5)
            {
                z = direction == Direction.SOUTH ? 0.5 : 0;
            }
            else
            {
                z = direction == Direction.SOUTH ? 0 : 0.5;
            }
            occz = (z == 0 && direction == Direction.SOUTH) ? 0 : 1;
        }
        else
        {
            if (z >= 0 && z < XTP(4)) {z = -0.5; occz = 0;}
            else if (z >= XTP(4) && z < XTP(12)) {z = 0; occz = 0.5;}
            else {z = 0.5; occz = 1;}
            if (x >= 0.5)
            {
                x = direction == Direction.EAST ? 0.5 : 0;
            }
            else
            {
                x = direction == Direction.EAST ? 0 : 0.5;
            }
            occx = (x == 0 && direction == Direction.EAST) ? 0 : 1;
        }

        y = XTP((float) (floor * 4));

        Occupation occupation = Occupation.mapPosition(occx, y, occz);

        VoxelShape shape = SHAPE_RIGHT_CONNECTED;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, x, y, z);

        ComponentStateDefinition init = new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, r, 0,
            shape,
            RIGHT_CONNECTED,
            List.of(occupation)
        );
        return applyConnection(beIn, init);
    }

    @Override
    public ComponentStateDefinition applyConnection(MultiBuiltBlockEntity be, ComponentStateDefinition definition)
    {
        if (be.getLevel() == null) return definition;
        if (!(definition.component() instanceof Hijiki)) throw new IllegalStateException("Wrong component type for connection.");

        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        Vec3 inBlockPos = definition.inBlockPos();
        float r = definition.rotationY();

        Direction direction;
        Vec3 expected;
        switch ((int) r)
        {
            case 90 ->
            {
                direction = Direction.WEST;
                expected = new Vec3(1, inBlockPos.y(), inBlockPos.z() + 0.5);
            }
            case 180 ->
            {
                direction = Direction.SOUTH;
                expected = new Vec3(inBlockPos.x() + 0.5, inBlockPos.y(), 0);
            }
            case 270 ->
            {
                direction = Direction.EAST;
                expected = new Vec3(0, inBlockPos.y(), inBlockPos.z() + 0.5);
            }
            default ->
            {
                direction = Direction.NORTH;
                expected = new Vec3(inBlockPos.x() + 0.5, inBlockPos.y(), 1);
            }
        };

        Direction pointed = be.toAbsolute(direction);
        Vec3 relExpected = be.outLayVec3(expected);

        boolean connectL = false;
        boolean connectR;

        if (level.getBlockEntity(pos.relative(pointed)) instanceof MultiBuiltBlockEntity mbe)
        {
            relExpected = mbe.transformVec3(relExpected);
            Vec3 vec = relExpected;
            connectL = mbe.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component() == definition.component() && m.occupation().contains(Occupation.mapPosition(vec.x(), vec.y(), vec.z())));
        }
        Vec3 vec = relExpected;
        connectR = be.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component() == definition.component() && m.occupation().contains(Occupation.mapPosition(vec.x(), vec.y(), vec.z())));

        BuildingComponentModelResourceLocation rl;
        VoxelShape shape;
        if (connectL)
        {
            rl = connectR ? BOTH_CONNECTED : LEFT_CONNECTED;
            shape = connectR ? SHAPE_BOTH_CONNECTED : SHAPE_LEFT_CONNECTED;
        }
        else
        {
            rl = RIGHT_CONNECTED;
            shape = SHAPE_RIGHT_CONNECTED;
        }
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, inBlockPos.x(), inBlockPos.y(), inBlockPos.z());

        return new ComponentStateDefinition(definition.component(), definition.inBlockPos(), definition.rotationX(), r, definition.rotationZ(), shape, rl, definition.occupation());
    }
}
