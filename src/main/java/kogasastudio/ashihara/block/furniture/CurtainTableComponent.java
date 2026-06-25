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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class CurtainTableComponent extends FurnitureComponent implements MultiBlockFurniture
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public CurtainTableComponent(String idIn, BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn,
        FurnitureRenderPass rendererPassIn)
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererPassIn);
        this.MODEL = model;
        this.SHAPE = makeShape();
    }

    @Override public float modelScale() { return 2.0f; }
    @Override public VoxelShape getBaseShape() { return this.SHAPE; }

    @Override
    public BlockBox getExtent(Direction facing)
    {
        return switch (facing)
        {
            case NORTH, SOUTH -> new BlockBox(new BlockPos(-1, 0, 0), new BlockPos(1, 1, 0));
            case EAST, WEST -> new BlockBox(new BlockPos(0, 0, -1), new BlockPos(0, 1, 1));
            default -> new BlockBox(BlockPos.ZERO, BlockPos.ZERO);
        };
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

    private static VoxelShape makeShape()
    {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.25, 0.8125, 0.25, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.40625, 0.25, 0.46875, 0.46875, 1.875, 0.53125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.25, 0.46875, 0.59375, 1.875, 0.53125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 1.875, 0.453125, 2, 1.96875, 0.546875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.875, 0.6875, 0.484375, 1.875, 1.875, 0.671875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.875, 0.125, 0.546875, 1.875, 1.3125, 0.796875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.875, 0, 0.734375, 1.875, 0.625, 0.921875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.875, 0, 0.921875, 1.875, 0.125, 1), BooleanOp.OR);
        return shape;
    }
}
