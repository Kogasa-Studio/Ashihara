package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static kogasastudio.ashihara.block.AdvancedFenceBlock.*;
import static kogasastudio.ashihara.utils.AshiharaTags.ADVANCED_FENCES;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class FenceDecorationBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;
    public static final BooleanProperty ORB = BooleanProperty.create("orb");

    public FenceDecorationBlock()
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS, ORB);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).is(ADVANCED_FENCES);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState fence = worldIn.getBlockState(pos.below());

        if (!this.canSurvive(state, worldIn, pos)) return;

        if (fence.getValue(COLUMN).equals(AdvancedFenceBlock.ColumnType.CORE))
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ORB, true));
        } else
        {
            if (fence.getValue(NORTH) && fence.getValue(SOUTH))
                worldIn.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.Z));
            else if (fence.getValue(EAST) && fence.getValue(WEST))
                worldIn.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.X));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        // todo 前三是最小值
        //VoxelShape x = box(3.5d, -0.1d, 6.5d, 12.5d, -0.95d, 9.5d);
        VoxelShape x = box(3.5d, -0.95d, 6.5d, 12.5d, -0.1d, 9.5d);
        // VoxelShape z = box(6.5d, -0.1d, 3.5d, 9.5d, -0.95d, 12.5d);
        VoxelShape z = box(6.5d, -0.95d, 3.5d, 9.5d, -0.1d, 12.5d);

        VoxelShape orb = box(5.5d, -1.0d, 5.5d, 10.5d, 9.5d, 10.5d);

        if (state.getValue(ORB)) return orb;
        else return state.getValue(AXIS).equals(Direction.Axis.X) ? x : z;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
        return new ItemStack(Items.GOLD_INGOT);
    }
}
