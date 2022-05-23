package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import net.minecraft.block.AbstractBlock.Properties;

public class BlockFenceDecoration extends Block
{
    public BlockFenceDecoration()
    {
        super
        (
            Properties.of(Material.METAL, MaterialColor.GOLD)
            .strength(0.2F)
            .sound(SoundType.LANTERN)
            .noOcclusion()
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(ORB, false));
    }

    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;
    public static final BooleanProperty ORB = BooleanProperty.create("orb");

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS, ORB);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).is(ADVANCED_FENCES);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState fence = worldIn.getBlockState(pos.below());

        if (!this.canSurvive(state, worldIn, pos)) return;

        if (fence.getValue(COLUMN).equals(BlockAdvancedFence.ColumnType.CORE))
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ORB, true));
        }
        else
        {
            if (fence.getValue(NORTH) && fence.getValue(SOUTH)) worldIn.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.Z));
            else if (fence.getValue(EAST) && fence.getValue(WEST)) worldIn.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.X));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape x = box(3.5d, -0.1d, 6.5d, 12.5d, -0.95d, 9.5d);
        VoxelShape z = box(6.5d, -0.1d, 3.5d, 9.5d, -0.95d, 12.5d);

        VoxelShape orb = box(5.5d, -1.0d, 5.5d, 10.5d, 9.5d, 10.5d);

        if (state.getValue(ORB)) return orb;
        else return state.getValue(AXIS).equals(Direction.Axis.X) ? x : z;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {return new ItemStack(Items.GOLD_INGOT);}
}
