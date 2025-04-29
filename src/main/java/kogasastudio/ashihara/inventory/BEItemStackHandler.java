package kogasastudio.ashihara.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class BEItemStackHandler<B extends BlockEntity> extends ItemStackHandler
{
    public final B be;

    public BEItemStackHandler(B be)
    {
        super();
        this.be = be;
    }

    public BEItemStackHandler(int size, B be)
    {
        super(size);
        this.be = be;
    }

    public BEItemStackHandler(NonNullList<ItemStack> stacks, B be)
    {
        super(stacks);
        this.be = be;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        be.setChanged();
        if (be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), UPDATE_ALL);
    }

    public int testIngredient(Ingredient ingredient)
    {
        int count = 0;
        for (ItemStack stack : this.stacks)
        {
            if (ingredient.test(stack)) count = stack.getCount();
        }
        return count;
    }

    public int testIngredients(List<Ingredient> ingredients, int maxValue)
    {
        int multiplier = maxValue;
        for (Ingredient ingredient : ingredients)
        {
            multiplier = Math.min(multiplier, testIngredient(ingredient));
        }
        return multiplier;
    }
}
