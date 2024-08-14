package kogasastudio.ashihara.block;

import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RiceDryingSticksBlock extends Block implements SimpleWaterloggedBlock
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final EnumProperty<RiceDryingState> DRYING_STATE = EnumProperty.create("drying_state", RiceDryingState.class);
    public static final EnumProperty<DryingSticksHalf> L_R_HALF = EnumProperty.create("l_r_type", DryingSticksHalf.class);

    public RiceDryingSticksBlock()
    {
        super
        (
            Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2)
            .sound(SoundType.WOOD)
            .noOcclusion()
            .lightLevel(i -> 1)
        );
        this.registerDefaultState
        (
            this.defaultBlockState()
            .setValue(HALF, DoubleBlockHalf.LOWER)
            .setValue(AXIS, Direction.Axis.X)
            .setValue(DRYING_STATE, RiceDryingState.NONE)
            .setValue(L_R_HALF, DryingSticksHalf.SINGLE)
            .setValue(WATERLOGGED, false)
        );
        initShapes();
    }

    VoxelShape single_X = Shapes.empty();
    VoxelShape single_Z;
    VoxelShape single_X_U = Shapes.empty();
    VoxelShape single_Z_U;
    VoxelShape multiple_X_L = Shapes.empty();
    VoxelShape multiple_Z_L;
    VoxelShape multiple_X_R;
    VoxelShape multiple_Z_R;
    VoxelShape multiple_X_L_U = Shapes.empty();
    VoxelShape multiple_Z_L_U;
    VoxelShape multiple_X_R_U;
    VoxelShape multiple_Z_R_U;
    VoxelShape rice_X = box(1.5, 2.25, 3, 14.5, 16, 13);
    VoxelShape rice_Z = ShapeHelper.rotateShape(rice_X, 90);
    VoxelShape rice_X_U = ShapeHelper.offsetShape(rice_X, 0, -3d/16d, 0);
    VoxelShape rice_Z_U = ShapeHelper.offsetShape(rice_Z, 0, -3d/16d, 0);

    private void initShapes()
    {
        single_X = Shapes.join(single_X, Shapes.box(0, 0.8125, 0.4684375, 1, 0.875, 0.5315625), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.03125, 0, -0.25, 0.09375, 0.25, 1.25), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.03125, 0.25, -0.125, 0.09375, 0.5, 1.125), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.03125, 0.5, -0.015625, 0.09375, 0.75, 1.015625), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.03125, 0.75, 0.078125, 0.09375, 1, 0.921875), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.90625, 0, -0.25, 0.96875, 0.25, 1.25), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.90625, 0.25, -0.125, 0.96875, 0.5, 1.125), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.90625, 0.5, -0.015625, 0.96875, 0.75, 1.015625), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.90625, 0.75, 0.078125, 0.96875, 1, 0.921875), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.875, 0, 0.46875, 0.9375, 1, 0.53125), BooleanOp.OR);
        single_X = Shapes.join(single_X, Shapes.box(0.0625, 0, 0.46875, 0.125, 1, 0.53125), BooleanOp.OR);

        single_X_U = Shapes.join(single_X_U, Shapes.box(0.0625, 0, 0.46875, 0.125, 0.625, 0.53125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0.03125, 0.25, 0.296875, 0.09375, 0.921875, 0.703125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0.03125, 0, 0.1875, 0.09375, 0.25, 0.8125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0, 0.625, 0.46875, 1, 0.6875, 0.53125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0.875, 0, 0.46875, 0.9375, 0.625, 0.53125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0.90625, 0, 0.1875, 0.96875, 0.25, 0.8125), BooleanOp.OR);
        single_X_U = Shapes.join(single_X_U, Shapes.box(0.90625, 0.25, 0.296875, 0.96875, 0.921875, 0.703125), BooleanOp.OR);

        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0, 0.8125, 0.4684375, 1, 0.875, 0.5315625), BooleanOp.OR);
        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0.03125, 0, -0.25, 0.09375, 0.25, 1.25), BooleanOp.OR);
        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0.03125, 0.25, -0.125, 0.09375, 0.5, 1.125), BooleanOp.OR);
        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0.03125, 0.5, -0.015625, 0.09375, 0.75, 1.015625), BooleanOp.OR);
        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0.03125, 0.75, 0.078125, 0.09375, 1, 0.921875), BooleanOp.OR);
        multiple_X_L = Shapes.join(multiple_X_L, Shapes.box(0.0625, 0, 0.46875, 0.125, 1, 0.53125), BooleanOp.OR);

        multiple_X_L_U = Shapes.join(multiple_X_L_U, Shapes.box(0.0625, 0, 0.46875, 0.125, 0.625, 0.53125), BooleanOp.OR);
        multiple_X_L_U = Shapes.join(multiple_X_L_U, Shapes.box(0.03125, 0.25, 0.296875, 0.09375, 0.921875, 0.703125), BooleanOp.OR);
        multiple_X_L_U = Shapes.join(multiple_X_L_U, Shapes.box(0.03125, 0, 0.1875, 0.09375, 0.25, 0.8125), BooleanOp.OR);
        multiple_X_L_U = Shapes.join(multiple_X_L_U, Shapes.box(0, 0.625, 0.46875, 1, 0.6875, 0.53125), BooleanOp.OR);

        single_Z = ShapeHelper.rotateShape(single_X, 90);
        single_Z_U = ShapeHelper.rotateShape(single_X_U, 90);
        multiple_X_R = ShapeHelper.rotateShape(multiple_X_L, 180);
        multiple_Z_L = ShapeHelper.rotateShape(multiple_X_L, 90);
        multiple_Z_R = ShapeHelper.rotateShape(multiple_Z_L, 180);
        multiple_X_R_U = ShapeHelper.rotateShape(multiple_X_L_U, 180);
        multiple_Z_L_U = ShapeHelper.rotateShape(multiple_X_L_U, 90);
        multiple_Z_R_U = ShapeHelper.rotateShape(multiple_Z_L_U, 180);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(AXIS);
        pBuilder.add(HALF);
        pBuilder.add(WATERLOGGED);
        pBuilder.add(DRYING_STATE);
        pBuilder.add(L_R_HALF);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState pState)
    {
        return !pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE);
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom)
    {
        if (pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE)) return; //保险措施
        RandomSource random = pLevel.getRandom();
        if (random.nextInt(10) <= 5)
        {
            pLevel.setBlock(pPos, pState.setValue(DRYING_STATE, pLevel.isRainingAt(pPos) ? RiceDryingState.WET : RiceDryingState.DRY), 3);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult)
    {
        if (pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE) && pStack.is(ItemRegistryHandler.RICE_CROP))
        {
            pLevel.setBlock(pPos, pState.setValue(DRYING_STATE, RiceDryingState.WET), 3);
            pLevel.playSound(pPlayer, pPos, SoundEvents.CHERRY_LEAVES_PLACE, SoundSource.BLOCKS);
            if (!pPlayer.isCreative()) pStack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }
        else if (!pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE))
        {
            popResource(pLevel, pPos, new ItemStack(pState.getValue(DRYING_STATE).equals(RiceDryingState.WET) ? ItemRegistryHandler.RICE_CROP.asItem() : ItemRegistryHandler.DRIED_RICE_CROP.asItem()));
            pLevel.setBlock(pPos, pState.setValue(DRYING_STATE, RiceDryingState.NONE), 3);
            pLevel.playSound(pPlayer, pPos, SoundEvents.CHERRY_LEAVES_BREAK, SoundSource.BLOCKS);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
    {
        if (!pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE))
            popResource((Level) pLevel, pPos, new ItemStack(pState.getValue(DRYING_STATE).equals(RiceDryingState.WET) ? ItemRegistryHandler.RICE_CROP.asItem() : ItemRegistryHandler.DRIED_RICE_CROP.asItem()));
        super.destroy(pLevel, pPos, pState);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction.Axis axis = state.getValue(AXIS);
        BlockState left = worldIn.getBlockState(pos.relative(axis, -1));
        BlockState right = worldIn.getBlockState(pos.relative(axis, 1));
        if
        (
            !(right.is(this) && state.getValue(L_R_HALF).equals(DryingSticksHalf.LEFT) && right.getValue(L_R_HALF).equals(DryingSticksHalf.RIGHT))
            || (left.is(this) && state.getValue(L_R_HALF).equals(DryingSticksHalf.RIGHT) && left.getValue(L_R_HALF).equals(DryingSticksHalf.LEFT))
        )
        {
            worldIn.setBlock(pos, initConnection(state, left, right), 3);
        }

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.LOWER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            }
        }
        else if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
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
        if (!context.getLevel().getBlockState(context.getClickedPos().above()).canBeReplaced(context)) return null;
        BlockState stateS = super.getStateForPlacement(context);
        stateS = stateS == null ? null : stateS.setValue(AXIS, context.getHorizontalDirection().getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X);
        Direction.Axis axis = stateS.getValue(AXIS);
        BlockState left = context.getLevel().getBlockState(context.getClickedPos().relative(axis, -1));
        BlockState right = context.getLevel().getBlockState(context.getClickedPos().relative(axis, 1));
        return initConnection(stateS, left, right);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlock
        (
            pos.above(),
            state
            .setValue(HALF, DoubleBlockHalf.UPPER)
            .setValue(WATERLOGGED, worldIn.getFluidState(pos.above()).getType().equals(Fluids.WATER))
            , 3
        );
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos)
    {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        VoxelShape shape;
        if (pState.getValue(L_R_HALF).equals(DryingSticksHalf.SINGLE))
        {
            if (pState.getValue(HALF).equals(DoubleBlockHalf.UPPER))
            {
                shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? single_X_U : single_Z_U;
            }
            else shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? single_X : single_Z;
        }
        else
        {
            if (pState.getValue(L_R_HALF).equals(DryingSticksHalf.LEFT))
            {
                if (pState.getValue(HALF).equals(DoubleBlockHalf.UPPER))
                {
                    shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? multiple_X_L_U : multiple_Z_L_U;
                }
                else shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? multiple_X_L : multiple_Z_L;
            }
            else
            {
                if (pState.getValue(HALF).equals(DoubleBlockHalf.UPPER))
                {
                    shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? multiple_X_R_U : multiple_Z_R_U;
                }
                else shape = pState.getValue(AXIS).equals(Direction.Axis.X) ? multiple_X_R : multiple_Z_R;
            }
        }
        if (!pState.getValue(DRYING_STATE).equals(RiceDryingState.NONE))
        {
            if (pState.getValue(HALF).equals(DoubleBlockHalf.UPPER)) shape = Shapes.or(shape, pState.getValue(AXIS).equals(Direction.Axis.X) ? rice_X_U : rice_Z_U);
            else shape = Shapes.or(shape, pState.getValue(AXIS).equals(Direction.Axis.X) ? rice_X : rice_Z);
        }

        return shape;
    }

    protected BlockState initConnection(BlockState state, BlockState left, BlockState right)
    {
        if (left.is(this) && !left.getValue(L_R_HALF).equals(DryingSticksHalf.RIGHT)) return state.setValue(L_R_HALF, DryingSticksHalf.RIGHT);
        else if (right.is(this) && !right.getValue(L_R_HALF).equals(DryingSticksHalf.LEFT)) return state.setValue(L_R_HALF, DryingSticksHalf.LEFT);
        else return state.setValue(L_R_HALF, DryingSticksHalf.SINGLE);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    enum RiceDryingState implements StringRepresentable
    {
        NONE("none"),
        WET("wet"),
        DRY("dry");

        public final String name;

        RiceDryingState(String name) {this.name = name;}

        @Override
        public String getSerializedName() {return this.name;}
    }

    enum DryingSticksHalf implements StringRepresentable
    {
        LEFT("left"),
        RIGHT("right"),
        SINGLE("single");

        public final String name;

        DryingSticksHalf(String name) {this.name = name;}

        @Override
        public String getSerializedName() {return this.name;}
    }
}
