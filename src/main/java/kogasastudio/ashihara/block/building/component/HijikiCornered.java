package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class HijikiCornered extends AdditionalComponent implements Interactable
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final BuildingComponentModelResourceLocation LONG_MODEL;

    private VoxelShape SHAPE;
    private VoxelShape LONG_SHAPE;
    private final Supplier<ItemStack> interactItem;

    public HijikiCornered
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        BuildingComponentModelResourceLocation longModel,
        VoxelShape shape,
        VoxelShape longShape,
        Supplier<ItemStack> interactItemIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.MODEL = model;
        this.LONG_MODEL = longModel;
        this.SHAPE = shape;
        this.LONG_SHAPE = longShape;
        this.interactItem = interactItemIn;
    }

    public HijikiCornered
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        BuildingComponentModelResourceLocation longModel,
        Supplier<ItemStack> interactItemIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        this(idIn, typeIn, model, longModel, null, null, interactItemIn, materialIn, dropsIn);
        initShape();
    }

    private void initShape()
    {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.625, -0.03125, 0.625, 1.125, 0.25, 1.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, -0.03125, 0.3125, 0.8125, 0.25, 0.8125), BooleanOp.OR);
        this.SHAPE = shape;

        VoxelShape shape1 = Shapes.empty();
        shape1 = Shapes.join(shape1, Shapes.box(0.125, -0.03125, 0.125, 0.625, 0.25, 0.625), BooleanOp.OR);
        shape1 = Shapes.join(shape1, Shapes.box(-0.1875, -0.03125, -0.1875, 0.3125, 0.25, 0.3125), BooleanOp.OR);
        shape1 = Shapes.join(shape1, Shapes.box(0.375, -0.03125, 0.375, 0.875, 0.25, 0.875), BooleanOp.OR);
        shape1 = Shapes.join(shape1, Shapes.box(0.625, -0.03125, 0.625, 1.125, 0.25, 1.125), BooleanOp.OR);
        this.LONG_SHAPE = shape1;
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
        double y = inBlockPos.y();

        int floor = (int) Math.clamp(Math.floor(y * 4), 0, 3);

        y = XTP((float) (floor * 4));

        Occupation occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, r + 45, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        if (this.LONG_MODEL == null || definition.model() == this.LONG_MODEL) return definition;
        if (context.getItemInHand().is(this.interactItem.get().getItem()))
        {
            VoxelShape shape = LONG_SHAPE;
            shape = ShapeHelper.rotateShape(shape, definition.rotationY() - 45);
            shape = ShapeHelper.offsetShape(shape, 0, definition.inBlockPos().y(), 0);
            return new ComponentStateDefinition(definition.component(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), shape, LONG_MODEL, definition.occupation());
        }
        return definition;
    }
}
