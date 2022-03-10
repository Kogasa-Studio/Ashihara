package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import static kogasastudio.ashihara.block.BlockAdvancedFence.*;
import static kogasastudio.ashihara.utils.AshiharaTags.ADVANCED_FENCES;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class BlockFenceDecoration extends Block
{
    public BlockFenceDecoration()
    {
        super
        (
            Properties.create(Material.IRON, MaterialColor.GOLD)
            .hardnessAndResistance(0.2F)
            .sound(SoundType.LANTERN)
            .notSolid()
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(ORB, false));
    }

    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;
    public static final BooleanProperty ORB = BooleanProperty.create("orb");

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS, ORB);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isIn(ADVANCED_FENCES);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState()
                : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState fence = worldIn.getBlockState(pos.down());

        if (!this.isValidPosition(state, worldIn, pos)) return;

        if (fence.get(COLUMN).equals(BlockAdvancedFence.ColumnType.CORE))
        {
            worldIn.setBlockState(pos, state.with(ORB, true));
        }
        else
        {
            if (fence.get(NORTH) && fence.get(SOUTH)) worldIn.setBlockState(pos, state.with(AXIS, Direction.Axis.Z));
            else if (fence.get(EAST) && fence.get(WEST)) worldIn.setBlockState(pos, state.with(AXIS, Direction.Axis.X));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape x = makeCuboidShape(3.5d, -0.1d, 6.5d, 12.5d, -0.95d, 9.5d);
        VoxelShape z = makeCuboidShape(6.5d, -0.1d, 3.5d, 9.5d, -0.95d, 12.5d);

        VoxelShape orb = makeCuboidShape(5.5d, -1.0d, 5.5d, 10.5d, 9.5d, 10.5d);

        if (state.get(ORB)) return orb;
        else return state.get(AXIS).equals(Direction.Axis.X) ? x : z;
    }
}
