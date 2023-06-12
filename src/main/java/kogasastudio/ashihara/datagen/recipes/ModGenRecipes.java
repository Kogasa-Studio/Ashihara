package kogasastudio.ashihara.datagen.recipes;

import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DustW
 **/
public abstract class ModGenRecipes
{
    private final Map<ResourceLocation, Map.Entry<String, String>> recipes = new HashMap<>();

    protected abstract void addRecipes();

    protected final void addRecipe(ResourceLocation name, String recipe, String subPath)
    {
        recipes.put(name, new HashMap.SimpleEntry<>(recipe, subPath));
    }

    public Map<ResourceLocation, Map.Entry<String, String>> getRecipes()
    {
        addRecipes();
        return recipes;
    }

    protected ResourceLocation defaultName(Item item)
    {
        return item.getRegistryName();
    }

    protected <TYPE extends BaseRecipe> String baseRecipe(TYPE recipe)
    {
        return JsonUtils.INSTANCE.pretty.toJson(recipe);
    }
}
