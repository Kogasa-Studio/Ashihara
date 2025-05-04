package kogasastudio.ashihara.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

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

    public boolean isEmpty()
    {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public List<ItemStack> getAllContents()
    {
        return List.copyOf(this.stacks);
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

    public int testIngredients(List<SizedIngredient> ingredients, int maxValue)
    {
        int multiplier = maxValue;
        for (SizedIngredient ingredient : ingredients)
        {
            multiplier = Math.min(multiplier, testIngredient(ingredient));
        }
        return multiplier;
    }
}
