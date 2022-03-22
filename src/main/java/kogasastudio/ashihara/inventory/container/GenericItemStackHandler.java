package kogasastudio.ashihara.inventory.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class GenericItemStackHandler extends ItemStackHandler
{
    public GenericItemStackHandler(int numSlots) {super(numSlots);}

    public boolean isEmpty()
    {
        for (ItemStack stack : this.stacks) {if (!stack.isEmpty()) return false;}
        return true;
    }

    public int getActualSize()
    {
        int i = 0;
        for (ItemStack stack : this.stacks) {if (!stack.isEmpty()) i += 1;}
        return i;
    }

    public void clear() {this.stacks = NonNullList.withSize(this.getSlots(), ItemStack.EMPTY);}

    @Override
    public String toString()
    {
        return this.stacks.toString();
    }
}
