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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StraightBarWindow extends AdditionalComponent implements Connectable
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

    public StraightBarWindow
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
        this.SHAPE_BOTH_CONNECTED = Shapes.box(0, 0, 0.4375, 1, 1, 0.5625);

        VoxelShape up = Shapes.empty();
        up = Shapes.join(up, Shapes.box(0, 0.125, 0.4375, 1, 1, 0.5625), BooleanOp.OR);
        up = Shapes.join(up, Shapes.box(0, 0, 0.40625, 1, 0.125, 0.59375), BooleanOp.OR);
        this.SHAPE_UP_CONNECTED = up;

        VoxelShape down = Shapes.empty();
        down = Shapes.join(down, Shapes.box(0, 0, 0.4375, 1, 0.875, 0.5625), BooleanOp.OR);
        down = Shapes.join(down, Shapes.box(0, 0.875, 0.40625, 1, 1, 0.59375), BooleanOp.OR);
        this.SHAPE_DOWN_CONNECTED = down;

        VoxelShape none = Shapes.empty();
        none = Shapes.join(none, Shapes.box(0, 0.125, 0.4375, 1, 0.875, 0.5625), BooleanOp.OR);
        none = Shapes.join(none, Shapes.box(0, 0.875, 0.40625, 1, 1, 0.59375), BooleanOp.OR);
        none = Shapes.join(none, Shapes.box(0, 0, 0.40625, 1, 0.125, 0.59375), BooleanOp.OR);
        this.SHAPE_NONE_CONNECTED = none;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);

        float r = direction.getAxis().equals(Direction.Axis.X) ? 90 : 0;

        Direction left = r == 0 ? Direction.WEST : Direction.NORTH;
        Direction right = left.getOpposite();

        List<Occupation> occupations = new ArrayList<>();
        occupations.addAll(Occupation.getEdged(left));
        occupations.addAll(Occupation.getEdged(right));
        occupations.addAll(Occupation.CENTER_ALL);

        VoxelShape shape = SHAPE_NONE_CONNECTED;
        if (r == 90) shape = ShapeHelper.rotateShape(shape, 90);

        ComponentStateDefinition init = new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, 0, 0),
            0, r, 0,
            shape,
            NONE_CONNECTED,
            occupations
        );
        return applyConnection(beIn, init);
    }

    @Override
    public SoundType getSoundType()
    {
        return SoundType.BAMBOO;
    }

    @Override
    public ComponentStateDefinition applyConnection(MultiBuiltBlockEntity be, ComponentStateDefinition definition)
    {
        Level level = be.getLevel();
        if (level == null) return definition;
        BlockPos pos = be.getBlockPos();
        boolean upConnected = false;
        boolean downConnected = false;
        if (level.getBlockEntity(pos.above()) instanceof MultiBuiltBlockEntity mbe)
        {
            if (mbe.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component())))
                upConnected = true;
        }
        if (level.getBlockEntity(pos.below()) instanceof MultiBuiltBlockEntity mbe)
        {
            if (mbe.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).stream().anyMatch(m -> m.component().id.equals(this.expectedId) || (m.component() == definition.component())))
                downConnected = true;
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
        return new ComponentStateDefinition(definition.component(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), newShape, newModel, definition.occupation());
    }
}
