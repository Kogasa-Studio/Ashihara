package kogasastudio.ashihara.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

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
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return 1;
    }

    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");

    @Override
    public boolean isRandomlyTicking(BlockState state) {return !state.getValue(BLOOMED);}

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random)
    {
        if (random.nextInt(3) == 1 && this.isValidBonemealTarget(worldIn, pos, state, false))
        {
            this.performBonemeal(worldIn, random, pos, state);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BLOOMED);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        BlockState down = worldIn.getBlockState(pos);
        return down.getMaterial() == Material.DIRT && !state.getValue(BLOOMED);
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {return !state.getValue(BLOOMED);}

    @Override
    public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockAndUpdate(pos, state.setValue(BLOOMED, true));
    }
}
