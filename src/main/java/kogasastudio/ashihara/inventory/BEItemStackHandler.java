package kogasastudio.ashihara.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class BEItemStackHandler<B extends BlockEntity> extends ItemStacksResourceHandler
{
    public final B be;
    private final Runnable onChange;

    public BEItemStackHandler(B be)
    {
        this(1, be, null);
    }

    public BEItemStackHandler(int size, B be)
    {
        this(size, be, null);
    }

    public BEItemStackHandler(NonNullList<ItemStack> stacks, B be)
    {
        this(stacks, be, null);
    }

    public BEItemStackHandler(int size, B be, @Nullable Runnable onChange)
    {
        super(size);
        this.be = be;
        this.onChange = onChange;
    }

    private BEItemStackHandler(NonNullList<ItemStack> stacks, B be, @Nullable Runnable onChange)
    {
        super(stacks);
        this.be = be;
        this.onChange = onChange;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents)
    {
        be.setChanged();
        if (be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), UPDATE_ALL);
        if (onChange != null) onChange.run();
    }

    public ItemStack getStackInSlot(int i)
    {
        return getResource(i).toStack(getAmountAsInt(i));
    }

    // ── Convenience methods ───────────────────────────────────────────────────

    public boolean isEmpty()
    {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public List<ItemStack> getAllContents()
    {
        return List.copyOf(this.stacks);
    }

    /**
     * Consumes up to {@code count} items matching {@code predicate} from internal slots.
     * Uses {@link #set} so that {@link #onContentsChanged} is properly triggered.
     */
    public void consumeItemStack(Predicate<ItemStack> predicate, int count)
    {
        int remaining = count;
        for (int i = 0; i < this.stacks.size() && remaining > 0; i++)
        {
            ItemStack stack = this.stacks.get(i);
            if (!stack.isEmpty() && predicate.test(stack))
            {
                int toConsume = Math.min(stack.getCount(), remaining);
                int newCount  = stack.getCount() - toConsume;
                set(i, newCount > 0 ? ItemResource.of(stack) : ItemResource.EMPTY, newCount);
                remaining -= toConsume;
            }
        }
    }

    @Override
    public boolean isValid(int index, ItemResource resource)
    {
        return super.isValid(index, resource);
    }

    /**
     * Tries to insert {@code stack} into the first available slot.
     *
     * @param stack    the stack to insert
     * @param simulate if {@code true} the operation is rolled back
     * @return the remainder that could not be inserted (may be empty)
     */
    public ItemStack insert(ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemResource resource = ItemResource.of(stack);
        int amount = stack.getCount();
        try (Transaction tx = Transaction.openRoot())
        {
            int inserted = this.insert(resource, amount, tx);
            if (!simulate && inserted > 0) tx.commit();
            return inserted >= amount ? ItemStack.EMPTY : stack.copyWithCount(amount - inserted);
        }
    }

    /**
     * Tries to insert each stack in the list, returning leftover stacks.
     *
     * @param stacks   the stacks to insert
     * @param simulate if {@code true} every insertion is rolled back
     * @return list of remainders (empty stacks mean fully inserted)
     */
    public List<ItemStack> insert(List<ItemStack> stacks, boolean simulate)
    {
        List<ItemStack> remainders = new ArrayList<>();
        for (ItemStack stack : stacks)
        {
            remainders.add(this.insert(stack, simulate));
        }
        return remainders;
    }

    public int testIngredient(SizedIngredient ingredient)
    {
        int count = 0;
        for (ItemStack stack : this.stacks)
        {
            if (ingredient.test(stack)) count = stack.getCount() / ingredient.count();
        }
        return count;
    }

    public int testIngredients(List<SizedIngredient> ingredients, int maxValue, boolean simulate)
    {
        int multiplier = maxValue;
        for (SizedIngredient ingredient : ingredients)
        {
            multiplier = Math.min(multiplier, testIngredient(ingredient));
        }
        if (!simulate)
        {
            for (SizedIngredient ingredient : ingredients)
            {
                consumeItemStack(i -> ingredient.ingredient().test(i), multiplier * ingredient.count());
            }
        }
        return multiplier;
    }
}
