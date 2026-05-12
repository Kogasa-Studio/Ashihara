package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import static kogasastudio.ashihara.block.AdvancedFenceBlock.*;
import static kogasastudio.ashihara.utils.AshiharaTags.ADVANCED_FENCES;

public class FenceDecorationBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty ORB = BooleanProperty.create("orb");

    public FenceDecorationBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ORB, false));
    }

    public FenceDecorationBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.GOLD)
            .strength(0.2F)
            .sound(SoundType.LANTERN)
            .noOcclusion()
        );
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
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random)
    {
        return !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
    {
        BlockState fence = level.getBlockState(pos.below());

        if (!this.canSurvive(state, level, pos)) return;

        if (fence.getValue(COLUMN).equals(AdvancedFenceBlock.ColumnType.CORE))
        {
            level.setBlockAndUpdate(pos, state.setValue(ORB, true));
        } else
        {
            if (fence.getValue(NORTH) && fence.getValue(SOUTH) && fence.getValue(EAST) && fence.getValue(WEST))
                level.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.Y));
            else if (fence.getValue(NORTH) && fence.getValue(SOUTH))
                level.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.Z));
            else if (fence.getValue(EAST) && fence.getValue(WEST))
                level.setBlockAndUpdate(pos, state.setValue(AXIS, Direction.Axis.X));
        }
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        // todo 前三是最小值
        //VoxelShape x = box(3.5d, -0.1d, 6.5d, 12.5d, -0.95d, 9.5d);
        VoxelShape x = box(2.0d, -1.25d, 6.5d, 14.0d, -0.25d, 9.5d);
        // VoxelShape z = box(6.5d, -0.1d, 3.5d, 9.5d, -0.95d, 12.5d);
        VoxelShape z = box(6.5d, -1.25d, 2.0d, 9.5d, -0.25d, 14.0d);
        VoxelShape cross = Shapes.or(x, z);

        VoxelShape orb = box(5.5d, -1.0d, 5.5d, 10.5d, 9.5d, 10.5d);

        if (state.getValue(ORB)) return orb;
        else return state.getValue(AXIS).equals(Direction.Axis.X) ? x : state.getValue(AXIS).equals(Direction.Axis.Y) ? cross : z;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
    {
        return new ItemStack(Items.GOLD_INGOT);
    }
}
