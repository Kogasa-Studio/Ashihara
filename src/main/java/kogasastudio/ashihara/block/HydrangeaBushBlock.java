package kogasastudio.ashihara.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

public class HydrangeaBushBlock extends BushBlock implements BonemealableBlock
{
    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");

    public HydrangeaBushBlock(boolean bloomed)
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.PLANT)
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

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(BLOOMED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random)
    {
        if (random.nextInt(3) == 1 && this.isValidBonemealTarget(worldIn, pos, state))
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
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState)
    {
        BlockState down = pLevel.getBlockState(pPos);
        return down.canSustainPlant(pLevel, pPos, Direction.UP, this.defaultBlockState()).isTrue() && !pState.getValue(BLOOMED);
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        return !state.getValue(BLOOMED);
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockAndUpdate(pos, state.setValue(BLOOMED, true));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec()
    {
        return simpleCodec(p -> new HydrangeaBushBlock(false));
    }
}
