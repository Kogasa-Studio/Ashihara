package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.CuttingBoardBE;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CuttingBoardBlock extends Block implements EntityBlock
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape X = box(2.0d, 0.0d, 1.0d, 14.0d, 2.0d, 15.0d);
    public static final VoxelShape Z = box(1.0d, 0.0d, 2.0d, 15.0d, 2.0d, 14.0d);

    public CuttingBoardBlock(Properties properties)
    {
        super(properties);
    }

    public CuttingBoardBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD)
            .strength(0.4F)
        );
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state)
    {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CuttingBoardBE)
        {
            Containers.dropItemStack((Level) level, pos.getX(), pos.getY(), pos.getZ(), ((CuttingBoardBE) be).getContent());
            ((Level) level).updateNeighbourForOutputSignal(pos, this);
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult useItemOn(ItemStack pStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        BlockEntity teIn = worldIn.getBlockEntity(pos);
        if (teIn == null || !teIn.getType().equals(BlockEntities.CUTTING_BOARD_BE.get())) return InteractionResult.FAIL;
        CuttingBoardBE te = (CuttingBoardBE) teIn;
        if (te.handleInteraction(player, handIn, worldIn, pos))
        {
            worldIn.sendBlockUpdated(pos, state, state, UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? X : Z;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new CuttingBoardBE(pPos, pState);
    }
}
