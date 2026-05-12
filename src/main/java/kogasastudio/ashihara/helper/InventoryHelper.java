package kogasastudio.ashihara.helper;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class InventoryHelper
{
    /**
     * Generic inventory interaction:
     * <ul>
     *   <li>If the player's hand is <em>empty</em>, extracts the last non-empty stack (up to {@code maxAmount}).</li>
     *   <li>If the player is <em>holding an item</em>, inserts it into the first accepting slot.</li>
     * </ul>
     *
     * @return {@code true} if the interaction succeeded
     */
    public static boolean interactWithInventory(ItemStacksResourceHandler inventory, ItemStack item, Player player, InteractionHand hand, int maxAmount)
    {
        if (item.isEmpty())
        {
            // Extract from last occupied slot
            for (int i = inventory.size() - 1; i >= 0; i--)
            {
                ItemResource resource = inventory.getResource(i);
                if (resource.isEmpty()) continue;
                int toExtract = Math.min(maxAmount, inventory.getAmountAsInt(i));
                if (toExtract <= 0) continue;
                try (Transaction tx = Transaction.openRoot())
                {
                    int extracted = inventory.extract(i, resource, toExtract, tx);
                    if (extracted > 0)
                    {
                        tx.commit();
                        player.setItemInHand(hand, resource.toStack(extracted));
                        return true;
                    }
                }
            }
        }
        else
        {
            // Insert into first accepting slot
            ItemResource resource = ItemResource.of(item);
            int amount = item.getCount();
            for (int i = 0; i < inventory.size(); i++)
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    int inserted = inventory.insert(i, resource, amount, tx);
                    if (inserted > 0)
                    {
                        tx.commit();
                        int remaining = amount - inserted;
                        player.setItemInHand(hand, remaining > 0 ? item.copyWithCount(remaining) : ItemStack.EMPTY);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
