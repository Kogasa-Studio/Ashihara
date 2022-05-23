package kogasastudio.ashihara.inventory.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class GenericItemStackHandler extends ItemStackHandler {
    public GenericItemStackHandler(int numSlots) {
        super(numSlots);
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void addItem(ItemStack stack) {
        for (int i = 0; i < this.getSlots(); i += 1) {
            if (insertItem(i, stack, false).isEmpty()) {
                return;
            }
        }
    }

    /**
     * 警告！不应该直接通过此方法修改内容
     */
    public NonNullList<ItemStack> getContent() {
        return this.stacks;
    }

    public int getActualSize() {
        int i = 0;
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                i += 1;
            }
        }
        return i;
    }

    public void clear() {
        this.stacks = NonNullList.withSize(this.getSlots(), ItemStack.EMPTY);
    }

    @Override
    public String toString() {
        return this.stacks.toString();
    }
}
