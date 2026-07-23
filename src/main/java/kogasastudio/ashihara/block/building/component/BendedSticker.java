package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
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
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class BendedSticker extends AdditionalComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    private VoxelShape SHAPE;

    public BendedSticker
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
    @Override
    public VoxelShape getBaseShape() { return SHAPE; }

    @Override
    public VoxelShape rebuildShape(Vec3 ibp, float rx, float ry, float rz) {
        VoxelShape s = ShapeHelper.rotateShape(getBaseShape(), -ry);
        return ShapeHelper.offsetShape(s, ibp.x, ibp.y, ibp.z);
    }



    public BendedSticker
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
        this.SHAPE = Shapes.box(0, -0.125, 0, 1, 0.625, 0.5);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = direction;
        Vec3 inBlockPos = beIn.inBlockVec(context.getClickLocation());

        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };

        double x;
        double y = inBlockPos.y();
        double z;

        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);
        int floor = y == 0 ? 0 : 2;

        if (direction.getAxis().equals(Direction.Axis.Z))
        {
            if (inBlockPos.z() <= 0.5)
            {
                z = direction == Direction.NORTH ? -0.5 : 0;
            }
            else z = direction == Direction.NORTH ? 0 : 0.5;
            x = 0;
        }
        else
        {
            if (inBlockPos.x() <= 0.5)
            {
                x = direction == Direction.WEST ? -0.5 : 0;
            }
            else x = direction == Direction.WEST ? 0 : 0.5;
            z = 0;
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
            Occupation.CENTER_ALL
        );
    }
}
