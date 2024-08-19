package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.item.Items.BONE_MEAL;

public class AbstractCropAge7Pickable extends AbstractCropAge7
{
    protected final int ageAvailable;
    protected final int ageTurnIn;

    public AbstractCropAge7Pickable(int max, int turn)
    {
        super();
        this.ageAvailable = max;
        this.ageTurnIn = turn;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack pStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        int age = state.getValue(AGE);
        ItemStack stack = player.getItemInHand(handIn);
        if (age < this.ageAvailable && stack.getItem().equals(BONE_MEAL)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (age == this.ageAvailable)
        {
            if (!worldIn.isClientSide())
            {
                for (ItemStack stack1 : getDrops(state, (ServerLevel) worldIn, pos, null))
                {
                    popResource(worldIn, pos, stack1);
                }
            }
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, state.setValue(AGE, this.ageTurnIn));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, state, worldIn, pos, player, handIn, hit);
    }
}
