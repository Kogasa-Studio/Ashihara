package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FermentationVatBlock extends FermentationBlock
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Base shapes (BlockBench default: SOUTH-facing)
    private static final VoxelShape BASE_SHAPE = Shapes.or(
        Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.9375, 0.9375),
        Shapes.box(0.03125, 0.9375, 0.03125, 0.96875, 1, 0.96875),
        Shapes.box(0.0375, 1, 0.21875, 0.9625, 1.0625, 0.34375),
        Shapes.box(0.0375, 1, 0.65625, 0.9625, 1.0625, 0.78125)
    );

    private static final VoxelShape BASE_NO_LID = Shapes.or(
        Shapes.box(0.125, 0, 0.125, 0.875, 0.09375, 0.875),
        Shapes.box(0.875, 0, 0.0625, 0.9375, 0.9375, 0.9375),
        Shapes.box(0.0625, 0, 0.0625, 0.125, 0.9375, 0.9375),
        Shapes.box(0.125, 0, 0.0625, 0.875, 0.9375, 0.125),
        Shapes.box(0.125, 0, 0.875, 0.875, 0.9375, 0.9375)
    );

    // [lid][dir]: lid=hasLid?1:0, dir=Direction.get2DDataValue()
    private static final VoxelShape[][] SHAPES = new VoxelShape[2][4];

    // Rotation from BlockBench default (SOUTH=0) to each direction.get2DDataValue() (S=0,W=1,N=2,E=3)
    private static final double[] ROTATIONS = {0, 90, 180, 270};

    static
    {
        for (int dir = 0; dir < 4; dir++)
        {
            SHAPES[0][dir] = ShapeHelper.rotateShape(BASE_NO_LID, Direction.Axis.Y, 0.5, 0.5, ROTATIONS[dir]);
            SHAPES[1][dir] = ShapeHelper.rotateShape(BASE_SHAPE, Direction.Axis.Y, 0.5, 0.5, ROTATIONS[dir]);
        }
    }

    public FermentationVatBlock(BlockBehaviour.Properties properties)
    {
        super(properties, FermentationBlockEntity.Size.VAT);
        registerDefaultState(stateDefinition.any().setValue(HAS_LID, true).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getFullShape(boolean hasLid)
    {
        return SHAPES[hasLid ? 1 : 0][0]; // Default shape (no per-state rotation needed; getShape is overridden)
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext ctx)
    {
        int dir = state.getValue(FACING).get2DDataValue();
        return SHAPES[state.getValue(HAS_LID) ? 1 : 0][dir];
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hit.getDirection() == Direction.UP && stack.isEmpty())
        {
            boolean lidOpen = state.getValue(HAS_LID);
            level.playSound(player, pos, lidOpen ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, state.setValue(HAS_LID, !lidOpen));
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}