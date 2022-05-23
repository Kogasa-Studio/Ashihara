package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class SimpleStairsBlock extends StairsBlock
{
    public SimpleStairsBlock()
    {
        super
        (
            BlockRegistryHandler.CHERRY_PLANKS.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(BlockRegistryHandler.CHERRY_PLANKS.get())
        );
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 20;}

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return true;}
}
