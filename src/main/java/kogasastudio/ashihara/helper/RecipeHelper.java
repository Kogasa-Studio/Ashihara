package kogasastudio.ashihara.helper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for recipe lookups against the new 26.1 RecipeMap API.
 * <p>
 * Uses {@link net.minecraft.world.item.crafting.RecipeManager#recipeMap()} → {@link net.minecraft.world.item.crafting.RecipeMap#byType(RecipeType)}
 * which is the approach used by Mekanism in {@code MekanismRecipeType.getRecipesUncached()}.
 * This avoids streaming all recipes and then filtering by type (O(n) → O(1) map lookup).
 * <p>
 * In 1.21, recipes are server-side only. For client access, this helper maintains a
 * client-side cache populated via {@code SyncRecipesPayload} from the server.
 */
public class RecipeHelper
{
    /** Client-side recipe cache populated by SyncRecipesPayload. */
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CLIENT_RECIPES = new ConcurrentHashMap<>();

    /** Server-side recipe cache for sending to clients on login. */
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> SERVER_CACHE = new ConcurrentHashMap<>();

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
    @SuppressWarnings("unchecked")
    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipesByType(Level level, RecipeType<T> type)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            var recipes = serverLevel.recipeAccess().recipeMap().byType(type);
            // Cache for client sync
            if (!recipes.isEmpty())
            {
                SERVER_CACHE.put(type, (List<RecipeHolder<?>>) (List<?>) List.copyOf(recipes));
            }
            return recipes;
        }
        // Client side: check local cache
        List<RecipeHolder<?>> cached = CLIENT_RECIPES.get(type);
        if (cached != null)
        {
            return (Collection<RecipeHolder<T>>) (Collection<?>) cached;
        }
        return Collections.emptyList();
    }

    /**
     * Called on client when SyncRecipesPayload is received.
     */
    public static void cacheClientRecipes(RecipeType<?> type, List<RecipeHolder<?>> recipes)
    {
        CLIENT_RECIPES.put(type, List.copyOf(recipes));
    }

    /**
     * Returns the server-side cached recipes to be sent to a newly connected client.
     */
    public static Map<RecipeType<?>, List<RecipeHolder<?>>> getServerCachedRecipes()
    {
        return Map.copyOf(SERVER_CACHE);
    }

    private RecipeHelper() {}
}
