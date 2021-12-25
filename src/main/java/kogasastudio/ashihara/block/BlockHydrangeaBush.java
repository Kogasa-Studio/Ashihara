package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockHydrangeaBush extends Block implements IGrowable
{
    public BlockHydrangeaBush(boolean bloomed)
    {
        super
        (
            Properties.create(Material.LEAVES)
            .hardnessAndResistance(0.05F)
            .sound(SoundType.PLANT)
            .notSolid()
            .tickRandomly()
            .setLightLevel((state) -> 1)
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(BLOOMED, bloomed));
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1;
    }

    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");

    @Override
    public boolean ticksRandomly(BlockState state) {return !state.get(BLOOMED);}

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (random.nextInt(3) == 1 && this.canGrow(worldIn, pos, state, false))
        {
            this.grow(worldIn, random, pos, state);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BLOOMED);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        BlockState down = worldIn.getBlockState(pos);
        return down.getMaterial() == Material.EARTH && !state.get(BLOOMED);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {return !state.get(BLOOMED);}

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockState(pos, state.with(BLOOMED, true));
    }
}
