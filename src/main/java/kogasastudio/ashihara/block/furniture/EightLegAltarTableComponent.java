package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class EightLegAltarTableComponent extends FurnitureComponent implements MultiBlockFurniture
{
    // -- Shape constants ----------------------------------------------
// -- Eight Leg Altar Table shapes -------------------------------------------

    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_SHAPE = Shapes.or(
        Shapes.box(-0.75, 0.875, 0, 1.75, 1, 1),
        Shapes.box(-0.484375, 0.125, 0.171875, -0.390625, 0.75, 0.265625),
        Shapes.box(-0.484375, 0.125, 0.359375, -0.390625, 0.75, 0.453125),
        Shapes.box(-0.484375, 0.125, 0.546875, -0.390625, 0.75, 0.640625),
        Shapes.box(-0.484375, 0.125, 0.734375, -0.390625, 0.75, 0.828125),
        Shapes.box(-0.5, 0, 0.03125, -0.375, 0.125, 0.96875),
        Shapes.box(-0.5, 0.75, 0.03125, -0.375, 0.875, 0.96875),
        Shapes.box(1.375, 0, 0.03125, 1.5, 0.125, 0.96875),
        Shapes.box(1.390625, 0.125, 0.171875, 1.484375, 0.75, 0.265625),
        Shapes.box(1.390625, 0.125, 0.359375, 1.484375, 0.75, 0.453125),
        Shapes.box(1.390625, 0.125, 0.546875, 1.484375, 0.75, 0.640625),
        Shapes.box(1.390625, 0.125, 0.734375, 1.484375, 0.75, 0.828125),
        Shapes.box(1.375, 0.75, 0.03125, 1.5, 0.875, 0.96875)
    );
    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_MINI_SHORT_SHAPE = Shapes.or(
        Shapes.box(-0.25, 0.625, 0.25, 1.25, 0.75, 0.75),
        Shapes.box(0.03125, 0.09375, 0.328125, 0.09375, 0.53125, 0.390625),
        Shapes.box(0.03125, 0.09375, 0.421875, 0.09375, 0.53125, 0.484375),
        Shapes.box(0.03125, 0.09375, 0.515625, 0.09375, 0.53125, 0.578125),
        Shapes.box(0.03125, 0.09375, 0.609375, 0.09375, 0.53125, 0.671875),
        Shapes.box(0, 0, 0.28125, 0.125, 0.09375, 0.71875),
        Shapes.box(0, 0.53125, 0.28125, 0.125, 0.625, 0.71875),
        Shapes.box(0.875, 0, 0.28125, 1, 0.09375, 0.71875),
        Shapes.box(0.90625, 0.09375, 0.328125, 0.96875, 0.53125, 0.390625),
        Shapes.box(0.90625, 0.09375, 0.421875, 0.96875, 0.53125, 0.484375),
        Shapes.box(0.90625, 0.09375, 0.515625, 0.96875, 0.53125, 0.578125),
        Shapes.box(0.90625, 0.09375, 0.609375, 0.96875, 0.53125, 0.671875),
        Shapes.box(0.875, 0.53125, 0.28125, 1, 0.625, 0.71875)
    );
    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_TALL_SHAPE = Shapes.or(
        Shapes.box(-0.75, 1.375, 0, 1.75, 1.5, 1),
        Shapes.box(-0.484375, 0.125, 0.171875, -0.390625, 1.25, 0.265625),
        Shapes.box(-0.484375, 0.125, 0.359375, -0.390625, 1.25, 0.453125),
        Shapes.box(-0.484375, 0.125, 0.546875, -0.390625, 1.25, 0.640625),
        Shapes.box(-0.484375, 0.125, 0.734375, -0.390625, 1.25, 0.828125),
        Shapes.box(-0.5, 0, 0.03125, -0.375, 0.125, 0.96875),
        Shapes.box(-0.5, 1.25, 0.03125, -0.375, 1.375, 0.96875),
        Shapes.box(1.375, 0, 0.03125, 1.5, 0.125, 0.96875),
        Shapes.box(1.390625, 0.125, 0.171875, 1.484375, 1.25, 0.265625),
        Shapes.box(1.390625, 0.125, 0.359375, 1.484375, 1.25, 0.453125),
        Shapes.box(1.390625, 0.125, 0.546875, 1.484375, 1.25, 0.640625),
        Shapes.box(1.390625, 0.125, 0.734375, 1.484375, 1.25, 0.828125),
        Shapes.box(1.375, 1.25, 0.03125, 1.5, 1.375, 0.96875)
    );
    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_THIN_SHAPE = Shapes.or(
        Shapes.box(-0.75, 1, 0.25, 1.75, 1.125, 0.75),
        Shapes.box(-0.46875, 0.09375, 0.328125, -0.40625, 0.90625, 0.390625),
        Shapes.box(-0.46875, 0.09375, 0.421875, -0.40625, 0.90625, 0.484375),
        Shapes.box(-0.46875, 0.09375, 0.515625, -0.40625, 0.90625, 0.578125),
        Shapes.box(-0.46875, 0.09375, 0.609375, -0.40625, 0.90625, 0.671875),
        Shapes.box(-0.5, 0, 0.28125, -0.375, 0.09375, 0.71875),
        Shapes.box(-0.5, 0.90625, 0.28125, -0.375, 1, 0.71875),
        Shapes.box(1.375, 0, 0.28125, 1.5, 0.09375, 0.71875),
        Shapes.box(1.40625, 0.09375, 0.328125, 1.46875, 0.90625, 0.390625),
        Shapes.box(1.40625, 0.09375, 0.421875, 1.46875, 0.90625, 0.484375),
        Shapes.box(1.40625, 0.09375, 0.515625, 1.46875, 0.90625, 0.578125),
        Shapes.box(1.40625, 0.09375, 0.609375, 1.46875, 0.90625, 0.671875),
        Shapes.box(1.375, 0.90625, 0.28125, 1.5, 1, 0.71875)
    );
    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_THIN_SHORT_SHAPE = Shapes.or(
        Shapes.box(-0.75, 0.625, 0.25, 1.75, 0.75, 0.75),
        Shapes.box(-0.46875, 0.09375, 0.328125, -0.40625, 0.53125, 0.390625),
        Shapes.box(-0.46875, 0.09375, 0.421875, -0.40625, 0.53125, 0.484375),
        Shapes.box(-0.46875, 0.09375, 0.515625, -0.40625, 0.53125, 0.578125),
        Shapes.box(-0.46875, 0.09375, 0.609375, -0.40625, 0.53125, 0.671875),
        Shapes.box(-0.5, 0, 0.28125, -0.375, 0.09375, 0.71875),
        Shapes.box(-0.5, 0.53125, 0.28125, -0.375, 0.625, 0.71875),
        Shapes.box(1.375, 0, 0.28125, 1.5, 0.09375, 0.71875),
        Shapes.box(1.40625, 0.09375, 0.328125, 1.46875, 0.53125, 0.390625),
        Shapes.box(1.40625, 0.09375, 0.421875, 1.46875, 0.53125, 0.484375),
        Shapes.box(1.40625, 0.09375, 0.515625, 1.46875, 0.53125, 0.578125),
        Shapes.box(1.40625, 0.09375, 0.609375, 1.46875, 0.53125, 0.671875),
        Shapes.box(1.375, 0.53125, 0.28125, 1.5, 0.625, 0.71875)
    );
    public static final VoxelShape EIGHT_LEG_ALTAR_TABLE_THIN_TALL_SHAPE = Shapes.or(
        Shapes.box(-0.75, 1.375, 0.25, 1.75, 1.5, 0.75),
        Shapes.box(-0.46875, 0.09375, 0.328125, -0.40625, 1.28125, 0.390625),
        Shapes.box(-0.46875, 0.09375, 0.421875, -0.40625, 1.28125, 0.484375),
        Shapes.box(-0.46875, 0.09375, 0.515625, -0.40625, 1.28125, 0.578125),
        Shapes.box(-0.46875, 0.09375, 0.609375, -0.40625, 1.28125, 0.671875),
        Shapes.box(-0.5, 0, 0.28125, -0.375, 0.09375, 0.71875),
        Shapes.box(-0.5, 1.28125, 0.28125, -0.375, 1.375, 0.71875),
        Shapes.box(1.375, 0, 0.28125, 1.5, 0.09375, 0.71875),
        Shapes.box(1.40625, 0.09375, 0.328125, 1.46875, 1.28125, 0.390625),
        Shapes.box(1.40625, 0.09375, 0.421875, 1.46875, 1.28125, 0.484375),
        Shapes.box(1.40625, 0.09375, 0.515625, 1.46875, 1.28125, 0.578125),
        Shapes.box(1.40625, 0.09375, 0.609375, 1.46875, 1.28125, 0.671875),
        Shapes.box(1.375, 1.28125, 0.28125, 1.5, 1.375, 0.71875)
    );

    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;
    private final boolean thin;

    public EightLegAltarTableComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        boolean thin,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        FurnitureRenderPass rendererPassIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererPassIn);
        this.MODEL = model;
        this.SHAPE = shape;
        this.thin = thin;
    }

    @Override public VoxelShape getBaseShape() { return this.SHAPE; }
    @Override public boolean isComplexShape() { return true; }

    @Override
    public VoxelShape rebuildShape(Vec3 inBlockPos, float rotX, float rotY, float rotZ)
    {
        VoxelShape full = rotY != 0 ? ShapeHelper.rotateShape(this.SHAPE, -rotY) : this.SHAPE;
        full = ShapeHelper.offsetShape(full, inBlockPos.x(), inBlockPos.y(), inBlockPos.z());
        return ShapeHelper.sliceShape(full, 1, net.minecraft.core.Vec3i.ZERO);
    }

    @Override
    public BlockBox getExtent(Direction facing)
    {
        if (thin)
        {
            return new BlockBox(new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0));
        }
        return new BlockBox(new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0));
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlock = beIn.inBlockVec(context.getClickLocation());
        double x = clampInBlock(inBlock.x() - 8f / 16, -0.5, 0.5);
        double y = clampInBlock(inBlock.y(), 0.0, 1.0);
        double z = clampInBlock(inBlock.z() - 8f / 16, -0.5, 0.5);
        Direction direction = context.getHorizontalDirection();
        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };
        VoxelShape shape = ShapeHelper.rotateShape(this.SHAPE, -r);
        shape = ShapeHelper.offsetShape(shape, x, y, z);
        return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, r, 0, shape, MODEL, List.of(), null);
    }
}
