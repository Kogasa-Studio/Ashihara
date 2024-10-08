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

public class SteepRafter extends AdditionalComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    protected VoxelShape SHAPE;

    public SteepRafter
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape;
    }

    public SteepRafter
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        this(idIn, typeIn, model, null, materialIn, dropsIn);
        initShape();
    }

    private void initShape()
    {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0, 0.75, 0.5, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.5, 0.5, 0.75, 1, 1), BooleanOp.OR);

        this.SHAPE = shape;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);

        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, 0, 0),
            0, r, 0,
            shape,
            MODEL,
            Occupation.CENTER_ALL
        );
    }
}
