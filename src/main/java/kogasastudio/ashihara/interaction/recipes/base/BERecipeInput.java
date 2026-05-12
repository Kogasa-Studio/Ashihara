package kogasastudio.ashihara.interaction.recipes.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Minimal RecipeInput implementation for block-entity-driven recipes.
 * These recipes are matched via {@link WrappedRecipe#testBE} rather than slot-based matching.
 */
public class BERecipeInput implements RecipeInput {
    public static final BERecipeInput EMPTY = new BERecipeInput();

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }
}

