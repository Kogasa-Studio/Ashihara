package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

    public CuttingBoardBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOD)
                                .sound(SoundType.WOOD)
                                .strength(0.4F)
                );
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState state1, boolean b)
    {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof CuttingBoardTE)
        {
            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((CuttingBoardTE) te).getContent());
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, state1, b);
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
    public ItemInteractionResult useItemOn(ItemStack pStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        BlockEntity teIn = worldIn.getBlockEntity(pos);
        if (teIn == null || !teIn.getType().equals(TERegistryHandler.CUTTING_BOARD_TE.get())) return ItemInteractionResult.FAIL;
        CuttingBoardTE te = (CuttingBoardTE) teIn;
        if (te.handleInteraction(player, handIn, worldIn, pos)) return ItemInteractionResult.SUCCESS;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape x = box(2.0d, 0.0d, 1.0d, 14.0d, 1.0d, 15.0d);
        VoxelShape z = box(1.0d, 0.0d, 2.0d, 15.0d, 1.0d, 14.0d);

        return state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? x : z;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new CuttingBoardTE(pPos, pState);
    }
}
