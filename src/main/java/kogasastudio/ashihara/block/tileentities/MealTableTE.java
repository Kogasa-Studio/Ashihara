package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.inventory.container.GenericItemStackHandler;
import kogasastudio.ashihara.item.IHasCustomModel;
import kogasastudio.ashihara.utils.ItemDisplayPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class MealTableTE extends AshiharaMachineTE
{
    private final float[][] SIZE_1 = {{6.0f, 6.0f, 6.0f}};
    private final float[][] SIZE_2 = {{3.0f, 6.0f, 6.0f}, {9.0f, 6.0f, 6.0f}};
    private final float[][] SIZE_3 = {{6.0f, 6.0f, 3.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};
    private final float[][] SIZE_4 = {{3.0f, 6.0f, 3.0f}, {9.0f, 6.0f, 3.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};
    private final float[][] SIZE_5 = {{3.0f, 6.0f, 3.0f}, {9.0f, 6.0f, 3.0f}, {6.0f, 6.0f, 6.0f}, {3.0f, 6.0f, 9.0f}, {9.0f, 6.0f, 9.0f}};
    public GenericItemStackHandler content = new GenericItemStackHandler(5)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return stack.getItem() instanceof IHasCustomModel ? ((IHasCustomModel) stack.getItem()).getModelStackSize() : 4;
        }
    };
    public Map<Integer, ItemDisplayPos> posMap = new HashMap<>(5);

    public MealTableTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MEAL_TABLE_TE.get(), pos, state);
    }

    private void notifyStateChanged()
    {
        for (int i = 0; i < this.content.getSlots(); i += 1)
        {
            if (this.content.getStackInSlot(i).isEmpty())
            {
                posMap.remove(i);
            } else if (this.posMap.get(i) == null)
            {
                this.posMap.put(i, new ItemDisplayPos(this.content, i, 4, Direction.NORTH));
            }
        }
        float[][] list = switch (this.posMap.size())
        {
            case 1 -> SIZE_1;
            case 2 -> SIZE_2;
            case 3 -> SIZE_3;
            case 4 -> SIZE_4;
            default -> SIZE_5;
        };
        for (int i = 0; i < list.length; i += 1)
        {
            posMap.get(i).applyPos(list[i]);
        }
        setChanged();
    }

    public void handleInteraction(Player playerIn, InteractionHand handIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (!stack.isEmpty())
        {
            boolean stateChanged = false;
            for (int i = 0; i < this.content.getSlots(); i += 1)
            {
                ItemStack contained = this.content.getStackInSlot(i);
                if (contained.is(stack.getItem()) && contained.getCount() < contained.getMaxStackSize())
                {
                    stack.shrink(this.content.insertItem(i, stack, false).getCount());
                    stateChanged = true;
                }
            }
            if (stateChanged) this.notifyStateChanged();
        }
    }

    public GenericItemStackHandler getContent()
    {
        return this.content;
    }
}