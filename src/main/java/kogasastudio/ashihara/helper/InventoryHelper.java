package kogasastudio.ashihara.helper;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InventoryHelper
{
    public static boolean interactWithInventory(ItemStackHandler inventory, ItemStack item, Player player, InteractionHand hand, int maxAmount)
    {
        if (item.isEmpty())
        {
            for (int i = inventory.getSlots() - 1; i >= 0; i--)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isEmpty() || inventory.extractItem(i, Math.min(maxAmount, stack.getCount()), true).isEmpty())
                    continue;
                player.setItemInHand(hand, inventory.extractItem(i, Math.min(maxAmount, stack.getCount()), false));
                return true;
            }
        }
        else
        {
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                if (inventory.insertItem(i, item, true).getCount() != item.getCount())
                {
                    player.setItemInHand(hand, inventory.insertItem(i, item, false));
                    return true;
                }
            }
        }
        return false;
    }
}
