package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import static net.minecraft.block.Blocks.STRIPPED_OAK_LOG;

public class StrippableLogBlock extends SimpleLogBlock
{
    public ItemStack getStripItem() {return ItemStack.EMPTY;}

    public Block getStrippedBlock() {return STRIPPED_OAK_LOG;}

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (player.getItemInHand(handIn).getItem() instanceof AxeItem)
        {
            worldIn.playSound(player, pos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!worldIn.isClientSide())
            {
                Block block = this.getStrippedBlock();
                BlockState stripped = block.defaultBlockState().hasProperty(AXIS) ? block.defaultBlockState().setValue(AXIS, state.getValue(AXIS)) : block.defaultBlockState();
                worldIn.setBlockAndUpdate(pos, stripped);
                if (!this.getStripItem().isEmpty()) worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), this.getStripItem()));
                if (!player.isCreative()) player.getItemInHand(handIn).hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(handIn));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
