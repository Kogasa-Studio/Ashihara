package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MillTE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MillContainer //extends AshiharaCommonContainer
{
    /*private final ContainerData millData;
    private final MillTE te;

    public MillContainer(int id, Inventory inventory, Level worldIn, BlockPos posIn, ContainerData millDataIn)
    {
        super(ContainerRegistryHandler.MILL_CONTAINER.get(), id);
        checkContainerDataCount(millDataIn, 4);
        this.millData = millDataIn;
        addDataSlots(millDataIn);
        layoutPlayerInventorySlots(inventory, 8, 120);
        MillTE teIn = (MillTE) worldIn.getBlockEntity(posIn);
        this.te = teIn;
        if (teIn != null)
        {
            addSlotBox(te.getInput(), 0, 55, 39, 2, 18, 2, 18);
            addSlotBox(te.getOutput(), 0, 130, 39, 2, 18, 2, 18);
            addSlot(new SlotItemHandler(te.fluidIO, 0, 17, 83));
            addSlot(new SlotItemHandler(te.fluidIO, 1, 122, 90));
            addSlot(new SlotItemHandler(te.fluidIO, 2, 152, 98));
        }
    }

    public MillContainer(int id, Inventory inventory, MillTE te)
    {
        this(id, inventory, inventory.player.level(), te.getBlockPos(), te.millData);
    }

    public MillTE getBe()
    {
        return this.te;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }

    *//**
     * 玩家在gui中shift点击时会调用的方法
     *
     * @param playerIn 玩家
     * @param index    玩家右键的物品栏的id
     * @return 玩家右键的物品
     *//*
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        //玩家右键的具体格子
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getItem(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~8: 快捷栏, 9~35: 玩家背包, 36~39: 输入槽, 40~43: 输出槽, 44~46: 流体互动槽
        if (index < 9)
        {
            isMerged = moveItemStackTo(newStack, 36, 40, false)
                    || moveItemStackTo(newStack, 44, 47, false)
                    || moveItemStackTo(newStack, 9, 36, false);
        } else if (index < 36)
        {
            isMerged = moveItemStackTo(newStack, 36, 40, false)
                    || moveItemStackTo(newStack, 44, 47, false)
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

    public int getArrowWidth()
    {
        int round = millData.get(0);
        int roundTotal = millData.get(1);
        int roundProgress = millData.get(2);
        int roundTicks = millData.get(3);
        int p = (roundTotal * roundTicks);
        float progress = p != 0 ? (float) (round * roundTicks + roundProgress) / p : 0;
        return (int) (progress * 33);
    }*/
}
