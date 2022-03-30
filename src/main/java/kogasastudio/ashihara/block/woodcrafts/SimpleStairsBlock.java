package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class SimpleStairsBlock extends StairsBlock
{
    public SimpleStairsBlock()
    {
        super
        (
            BlockRegistryHandler.CHERRY_PLANKS.get().getDefaultState(),
            Properties.from(BlockRegistryHandler.CHERRY_PLANKS.get())
        );
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 20;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return true;}
}
