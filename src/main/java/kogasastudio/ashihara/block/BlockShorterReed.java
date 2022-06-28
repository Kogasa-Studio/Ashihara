package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockShorterReed extends BlockReed
{
    public BlockShorterReed(){super();}

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape SHAPE1 = Block.makeCuboidShape(0,0,0,16,16,16);
        VoxelShape SHAPE2 = Block.makeCuboidShape(0,0,0,16,8,16);
        return state.get(HALF) == DoubleBlockHalf.UPPER ? SHAPE2 : SHAPE1;
    }
}
