package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.inventory.container.GenericItemStackHandler;
import kogasastudio.ashihara.item.IHasCustomModel;
import kogasastudio.ashihara.utils.ItemDisplayPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Map;

public class MealTableTE extends AshiharaMachineTE
{
    public MealTableTE() {super(TERegistryHandler.MEAL_TABLE_TE.get());}

    public GenericItemStackHandler content = new GenericItemStackHandler(5)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return stack.getItem() instanceof IHasCustomModel ? ((IHasCustomModel) stack.getItem()).getModelStackSize() : 4;
        }
    };

    public Map<Integer, ItemDisplayPos> posMap = new HashMap<>(5);
    private final float[][] SIZE_1 = {{6.0f, 6.0f, 6.0f}};
    private final float[][] SIZE_2 = {{3.0f, 6.0f, 6.0f}, {9.0f, 6.0f, 6.0f}};
    private final float[][] SIZE_3 = {{6.0f, 6.0f, 3.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};
    private final float[][] SIZE_4 = {{3.0f, 6.0f, 3.0f}, {9.0f, 6.0f, 3.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};
    private final float[][] SIZE_5 = {{3.0f, 6.0f, 3.0f}, {9.0f, 6.0f, 3.0f}, {6.0f, 6.0f, 6.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};

    private void notifyStateChanged()
    {
        for (int i = 0; i < this.content.getSlots(); i += 1)
        {
            if (this.content.getStackInSlot(i).isEmpty()) {posMap.remove(i);}
            else if (this.posMap.get(i) == null) {this.posMap.put(i, new ItemDisplayPos(this.content, i, 4, Direction.NORTH));}
        }
        float[][] list;
        switch (this.posMap.size())
        {
            case 1 : list = SIZE_1;break;
            case 2 : list = SIZE_2;break;
            case 3 : list = SIZE_3;break;
            case 4 : list = SIZE_4;break;
            default: list = SIZE_5;
        }
        for (int i = 0; i < list.length; i += 1)
        {
            posMap.get(i).applyPos(list[i]);
        }
        markDirty();
    }

    public void handleInteraction(PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (!stack.isEmpty())
        {
            for (int i = 0; i < this.content.getSlots(); i += 1)
            {
                ItemStack contained = this.content.getStackInSlot(i);
                if (contained.isItemEqual(stack) && contained.getCount() < contained.getMaxStackSize())
                {
                    stack.shrink(this.content.insertItem(i, stack, false).getCount());
                }
            }
        }
    }

    public GenericItemStackHandler getContent() {return this.content;}
}