package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import static kogasastudio.ashihara.utils.AshiharaTags.MASHABLE;

public class MortarContainer extends AshiharaCommonContainer
{
    private final MortarTE te;

    public MortarContainer(int id, Inventory inv, MortarTE teIn)
    {
        super(ContainerRegistryHandler.MORTAR_CONTAINER.get(), id);
        this.te = teIn;

        layoutPlayerInventorySlots(inv, 8, 121);
        if (teIn != null)
        {
            addSlotBox(teIn.contents, 0, 80, 26, 1, 18, 4, 18);
            addSlot(new SlotItemHandler(teIn.fluidIO, 0, 21, 85));
            addSlot(new SlotItemHandler(teIn.fluidIO, 1, 148, 85));
        }
    }

    @Override
    protected int addSlotRange(IItemHandler inventory, int index, int x, int y, int amount, int dx)
    {
        for (int i = 0; i < amount; i++)
        {
            addSlot(new MortarSlot(inventory, index, x, y));
            x += dx;
            index += 1;
        }
        return index;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }

    /**
     * 玩家在gui中shift点击时会调用的方法
     *
     * @param playerIn 玩家
     * @param index    玩家点击的物品栏的id
     * @return 玩家右键的物品
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        //玩家点击的具体格子
        Slot slot = this.slots.get(index);

        if (!slot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getItem();
        ItemStack oldStack = newStack.copy();

        boolean isMerged;

        // 0~8: 快捷栏, 9~35: 玩家背包, 36~39: 内容槽, 40~41: 流体互动槽
        if (index < 9)
        {
            isMerged = moveItemStackTo(newStack, 36, 40, false)
                    || moveItemStackTo(newStack, 40, 42, false)
                    || moveItemStackTo(newStack, 9, 36, false);
        } else if (index < 36)
        {
            isMerged = moveItemStackTo(newStack, 36, 40, false)
                    || moveItemStackTo(newStack, 40, 42, false)
                    || moveItemStackTo(newStack, 0, 9, true);
        } else
        {
            isMerged = moveItemStackTo(newStack, 0, 9, true)
                    || moveItemStackTo(newStack, 9, 36, false);
        }

        if (!isMerged)
        {
            return ItemStack.EMPTY;
        }

        if (newStack.getCount() == 0)
        {
            slot.set(ItemStack.EMPTY);
        } else
        {
            slot.setChanged();
        }

        slot.onTake(playerIn, newStack);

        return oldStack;
    }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();
        te.notifyStateChanged();
    }

    public int getArrowHeight()
    {
        int teProgress = this.te.progress;
        int teProgressTotal = this.te.progressTotal;
        float progress = teProgressTotal == 0 ? 0f : (float) teProgress / (float) teProgressTotal;
        return (int) (progress * 81);
    }

    public int getNextStep()
    {
        return this.te.nextStep;
    }

    public MortarTE getTE()
    {
        return this.te;
    }

    private static class MortarSlot extends SlotItemHandler
    {
        MortarSlot(IItemHandler inv, int index, int x, int y)
        {
            super(inv, index, x, y);
        }

        @Override
        public int getMaxStackSize()
        {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return stack.is(MASHABLE);
        }
    }
}
