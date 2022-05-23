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

import net.minecraft.block.AbstractBlock.Properties;

public class BlockHydrangeaBush extends Block implements IGrowable
{
    public BlockHydrangeaBush(boolean bloomed)
    {
        super
        (
            Properties.of(Material.LEAVES)
            .strength(0.05F)
            .sound(SoundType.GRASS)
            .noOcclusion()
            .randomTicks()
            .lightLevel((state) -> 1)
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(BLOOMED, bloomed));
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1;
    }

    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");

    @Override
    public boolean isRandomlyTicking(BlockState state) {return !state.getValue(BLOOMED);}

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (random.nextInt(3) == 1 && this.isValidBonemealTarget(worldIn, pos, state, false))
        {
            this.performBonemeal(worldIn, random, pos, state);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BLOOMED);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        BlockState down = worldIn.getBlockState(pos);
        return down.getMaterial() == Material.DIRT && !state.getValue(BLOOMED);
    }

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {return !state.getValue(BLOOMED);}

    @Override
    public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockAndUpdate(pos, state.setValue(BLOOMED, true));
    }
}
