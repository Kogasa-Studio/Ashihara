package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MillTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class MillContainer extends AshiharaCommonContainer
{
    private final IIntArray millData;
    private final MillTE te;

    public MillContainer(int id, PlayerInventory inventory, IIntArray millDataIn, BlockPos pos)
    {
        super(ContainerRegistryHandler.MILL_CONTAINER.get(), id);
        assertIntArraySize(millDataIn, 4);
        this.millData = millDataIn;
        trackIntArray(millDataIn);
        layoutPlayerInventorySlots(inventory, 8, 120);
        MillTE teIn = (MillTE) inventory.player.world.getTileEntity(pos);
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

    public MillContainer(int id, PlayerInventory inventory, PacketBuffer buffer)
    {
        this(id, inventory, new IntArray(4), buffer.readBlockPos());
    }

    public MillTE getTE() {return this.te;}

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {return true;}

    /**
     * 玩家在gui中shift点击时会调用的方法
     * @param playerIn 玩家
     * @param index 玩家右键的物品栏的id
     * @return 玩家右键的物品
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        //玩家右键的具体格子
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) {return ItemStack.EMPTY;}

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~8: 快捷栏, 9~35: 玩家背包, 36~39: 输入槽, 40~43: 输出槽, 44~46: 流体互动槽
        if (index < 9)
        {
            isMerged = mergeItemStack(newStack, 36, 40, false)
            || mergeItemStack(newStack, 44, 47, false)
            || mergeItemStack(newStack, 9, 36, false);
        }
        else if (index < 36)
        {
            isMerged = mergeItemStack(newStack, 36, 40, false)
            || mergeItemStack(newStack, 44, 47, false)
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

    public int getArrowWidth()
    {
        int round = millData.get(0);
        int roundTotal = millData.get(1);
        int roundProgress = millData.get(2);
        int roundTicks = millData.get(3);
        int p = (roundTotal * roundTicks);
        float progress = p != 0 ? (float) (round * roundTicks + roundProgress) / p : 0;
        return (int)(progress * 33);
    }
}
