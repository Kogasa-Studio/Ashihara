package kogasastudio.ashihara.block.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuttingBoardTE extends AshiharaMachineTE
{
    private ItemStack content = ItemStack.EMPTY;

    public CuttingBoardTE() {super(TERegistryHandler.CUTTING_BOARD_TE.get());}

    public ItemStack getContent() {return this.content.copy();}

    public void matchesRecipe() {}

    public void cut() {}

    public boolean handleInteraction(PlayerEntity playerIn, Hand handIn, World worldIn, BlockPos posIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (this.content != ItemStack.EMPTY && stack.isEmpty())
        {
            playerIn.setHeldItem(handIn, this.content);
            this.content = ItemStack.EMPTY;
            worldIn.playSound(playerIn, posIn, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        else if (this.content.isEmpty())
        {
            this.content = stack.split(Math.min(stack.getCount(), 4));
            worldIn.playSound(playerIn, posIn, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.content = ItemStack.read(nbt.getCompound("content"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.put("content", this.content.write(new CompoundNBT()));
        super.write(compound);
        return compound;
    }
}
