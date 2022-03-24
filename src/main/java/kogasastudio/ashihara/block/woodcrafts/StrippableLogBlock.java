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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (player.getHeldItem(handIn).getItem() instanceof AxeItem)
        {
            worldIn.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!worldIn.isRemote())
            {
                Block block = this.getStrippedBlock();
                BlockState stripped = block.getDefaultState().hasProperty(AXIS) ? block.getDefaultState().with(AXIS, state.get(AXIS)) : block.getDefaultState();
                worldIn.setBlockState(pos, stripped);
                if (!this.getStripItem().isEmpty()) worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), this.getStripItem()));
                if (!player.isCreative()) player.getHeldItem(handIn).damageItem(1, player, (playerIn) -> playerIn.sendBreakAnimation(handIn));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
