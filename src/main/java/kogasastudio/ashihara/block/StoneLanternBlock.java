package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;
import static net.minecraft.world.item.Items.GLASS_PANE;

public class StoneLanternBlock extends DoubleLanternBlock
{
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty MOSSY = BooleanProperty.create("mossy");

    public StoneLanternBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.STONE)
                                .strength(4.0F)
                                .sound(SoundType.STONE)
                                .lightLevel(getLightValueLit(15)),
                        0.5d, 0.125d, 0.5d
                );
        this.registerDefaultState(this.defaultBlockState().setValue(SEALED, false).setValue(MOSSY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SEALED, MOSSY);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.LOWER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            } else if (state.getValue(LIT) && state.getValue(WATERLOGGED) && !state.getValue(SEALED))
            {
                worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        } else if (doubleblockhalf == DoubleBlockHalf.LOWER)
        {
            BlockPos blockpos = pos.above();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.UPPER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult)
    {
        if (pPlayer.isShiftKeyDown() && pState.getValue(SEALED))
        {
            pLevel.playSound(pPlayer, pPos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SEALED, false));
            if (!pPlayer.isCreative())
            {
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(GLASS_PANE));
            }
            return InteractionResult.SUCCESS;
        } else if (pState.getValue(LIT))
        {
            pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        RandomSource random = worldIn.getRandom();
        if (stack.getItem().equals(Items.FLINT_AND_STEEL) && !state.getValue(LIT) && (!state.getValue(WATERLOGGED) || state.getValue(SEALED)))
        {
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, true));
            return ItemInteractionResult.SUCCESS;
        }
        else if (stack.getItem().equals(GLASS_PANE) && !state.getValue(SEALED))
        {
            worldIn.playSound(player, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockAndUpdate(pos, state.setValue(SEALED, true));
            if (!player.isCreative())
            {
                stack.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        } else
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape v1 = Block.box(2.0d, 0.0d, 2.0d, 14.0d, 8.0d, 14.0d);
        VoxelShape v2 = Block.box(3.0d, 8.0d, 3.0d, 13.0d, 16.0d, 13.0d);
        VoxelShape LOWER = Shapes.or(v1, v2);
        VoxelShape v3 = Block.box(5.0d, 0.0d, 5.0d, 11.0d, 5.0d, 11.0d);
        VoxelShape v4 = Block.box(2.0d, 5.0d, 2.0d, 14.0d, 11.25d, 14.0d);
        VoxelShape v5 = Block.box(7.0d, 11.25d, 7.0d, 9.0d, 13.25d, 9.0d);
        VoxelShape UPPER = Shapes.or(v3, v4, v5);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
        {
            return LOWER;
        } else
        {
            return UPPER;
        }
    }

    @Override
    public void handlePrecipitation(BlockState pState, Level pLevel, BlockPos pPos, Biome.Precipitation pPrecipitation)
    {
        super.handlePrecipitation(pState, pLevel, pPos, pPrecipitation);
    }

    // todo 应该是上面的方法(handlePrecipitation)，我不清楚你想要的功能，没用过，你自己定夺 @Override
    public void handleRain(Level worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.is(BlockRegistryHandler.STONE_LANTERN.get()))
        {
            return;
        }
        RandomSource random = worldIn.getRandom();
        if (random.nextInt(10) <= 5)
        {
            BlockPos offset = state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            worldIn.setBlockAndUpdate(pos, worldIn.getBlockState(pos).setValue(MOSSY, true));
            worldIn.setBlockAndUpdate(offset, worldIn.getBlockState(offset).setValue(MOSSY, true));
        }
    }
}
