package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockDoubleLantern extends Block implements IWaterLoggable
{
    public BlockDoubleLantern(Properties properties)
    {
        super(properties);
        this.registerDefaultState
        (
            this.stateDefinition.any()
            .setValue(LIT, false)
            .setValue(AXIS, Direction.Axis.X)
            .setValue(HALF, DoubleBlockHalf.LOWER)
            .setValue(WATERLOGGED, false)
        );
    }

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

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
            }
        }
        else if (doubleblockhalf == DoubleBlockHalf.LOWER)
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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        return blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)
            ? this.defaultBlockState()
                .setValue(AXIS, context.getHorizontalDirection().getAxis())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(blockpos).getType().equals(Fluids.WATER))
            : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if(player.getItemInHand(handIn).getItem() == Items.AIR && state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            Random random = worldIn.getRandom();
            Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, !instantState));
            return InteractionResult.SUCCESS;
        }
        else return InteractionResult.PASS;
    }
    //大部分是抄的原版
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlock
        (
            pos.above(),
            this.defaultBlockState()
            .setValue(HALF, DoubleBlockHalf.UPPER)
            .setValue(AXIS, state.getValue(AXIS))
            .setValue(WATERLOGGED, worldIn.getFluidState(pos.above()).getType().equals(Fluids.WATER))
            , 3
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, AXIS, HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}

