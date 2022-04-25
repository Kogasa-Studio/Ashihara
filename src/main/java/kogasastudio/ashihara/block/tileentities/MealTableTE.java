package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.inventory.container.GenericItemStackHandler;
import kogasastudio.ashihara.item.IHasCustomModel;
import kogasastudio.ashihara.utils.ItemDisplayPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.ArrayList;

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
    public ArrayList<ItemDisplayPos> posList = new ArrayList<>();

    public void handleInteraction(PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        for (int i = 0; i < this.content.getSlots(); i += 1)
        {
            ItemStack contained = this.content.getStackInSlot(i);
            if (contained.isItemEqual(stack) && contained.getCount() < contained.getMaxStackSize())
            {
                stack.shrink(this.content.insertItem(i, stack, false).getCount());
            }
        }
    }

    public GenericItemStackHandler getContent() {return this.content;}
}