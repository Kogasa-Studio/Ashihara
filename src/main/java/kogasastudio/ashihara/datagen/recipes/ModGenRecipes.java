package kogasastudio.ashihara.datagen.recipes;

import com.google.gson.JsonObject;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.interaction.recipes.base.BaseSerializer;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DustW
 **/
public abstract class ModGenRecipes
{
    private final Map<ResourceLocation, Map.Entry<String, String>> recipes = new HashMap<>();
    private final List<FinishedRecipe> finishedRecipes = new ArrayList<>();

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

    protected <TYPE extends BaseRecipe> String baseRecipe(TYPE recipe)
    {
        return JsonUtils.INSTANCE.pretty.toJson(recipe);
    }

    protected class AshiharaRecipeResult implements FinishedRecipe
    {
        private final BaseSerializer<BaseRecipe> serializer;

        public AshiharaRecipeResult(BaseSerializer<BaseRecipe> recipeTypeIn)
        {
            this.serializer = recipeTypeIn;
        }

        @Override
        public void serializeRecipeData(JsonObject json)
        {

        }

        @Override
        public ResourceLocation getId()
        {
            return null;
        }

        @Override
        public RecipeSerializer<?> getType()
        {
            return this.serializer;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement()
        {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId()
        {
            return null;
        }
    }
}
