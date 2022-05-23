package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG;

public class StrippableLogBlock extends SimpleLogBlock {
    public ItemStack getStripItem() {
        return ItemStack.EMPTY;
    }

    public Block getStrippedBlock() {
        return STRIPPED_OAK_LOG;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getItemInHand(handIn).getItem() instanceof AxeItem) {
            worldIn.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!worldIn.isClientSide()) {
                Block block = this.getStrippedBlock();
                BlockState stripped = block.defaultBlockState().hasProperty(AXIS) ? block.defaultBlockState().setValue(AXIS, state.getValue(AXIS)) : block.defaultBlockState();
                worldIn.setBlockAndUpdate(pos, stripped);
                if (!this.getStripItem().isEmpty()) {
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), this.getStripItem()));
                }
                if (!player.isCreative()) {
                    player.getItemInHand(handIn).hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(handIn));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
