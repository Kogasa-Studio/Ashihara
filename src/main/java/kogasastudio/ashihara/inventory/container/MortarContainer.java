package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MortarContainer extends AshiharaCommonContainer
{
    private final IIntArray mortarData;
    private final World world;
    private final BlockPos pos;

    public MortarContainer(int id, PlayerInventory inv, World worldIn, BlockPos posIn, IIntArray mortarDataIn)
    {
        super(ContainerRegistryHandler.MORTAR_CONTAINER.get(), id);
        assertIntArraySize(mortarDataIn, 3);
        this.mortarData = mortarDataIn;
        this.world = worldIn;
        this.pos = posIn;
        trackIntArray(mortarDataIn);
        layoutPlayerInventorySlots(inv, 8, 121);
        MortarTE te = (MortarTE) worldIn.getTileEntity(posIn);
        if (te != null) addSlotBox(te.contents, 0, 80, 26, 1, 18, 4, 18);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }

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

        ItemStack newStack;
        //若点击的不是舂的内容物则只变更一个物品
        if (index <= 36) {newStack = new ItemStack(slot.getStack().getItem());slot.getStack().shrink(1);}
        else newStack = slot.getStack();
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

    public int getArrowHeight()
    {
        int teProgress = this.mortarData.get(0);
        int teProgressTotal = this.mortarData.get(1);
        float progress = teProgressTotal == 0 ? 0 : (float) teProgress / teProgressTotal;
        return (int) progress * 81;
    }

    public int getNextStep() {return this.mortarData.get(2);}
}
