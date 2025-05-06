package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.helper.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MortarBlock extends Block implements EntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final VoxelShape SHAPE = Shapes.or
    (
        box(2,0,2,14,2,14),
        box(2,2,2,14,13,4),
        box(2,2,12,14,13,14),
        box(12,2,4,14,13,12),
        box(2,2,4,4,13,12)
    );

    public MortarBlock()
    {
        super
        (
            Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(3.0F)
            // todo tag .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
            .noOcclusion()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state)
    {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MortarTE mortarTE && !mortarTE.inventory.isEmpty())
        {
            for (ItemStack stack : mortarTE.inventory.getAllContents())
            {
                popResource((Level) level, pos, stack);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        MortarTE te = (MortarTE) worldIn.getBlockEntity(pos);
        if (te != null)
        {
            te.pushLastLiquidLevel();
            if (FluidHelper.notifyFluidTankInteraction(player, handIn, stack, te.fluidTank, worldIn, pos))
            {
                te.switchFluid.switchRender(player);
                player.getInventory().setChanged();
                te.setChanged();
                te.updateBlock();
                te.refreshRecipe();
                return ItemInteractionResult.SUCCESS;
            }
            if (InventoryHelper.interactWithInventory(te.inventory, stack, player, handIn, 64))
            {
                player.getInventory().setChanged();
                te.setChanged();
                te.updateBlock();
                te.refreshRecipe();
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new MortarTE(pos, state);
    }
}
