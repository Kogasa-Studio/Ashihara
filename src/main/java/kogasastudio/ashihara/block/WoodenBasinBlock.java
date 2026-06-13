package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.helper.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class WoodenBasinBlock extends FermentationBlock
{
    private static final VoxelShape SHAPE = Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.5, 0.9375);

    public WoodenBasinBlock(BlockBehaviour.Properties properties)
    {
        super(properties, FermentationBlockEntity.Size.BASIN);
    }

    @Override
    protected VoxelShape getFullShape(boolean hasLid)
    {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (level.getBlockEntity(pos) instanceof FermentationBlockEntity be)
        {
            if (FluidUtil.interactWithFluidHandler(player, hand, pos, be.fluid) || InventoryHelper.interactWithInventory(be.inventory, player.getItemInHand(hand), player, hand, 64))
            {
                be.setChanged();
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}