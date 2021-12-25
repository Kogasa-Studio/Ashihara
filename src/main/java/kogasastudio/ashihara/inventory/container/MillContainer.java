package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MillTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MillContainer extends Container
{
    private final IIntArray millData;
    public MillContainer(int id, PlayerInventory inventory, World worldIn, BlockPos posIn, IIntArray millDataIn)
    {
        super(ContainerRegistryHandler.MILL_CONTAINER.get(), id);
        assertIntArraySize(millDataIn, 4);
        this.millData = millDataIn;
        trackIntArray(millDataIn);
        layoutPlayerInventorySlots(inventory, 8, 120);
        MillTE te = (MillTE) worldIn.getTileEntity(posIn);
        if (te != null)
        {
            addSlotBox(te.getInput(), 0, 55, 39, 2, 18, 2, 18);
            addSlotBox(te.getOutput(), 0, 130, 39, 2, 18, 2, 18);
        }
    }

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

        // 0~8: 玩家背包, 9~35: 快捷栏, 36~39: 输入槽, 40~43: 输出槽
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
            isMerged = mergeItemStack(newStack, 27, 36, true)
            || mergeItemStack(newStack, 0, 27, false);
        }

        if (!isMerged){return ItemStack.EMPTY;}

        if (newStack.getCount() == 0) {slot.putStack(ItemStack.EMPTY);}
        else {slot.onSlotChanged();}

        slot.onTake(playerIn, newStack);

        return oldStack;
    }

    /**
     * 批量添加格子
     * @param inventory 已存储的物品数据
     * @param index 起始格子
     * @param x x
     * @param y y
     * @param amount 要连续添加格子的个数
     * @param dx 格子之间的距离
     * @return 加格子后最后一个格子对应的格子序号
     */
    private int addSlotRange(IInventory inventory, int index, int x, int y, int amount, int dx)
    {
        for (int i = 0; i < amount; i++)
        {
            addSlot(new Slot(inventory, index, x, y));
            x += dx;
            index += 1;
        }
        return index;
    }

    /**
     * 批量分行添加格子
     * @param inventory 已存储的物品数据
     * @param index 起始格子
     * @param x x
     * @param y y
     * @param horAmount 行数
     * @param dx 横着数格子间的距离
     * @param verAmount 列数
     * @param dy 竖着数格子间的距离
     */
    private void addSlotBox(IInventory inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy)
    {
        for (int j = 0; j < verAmount; j++)
        {
            index = addSlotRange(inventory, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    //将玩家的整个物品栏添加进container
    private void layoutPlayerInventorySlots(IInventory inventory, int leftCol, int topRow)
    {
        //背包
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        //快捷栏
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
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
