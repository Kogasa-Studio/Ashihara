package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShorterReedBlock extends ReedBlock
{
    public ShorterReedBlock()
    {
        super();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape SHAPE1 = Block.box(0, 0, 0, 16, 16, 16);
        VoxelShape SHAPE2 = Block.box(0, 0, 0, 16, 8, 16);
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE2 : SHAPE1;
    }
}
