package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.building.component.ModelStateDefinition;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.item.block.BuildingComponentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseMultiBuiltBlock extends Block implements EntityBlock, SimpleWaterloggedBlock
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    VoxelShape debug = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);

    public BaseMultiBuiltBlock()
    {
        super
        (
            Properties.of()
            .sound(SoundType.EMPTY)
        );
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams)
    {
        BlockEntity be = pParams.getParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> ret = new ArrayList<>();
        if (be instanceof MultiBuiltBlockEntity mbe)
        {
            for (ModelStateDefinition model : mbe.getModels())
            {
                ret.addAll(model.component().drops);
            }
        }
        return ret.isEmpty() ? super.getDrops(pState, pParams) : ret;
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston)
    {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (be instanceof MultiBuiltBlockEntity mbe) mbe.checkConnection();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult)
    {
        UseOnContext context = new UseOnContext(pLevel, pPlayer, pHand, pStack, pHitResult);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MultiBuiltBlockEntity be)
        {
            if (pStack.getItem() instanceof BuildingComponentItem componentItem && be.tryPlace(context, componentItem.getComponent())) return ItemInteractionResult.SUCCESS;
            else if ((pStack.is(ItemRegistryHandler.WOODEN_HAMMER) || pStack.is(ItemRegistryHandler.CHISEL)) && be.tryBreak(context))
            {
                if (be.MODEL_STATES.isEmpty()) pLevel.removeBlock(pPos, false);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MultiBuiltBlockEntity be)
        {
            if (!be.getShape().isEmpty()) return be.getShape();
        }
        return debug;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(FACING, WATERLOGGED);
        super.createBlockStateDefinition(pBuilder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        BlockState state = super.getStateForPlacement(pContext);
        state = state.setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new MultiBuiltBlockEntity(pPos, pState);
    }
}
