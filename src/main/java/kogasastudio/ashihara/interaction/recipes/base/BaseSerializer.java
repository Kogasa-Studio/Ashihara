package kogasastudio.ashihara.interaction.recipes.base;

import com.google.gson.JsonObject;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

/**
 * @author DustW
 **/
public class BaseSerializer<RECIPE extends BaseRecipe> implements RecipeSerializer<RECIPE>
{

    Class<RECIPE> recipeClass;

    public BaseSerializer(Class<RECIPE> recipeClass)
    {
        super();
        this.recipeClass = recipeClass;
    }

    @Nullable
    @Override
    public RECIPE fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer)
    {
        return JsonUtils.INSTANCE.normal.fromJson(pBuffer.readUtf(), recipeClass).setID(pRecipeId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, RECIPE pRecipe)
    {
        pBuffer.writeUtf(JsonUtils.INSTANCE.normal.toJson(pRecipe));
    }

    @Override
    public RECIPE fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe)
    {
        return JsonUtils.INSTANCE.normal.fromJson(pSerializedRecipe, recipeClass).setID(pRecipeId);
    }

    public JsonObject toJson(RECIPE pRecipe)
    {
        return JsonUtils.INSTANCE.normal.toJsonTree(pRecipe).getAsJsonObject();
    }
}
