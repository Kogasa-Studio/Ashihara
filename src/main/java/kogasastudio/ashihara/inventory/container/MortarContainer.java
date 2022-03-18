package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static kogasastudio.ashihara.utils.AshiharaTags.MASHABLE;

public class MortarContainer extends AshiharaCommonContainer
{
    private final IIntArray mortarData;
    private final MortarTE te;

    private static class MortarSlot extends SlotItemHandler
    {
        MortarSlot(IItemHandler inv, int index, int x, int y) {super(inv, index, x, y);}

        @Override
        public int getSlotStackLimit() {return 1;}

        @Override
        public boolean isItemValid(ItemStack stack) {return stack.getItem().isIn(MASHABLE);}
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

    public MortarContainer(int id, PlayerInventory inv, MortarTE teIn, IIntArray mortarDataIn)
    {
        super(ContainerRegistryHandler.MORTAR_CONTAINER.get(), id);
        assertIntArraySize(mortarDataIn, 3);
        this.mortarData = mortarDataIn;
        this.te = teIn;
        trackIntArray(mortarDataIn);
        layoutPlayerInventorySlots(inv, 8, 121);
        if (teIn != null)
        {
            addSlotBox(teIn.contents, 0, 80, 26, 1, 18, 4, 18);
            addSlot(new SlotItemHandler(teIn.fluidIO, 0, 21, 85));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }

    /**
     * 玩家在gui中shift点击时会调用的方法
     * @param playerIn 玩家
     * @param index 玩家点击的物品栏的id
     * @return 玩家右键的物品
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        //玩家点击的具体格子
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) {return ItemStack.EMPTY;}

        ItemStack newStack = slot.getStack();
        ItemStack oldStack = newStack.copy();

        boolean isMerged;

        // 0~8: 快捷栏, 9~35: 玩家背包, 36~39: 内容槽
        if (index < 9)
        {
            isMerged = mergeItemStack(newStack, 36, 40, false)
            ||mergeItemStack(newStack, 9, 36, false);
        }
        else if (index < 36)
        {
            isMerged = mergeItemStack(newStack, 36, 40, false)
            || mergeItemStack(newStack, 0, 9, true);
        }
        else
        {
            isMerged = mergeItemStack(newStack, 0, 9, true)
            || mergeItemStack(newStack, 9, 36, false);
        }

        if (!isMerged){return ItemStack.EMPTY;}

        if (newStack.getCount() == 0) {slot.putStack(ItemStack.EMPTY);}
        else {slot.onSlotChanged();}

        slot.onTake(playerIn, newStack);

        return oldStack;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        te.notifyStateChanged();
        LOGGER_MAIN.info("DEEEEEEEEEEEEEEEEEEEEEEBU");
    }

    public int getArrowHeight()
    {
        int teProgress = this.mortarData.get(0);
        int teProgressTotal = this.mortarData.get(1);
        float progress = teProgressTotal == 0 ? 0f : (float) teProgress / (float) teProgressTotal;
        return (int) (progress * 81);
    }

    public int getNextStep() {return this.mortarData.get(2);}

    public MortarTE getTE() {return this.te;}
}
