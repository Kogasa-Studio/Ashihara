package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class QuarterStraightBarWindow extends AdditionalComponent implements Connectable
{
    private final String expectedId;
    private final BuildingComponentModelResourceLocation NONE_CONNECTED;
    private final BuildingComponentModelResourceLocation UP_CONNECTED;
    private final BuildingComponentModelResourceLocation DOWN_CONNECTED;
    private final BuildingComponentModelResourceLocation BOTH_CONNECTED;

    private VoxelShape SHAPE_NONE_CONNECTED;
    private VoxelShape SHAPE_UP_CONNECTED;
    private VoxelShape SHAPE_DOWN_CONNECTED;
    private VoxelShape SHAPE_BOTH_CONNECTED;

    public QuarterStraightBarWindow
    (
        String idIn,
        BuildingComponents.Type typeIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        BuildingComponentModelResourceLocation noneConnected,
        BuildingComponentModelResourceLocation upConnected,
        BuildingComponentModelResourceLocation downConnected,
        BuildingComponentModelResourceLocation allConnected,
        String expectedIdIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.NONE_CONNECTED = noneConnected;
        this.UP_CONNECTED = upConnected;
        this.DOWN_CONNECTED = downConnected;
        this.BOTH_CONNECTED = allConnected;
        this.expectedId = expectedIdIn;
        initShape();
    }

    private void initShape()
    {
        this.SHAPE_BOTH_CONNECTED = Shapes.box(0.25, 0, 0.4375, 0.75, 0.5, 0.5625);

        VoxelShape up = Shapes.empty();
        up = Shapes.join(up, Shapes.box(0.25, 0.125, 0.4375, 0.75, 0.5, 0.5625), BooleanOp.OR);
        up = Shapes.join(up, Shapes.box(0.25, 0, 0.40625, 0.75, 0.125, 0.59375), BooleanOp.OR);
        this.SHAPE_UP_CONNECTED = up;

        VoxelShape down = Shapes.empty();
        down = Shapes.join(down, Shapes.box(0.25, 0, 0.4375, 0.75, 0.375, 0.5625), BooleanOp.OR);
        down = Shapes.join(down, Shapes.box(0.25, 0.375, 0.40625, 0.75, 0.5, 0.59375), BooleanOp.OR);
        this.SHAPE_DOWN_CONNECTED = down;

        VoxelShape none = Shapes.empty();
        none = Shapes.join(none, Shapes.box(0.25, 0.375, 0.40625, 0.75, 0.5, 0.59375), BooleanOp.OR);
        none = Shapes.join(none, Shapes.box(0.25, 0.125, 0.4375, 0.75, 0.375, 0.5625), BooleanOp.OR);
        none = Shapes.join(none, Shapes.box(0.25, 0, 0.40625, 0.75, 0.125, 0.59375), BooleanOp.OR);
        this.SHAPE_NONE_CONNECTED = none;
    }

    @Override
    public SoundType getSoundType()
    {
        return SoundType.BAMBOO;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);

        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));
        double y = inBlockPos.y();

        if (context.getClickedFace().equals(Direction.UP))
        {
            y = (y >= 0 && y < XTP(8)) ? 0 : XTP(8);
        }
        else y = (y > 0 && y <= XTP(8)) ? 0 : XTP(8);

        double x;
        double z;
        float r;
        if (direction.getAxis().equals(Direction.Axis.Z))
        {
            x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
            z = 0;
            r = 0;
        }
        else
        {
            z = inBlockPos.z() <= XTP(8) ? XTP(-4) : XTP(4);
            x = 0;
            r = 90;
        }

        int floor = y == 0 ? 0 : 2;

        Occupation occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE_NONE_CONNECTED;
        shape = ShapeHelper.rotateShape(shape, r);
        shape = ShapeHelper.offsetShape(shape, x, y, z);

        ComponentStateDefinition init = new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, r, 0,
            shape,
            NONE_CONNECTED,
            List.of(occupation)
        );
        return applyConnection(beIn, init);
    }

    @Override
    public ComponentStateDefinition applyConnection(MultiBuiltBlockEntity be, ComponentStateDefinition definition)
    {
        Level level = be.getLevel();
        if (level == null) return definition;
        BlockPos pos = be.getBlockPos();
        boolean upConnected = false;
        boolean downConnected = false;
        Occupation occupation = definition.occupation().getFirst();
        Occupation expected = occupation == Occupation.F1_C ? Occupation.F3_C : Occupation.F1_C;

        //up
        if (occupation == Occupation.F3_C && level.getBlockEntity(pos.above()) instanceof MultiBuiltBlockEntity mbe)
        {
            if (mbe.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component() && m.occupation().getFirst() == expected)))
                upConnected = true;
        }
        else if (occupation != Occupation.F3_C)
        {
            upConnected = be.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component() && m.occupation().getFirst() == expected));
        }

        //down
        if (occupation == Occupation.F1_C && level.getBlockEntity(pos.below()) instanceof MultiBuiltBlockEntity mbe)
        {
            if (mbe.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component() && m.occupation().getFirst() == expected)))
                downConnected = true;
        }
        else if (occupation != Occupation.F1_C)
        {
            downConnected = be.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component() && m.occupation().getFirst() == expected));
        }
        BuildingComponentModelResourceLocation newModel;
        VoxelShape newShape;
        if (upConnected)
        {
            newModel= downConnected ? BOTH_CONNECTED : UP_CONNECTED;
            newShape = downConnected ? SHAPE_BOTH_CONNECTED : SHAPE_UP_CONNECTED;
        }
        else
        {
            newModel = downConnected ? DOWN_CONNECTED : NONE_CONNECTED;
            newShape = downConnected ? SHAPE_DOWN_CONNECTED : SHAPE_NONE_CONNECTED;
        }
        newShape = ShapeHelper.rotateShape(newShape, definition.rotationY());
        newShape = ShapeHelper.offsetShape(newShape, definition.inBlockPos().x(), definition.inBlockPos().y(), definition.inBlockPos().z());
        return new ComponentStateDefinition(definition.component(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), newShape, newModel, definition.occupation());
    }
}
