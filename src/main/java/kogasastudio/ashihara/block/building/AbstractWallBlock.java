package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.utils.WallTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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

public abstract class AbstractWallBlock extends Block implements SimpleWaterloggedBlock, IExpandable, IVariable<WallTypes>
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    VoxelShape MAIN_X = Block.box(0d, 0d, 7d, 16d, 16d, 9d);
    VoxelShape L_X = Block.box(16d, 0d, 7d, 24d, 16d, 9d);
    VoxelShape R_X = Block.box(-8d, 0d, 7d, 0d, 16d, 9d);
    VoxelShape U_X = Block.box(0d, 16d, 7d, 16d, 24d, 9d);
    VoxelShape D_X = Block.box(0d, -8d, 7d, 16d, 0d, 9d);
    VoxelShape LU_X = Block.box(16d, 16d, 7d, 24d, 24d, 9d);
    VoxelShape LD_X = Block.box(16d, -8d, 7d, 24d, 0d, 9d);
    VoxelShape RU_X = Block.box(-8d, 16d, 7d, 0d, 24d, 9d);
    VoxelShape RD_X = Block.box(-8d, -8d, 7d, 0d, 0d, 9d);
    VoxelShape MAIN_Z = Block.box(7d, 0d, 0d, 9d, 16d, 16d);
    VoxelShape L_Z = Block.box(7d, 0d, 16d, 9d, 16d, 24d);
    VoxelShape R_Z = Block.box(7d, 0d, -8d, 9d, 16d, 0d);
    VoxelShape U_Z = Block.box(7d, 16d, 0d, 9d, 24d, 16d);
    VoxelShape D_Z = Block.box(7d, -8d, 0d, 9d, 0d, 16d);
    VoxelShape LU_Z = Block.box(7d, 16d, 16d, 9d, 24d, 24d);
    VoxelShape LD_Z = Block.box(7d, -8d, 16d, 9d, 0d, 24d);
    VoxelShape RU_Z = Block.box(7d, 16d, -8d, 9d, 24d, 0d);
    VoxelShape RD_Z = Block.box(7d, -8d, -8d, 9d, 0d, 0d);

    public AbstractWallBlock(Properties p_49795_)
    {
        super(p_49795_);
    }

    public AbstractWallBlock()
    {
        this
        (
                Properties.of()
                        .mapColor(DyeColor.WHITE)
                        .strength(1.5f)
                        .sound(SoundType.BAMBOO)
        );
        this.registerDefaultState
        (
                defaultBlockState()
                        .setValue(WATERLOGGED, false)
                        .setValue(AXIS, Direction.Axis.X)
                        .setValue(L, false)
                        .setValue(R, false)
                        .setValue(U, false)
                        .setValue(D,false)
        );
    }

    public BlockState updateState(Level level, BlockPos pos, BlockState state)
    {
        Direction left = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.LEFT);
        Direction right = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.RIGHT);

        BlockState l = level.getBlockState(pos.relative(left));
        BlockState r = level.getBlockState(pos.relative(right));
        BlockState u = level.getBlockState(pos.above());
        BlockState d = level.getBlockState(pos.below());

        state = this.expand(RelativeDirection.LEFT, state, !l.isAir() && !ShapeHelper.canBlockSupport(l, level, pos.relative(left), left.getOpposite(), ShapeHelper.getWallShape(right, state.getValue(AXIS))));
        state = this.expand(RelativeDirection.RIGHT, state, !r.isAir() && !ShapeHelper.canBlockSupport(r, level, pos.relative(right), right.getOpposite(), ShapeHelper.getWallShape(left, state.getValue(AXIS))));
        state = this.expand(RelativeDirection.UP, state, !u.isAir() && !ShapeHelper.canBlockSupport(u, level, pos.above(), Direction.DOWN, ShapeHelper.getWallShape(Direction.DOWN, state.getValue(AXIS))));
        state = this.expand(RelativeDirection.DOWN, state, !d.isAir() && !ShapeHelper.canBlockSupport(d, level, pos.below(), Direction.UP, ShapeHelper.getWallShape(Direction.UP, state.getValue(AXIS))));

        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape;
        if(state.getValue(AXIS).equals(Direction.Axis.X))
        {
            shape = MAIN_X;
            if(state.getValue(L)) shape = Shapes.or(shape, L_X);
            if(state.getValue(R)) shape = Shapes.or(shape, R_X);
            if(state.getValue(U)) shape = Shapes.or(shape, U_X);
            if(state.getValue(D)) shape = Shapes.or(shape, D_X);
            if(state.getValue(L) && state.getValue(U)) shape = Shapes.or(shape, LU_X);
            if(state.getValue(L) && state.getValue(D)) shape = Shapes.or(shape, LD_X);
            if(state.getValue(R) && state.getValue(U)) shape = Shapes.or(shape, RU_X);
            if(state.getValue(R) && state.getValue(D)) shape = Shapes.or(shape, RD_X);
            return shape;
        }
        else
        {
            shape = MAIN_Z;
            if(state.getValue(L)) shape = Shapes.or(shape, L_Z);
            if(state.getValue(R)) shape = Shapes.or(shape, R_Z);
            if(state.getValue(U)) shape = Shapes.or(shape, U_Z);
            if(state.getValue(D)) shape = Shapes.or(shape, D_Z);
            if(state.getValue(L) && state.getValue(U)) shape = Shapes.or(shape, LU_Z);
            if(state.getValue(L) && state.getValue(D)) shape = Shapes.or(shape, LD_Z);
            if(state.getValue(R) && state.getValue(U)) shape = Shapes.or(shape, RU_Z);
            if(state.getValue(R) && state.getValue(D)) shape = Shapes.or(shape, RD_Z);
            return shape;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED).add(AXIS).add(L).add(R).add(U).add(D);
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
