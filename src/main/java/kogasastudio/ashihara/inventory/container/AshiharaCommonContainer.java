package kogasastudio.ashihara.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AshiharaCommonContainer extends Container
{
    protected AshiharaCommonContainer(ContainerType<?> type, int id) {super(type, id);}

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
    protected int addSlotRange(IInventory inventory, int index, int x, int y, int amount, int dx)
    {
        for (int i = 0; i < amount; i++)
        {
            addSlot(new Slot(inventory, index, x, y));
            x += dx;
            index += 1;
        }
        return index;
    }

    protected int addSlotRange(IItemHandler inventory, int index, int x, int y, int amount, int dx)
    {
        for (int i = 0; i < amount; i++)
        {
            addSlot(new SlotItemHandler(inventory, index, x, y));
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
    protected void addSlotBox(IInventory inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy)
    {
        for (int j = 0; j < verAmount; j++)
        {
            index = addSlotRange(inventory, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void addSlotBox(IItemHandler inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy)
    {
        for (int j = 0; j < verAmount; j++)
        {
            index = addSlotRange(inventory, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    //将玩家的整个物品栏添加进container
    protected void layoutPlayerInventorySlots(IInventory inventory, int leftCol, int topRow)
    {
        //背包
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        //快捷栏
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {return false;}
}
