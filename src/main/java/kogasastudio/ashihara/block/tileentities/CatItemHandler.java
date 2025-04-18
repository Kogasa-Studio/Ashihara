package kogasastudio.ashihara.block.tileentities;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.HashMap;
import java.util.Map;

public class CatItemHandler implements IItemHandlerModifiable
{
    public final Map<Integer, ItemStack> inventory = new HashMap<Integer, ItemStack>();

    public CatItemHandler(int slots)
    {
        for (int i = 0; i < slots; i++)
        {
            inventory.put(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        inventory.put(slot, stack);
    }

    @Override
    public int getSlots()
    {
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inventory.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        TagKey
        if (inventory.get(slot).isEmpty())
        {
            if (!simulate) inventory.put(slot, stack);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack stack = inventory.get(slot).copy();
        if (simulate) inventory.put(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return true;
    }
}
