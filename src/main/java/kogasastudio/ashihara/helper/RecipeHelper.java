package kogasastudio.ashihara.helper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;

/**
 * Utility for recipe lookups against the new 26.1 RecipeMap API.
 * <p>
 * Uses {@link RecipeManager#recipeMap()} → {@link net.minecraft.world.item.crafting.RecipeMap#byType(RecipeType)}
 * which is the approach used by Mekanism in {@code MekanismRecipeType.getRecipesUncached()}.
 * This avoids streaming all recipes and then filtering by type (O(n) → O(1) map lookup).
 */
public class RecipeHelper
{
    /**
     * Returns all registered recipes of the given type, or an empty collection if the level
     * is null or not a server level (i.e. recipes are not accessible client-side via recipeAccess).
     *
     * @param level the current world
     * @param type  the recipe type to query
     * @param <I>   the RecipeInput subtype
     * @param <T>   the Recipe subtype
     * @return typed collection of recipe holders; never null
     */
    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipesByType(
            Level level, RecipeType<T> type)
    {
        if (!(level instanceof ServerLevel serverLevel)) return Collections.emptyList();
        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.recipeMap().byType(type);
    }

    private RecipeHelper() {}
}

