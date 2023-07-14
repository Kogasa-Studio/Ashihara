package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CuttingBoardRecipe extends BaseRecipe
{
    @Expose
    private final Ingredient ingredient;
    @Expose
    private final NonNullList<ItemStack> result;
    @Expose
    private final CuttingBoardToolType tool;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, NonNullList<ItemStack> outputIn, CuttingBoardToolType typeIn)
    {
        this.id = idIn;
        this.ingredient = inputIn;
        this.result = outputIn;
        this.tool = typeIn;
    }

    public String getInfo()
    {
        return
                "\n{\n    input: " + Arrays.toString(this.ingredient.getItems())
                        + "\n    output: " + this.result.toString()
                        + "\n    id: " + this.id.toString()
                        + "\n    type: " + this.tool.getName()
                        + "\n}";
    }

    @Override
    public boolean matches(List<ItemStack> inputs)
    {
        return ingredient.test(inputs.get(0));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this.ingredient);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return this.result.get(0);
    }

    public NonNullList<ItemStack> getOutput()
    {
        return DataHelper.copyAndCast(this.result);
    }

    public CuttingBoardToolType getTool()
    {
        return this.tool;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeType<?> getType()
    {
        return RecipeTypes.CUTTING_BOARD.get();
    }

    public Ingredient getInput()
    {
        return ingredient;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.CUTTING_BOARD.get();
    }

    public static class CuttingBoardRecipeSerializer implements RecipeSerializer<CuttingBoardRecipe>
    {
        @Override
        public CuttingBoardRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context)
        {
            return this.fromJson(recipeLoc, recipeJson);
        }

        @Override
        public CuttingBoardRecipe fromJson(ResourceLocation rl, JsonObject json)
        {
            CuttingBoardToolType toolTypeIn = CuttingBoardToolType.nameMatches(json.get("tool").getAsString());
            if (toolTypeIn.isEmpty()) throw new JsonParseException("Invalid tool type!");
            JsonArray outputRaw = json.get("result").getAsJsonArray();

            Ingredient ingredientIn = Ingredient.fromJson(json.get("ingredient"));
            NonNullList<ItemStack> outputIn = NonNullList.create();
            for (JsonElement element : outputRaw) outputIn.add(CraftingHelper.getItemStack(element.getAsJsonObject(), true));

            return new CuttingBoardRecipe(rl, ingredientIn, outputIn, toolTypeIn);
        }

        @Override
        public @Nullable CuttingBoardRecipe fromNetwork(ResourceLocation rl, FriendlyByteBuf buffer)
        {
            Ingredient ingredientN = Ingredient.fromNetwork(buffer);
            CuttingBoardToolType toolTypeN = CuttingBoardToolType.nameMatches(buffer.readUtf());
            NonNullList<ItemStack> outputN = DataHelper.copyAndCast(buffer.readList(FriendlyByteBuf::readItem));

            return new CuttingBoardRecipe(rl, ingredientN, outputN, toolTypeN);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuttingBoardRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeUtf(recipe.tool.getName());
            buffer.writeCollection(recipe.result, FriendlyByteBuf::writeItem);
        }
    }
}
