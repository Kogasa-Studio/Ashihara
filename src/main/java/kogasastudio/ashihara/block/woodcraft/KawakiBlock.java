package kogasastudio.ashihara.block.woodcraft;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.helper.BlockActionHelper;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class KawakiBlock extends Block implements IVariable<AshiharaWoodTypes>
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ISLONG = BooleanProperty.create("is_long");
    private static AshiharaWoodTypes type;

    public KawakiBlock(Properties properties, AshiharaWoodTypes typeIn)
    {
        super(properties);
        type = typeIn;
    }

    public KawakiBlock(AshiharaWoodTypes typeIn)
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD),
            typeIn
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, ISLONG);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level worldIn = context.getLevel();
        BlockPos posIn = context.getClickedPos();
        Direction facingIn = context.getHorizontalDirection();
        BlockState facingState = worldIn.getBlockState(posIn.relative(facingIn.getOpposite()));

        return this.defaultBlockState()
                .setValue(FACING, facingIn.getOpposite())
                .setValue(ISLONG, facingState.isFaceSturdy(worldIn, posIn.relative(facingIn), facingIn.getOpposite())
                        || canConnect(this.defaultBlockState(), facingState));
    }

    private boolean canConnect(BlockState state, BlockState toCheck)
    {
        return BlockActionHelper.typeMatches(state, toCheck) && (toCheck.getBlock() instanceof KawakiBlock || toCheck.getBlock() instanceof KumimonoBlock);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
    {
        BlockState expandedState = level.getBlockState(pos.relative(state.getValue(FACING)));
        boolean shouldBeLong = expandedState.isFaceSturdy(level, pos.relative(state.getValue(FACING)), state.getValue(FACING).getOpposite())
        || canConnect(state, expandedState);
        if (state.getValue(ISLONG) != shouldBeLong)
        {
            level.setBlockAndUpdate(pos, state.setValue(ISLONG, shouldBeLong));
        }
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return !worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isAir();
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random)
    {
        return !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape n = box(6.0d, 8.0d, 4.0d, 10.0d, 16.0d, 16.0d);
        VoxelShape e = box(0.0d, 8.0d, 6.0d, 12.0d, 16.0d, 10.0d);
        VoxelShape s = box(6.0d, 8.0d, 0.0d, 10.0d, 16.0d, 12d);
        VoxelShape w = box(4.0d, 8.0d, 6.0d, 16.0d, 16.0d, 10.0d);

        if (state.getValue(ISLONG))
        {
            if (state.getValue(FACING).getAxis().equals(Direction.Axis.X)) return Shapes.or(e, w);
            else return Shapes.or(n, s);
        } else
        {
            return switch (state.getValue(FACING))
            {
                case EAST -> e;
                case SOUTH -> s;
                case WEST -> w;
                default -> n;
            };
        }
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return type;
    }
}
