package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class StraightBarWindowBlock extends Block implements SimpleWaterloggedBlock, IExpandable
{
    public static final BooleanProperty UP_EDGE = BooleanProperty.create("up");
    public static final BooleanProperty DOWN_EDGE = BooleanProperty.create("down");
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StraightBarWindowBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UP_EDGE, false).setValue(DOWN_EDGE, false).setValue(L, false).setValue(R, false).setValue(WATERLOGGED, false));
    }

    VoxelShape MAIN_X = Block.box(0d, 0d, 7.5d, 16d, 16d, 8.5d);
    VoxelShape L_X = Block.box(16d, 0d, 7.5d, 24d, 16d, 8.5d);
    VoxelShape R_X = Block.box(-8d, 0d, 7.5d, 0d, 16d, 8.5d);
    VoxelShape MAIN_Z = Block.box(7.5d, 0d, 0d, 8.5d, 16d, 16d);
    VoxelShape L_Z = Block.box(7.5d, 0d, 16d, 8.5d, 16d, 24d);
    VoxelShape R_Z = Block.box(7.5d, 0d, -8d, 8.5d, 16d, 0d);

    VoxelShape EDGE_U_X = Block.box(0d, 14d, 7d, 16d, 16d, 9d);
    VoxelShape EDGE_D_X = Block.box(0d, 0d, 7d, 16d, 2d, 9d);
    VoxelShape EDGE_LU_X = Block.box(16d, 14d, 7d, 24d, 16d, 9d);
    VoxelShape EDGE_RU_X = Block.box(-8d, 14d, 7d, 0d, 16d, 9d);
    VoxelShape EDGE_LD_X = Block.box(16d, 0d, 7d, 24d, 2d, 9d);
    VoxelShape EDGE_RD_X = Block.box(-8d, 0d, 7d, 0d, 2d, 9d);

    VoxelShape EDGE_U_Z = Block.box(7d, 14d, 0d, 9d, 16d, 16d);
    VoxelShape EDGE_D_Z = Block.box(7d, 0d, 0d, 9d, 2d, 16d);
    VoxelShape EDGE_LU_Z = Block.box(7d, 14d, 16d, 9d, 16d, 24d);
    VoxelShape EDGE_RU_Z = Block.box(7d, 14d, -8d, 9d, 16d, 0d);
    VoxelShape EDGE_LD_Z = Block.box(7d, 0d, 16d, 9d, 2d, 24d);
    VoxelShape EDGE_RD_Z = Block.box(7d, 0d, -8d, 9d, 2d, 0d);

    public BlockState updateState(Level level, BlockPos pos, BlockState state)
    {
        Direction left = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.LEFT);
        Direction right = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.RIGHT);

        BlockState l = level.getBlockState(pos.relative(left));
        BlockState r = level.getBlockState(pos.relative(right));

        state = expand(RelativeDirection.LEFT, state, !l.isAir() && !ShapeHelper.canBlockSupport(l, level, pos.relative(left), left.getOpposite(), ShapeHelper.getWallShape(right, state.getValue(AXIS))));
        state = expand(RelativeDirection.RIGHT, state, !r.isAir() && !ShapeHelper.canBlockSupport(r, level, pos.relative(right), right.getOpposite(), ShapeHelper.getWallShape(left, state.getValue(AXIS))));
        state = state.setValue(UP_EDGE, !level.getBlockState(pos.above()).is(this.asBlock()));
        state = state.setValue(DOWN_EDGE, !level.getBlockState(pos.below()).is(this.asBlock()));

        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = state.getValue(AXIS).equals(Direction.Axis.X) ? this.MAIN_X : this.MAIN_Z;
        boolean l = state.getValue(L);
        boolean r = state.getValue(R);
        boolean u_edge = state.getValue(UP_EDGE);
        boolean d_edge = state.getValue(DOWN_EDGE);

        if (state.getValue(AXIS) == Direction.Axis.Z)
        {
            shape = Shapes.or(shape, l ? L_Z : Shapes.empty(), r ? R_Z : Shapes.empty());
            if (u_edge) shape = Shapes.or(shape, EDGE_U_Z, l ? EDGE_LU_Z : Shapes.empty(), r ? EDGE_RU_Z : Shapes.empty());
            if (d_edge) shape = Shapes.or(shape, EDGE_D_Z, l ? EDGE_LD_Z : Shapes.empty(), r ? EDGE_RD_Z : Shapes.empty());
        }
        else
        {
            shape = Shapes.or(shape, l ? L_X : Shapes.empty(), r ? R_X : Shapes.empty());
            if (u_edge) shape = Shapes.or(shape, EDGE_U_X, l ? EDGE_LU_X : Shapes.empty(), r ? EDGE_RU_X : Shapes.empty());
            if (d_edge) shape = Shapes.or(shape, EDGE_D_X, l ? EDGE_LD_X : Shapes.empty(), r ? EDGE_RD_X : Shapes.empty());
        }

        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(L, R, UP_EDGE, DOWN_EDGE, AXIS, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState updated = defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X);
        updated = this.updateState(context.getLevel(), context.getClickedPos(), updated);
        return updated;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean b)
    {
        BlockState updated = this.updateState(level, pos, state);
        if (!updated.equals(state)) level.setBlockAndUpdate(pos, updated);
        super.neighborChanged(state, level, pos, block, neighborPos, b);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
