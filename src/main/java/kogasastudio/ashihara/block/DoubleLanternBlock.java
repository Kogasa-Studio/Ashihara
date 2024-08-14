package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DoubleLanternBlock extends LanternBlock implements SimpleWaterloggedBlock
{
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DoubleLanternBlock(Properties properties, double XIn, double YIn, double ZIn)
    {
        super(properties, XIn, YIn, ZIn);
        this.registerDefaultState
                (
                        this.stateDefinition.any()
                                .setValue(LIT, false)
                                .setValue(HALF, DoubleBlockHalf.LOWER)
                                .setValue(WATERLOGGED, false)
                );
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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        return /*blockpos.getY() < 255 && */context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)
                ? this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(blockpos).getType().equals(Fluids.WATER))
                : null;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        else return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        if (!stateIn.getValue(HALF).equals(DoubleBlockHalf.LOWER)) {super.animateTick(stateIn, worldIn, pos, rand);}
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
                                .setValue(WATERLOGGED, worldIn.getFluidState(pos.above()).getType().equals(Fluids.WATER))
                        , 3
                );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    //沿X/Z轴有变种的版本
    public static class AxisAlignedVariant extends DoubleLanternBlock
    {
        public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
        public AxisAlignedVariant(Properties properties, double XIn, double YIn, double ZIn)
        {
            super(properties, XIn, YIn, ZIn);
            this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            super.createBlockStateDefinition(builder);
            builder.add(AXIS);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context)
        {
            BlockState stateS = super.getStateForPlacement(context);
            return stateS == null ? null : stateS.setValue(AXIS, context.getHorizontalDirection().getAxis());
        }

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
    }

    //沿水平方向都有变种的版本
    public static class FourFacingVariant extends DoubleLanternBlock
    {
        public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
        public FourFacingVariant(Properties properties, double XIn, double YIn, double ZIn)
        {
            super(properties, XIn, YIn, ZIn);
            this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            super.createBlockStateDefinition(builder);
            builder.add(FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context)
        {
            BlockState stateS = super.getStateForPlacement(context);
            return stateS == null ? null : stateS.setValue(FACING, context.getHorizontalDirection().getOpposite());
        }

        @Override
        public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
        {
            worldIn.setBlock
                    (
                            pos.above(),
                            this.defaultBlockState()
                                    .setValue(HALF, DoubleBlockHalf.UPPER)
                                    .setValue(FACING, state.getValue(FACING))
                                    .setValue(WATERLOGGED, worldIn.getFluidState(pos.above()).getType().equals(Fluids.WATER))
                            , 3
                    );
        }
    }
}

