package kogasastudio.ashihara.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeHelper
{
    public static VoxelShape getWallShape(Direction direction, Direction.Axis axis)
    {
        if (direction.equals(Direction.NORTH)) return Block.box(7, 0, 0, 9, 16, 1);
        if (direction.equals(Direction.WEST)) return Block.box(0, 0, 7, 1, 16, 9);
        if (direction.equals(Direction.SOUTH)) return Block.box(7, 0, 15, 9, 16, 16);
        if (direction.equals(Direction.EAST)) Block.box(15, 0, 7, 16, 16, 9);
        if (direction.equals(Direction.UP))
        {
            if (axis.equals(Direction.Axis.X)) return Block.box(0, 15, 7, 16, 16, 9);
            else return Block.box(7, 15, 0, 9, 16, 16);
        }
        else
        {
            if (axis.equals(Direction.Axis.X)) return Block.box(0, 0, 7, 16, 1, 9);
            else return Block.box(7, 0, 0, 9, 1, 16);
        }
    }

    /**
     * 检测某方块是否在某方向的形状是否符合某种形状要求。
     * 判断标准：获取该方块在该方向的形状，尝试按照某种规则与需求的形状相交。若相交后的形状不为空，返回true，否则返回false
     * @param shapeNeeded 需对比的形状
     */
    public static boolean canBlockSupport(BlockState supporter, BlockGetter getter, BlockPos supportedPos, Direction direction, VoxelShape shapeNeeded)
    {
        VoxelShape shape = supporter.getBlockSupportShape(getter, supportedPos);
        VoxelShape face = shape.getFaceShape(direction);
        boolean f = !Shapes.joinIsNotEmpty(face, shapeNeeded, BooleanOp.ONLY_SECOND);
        //VoxelShape merged = Shapes.or(face, shapeNeeded);
        //boolean f = merged.equals(shapeNeeded);
        return f;
    }
}
