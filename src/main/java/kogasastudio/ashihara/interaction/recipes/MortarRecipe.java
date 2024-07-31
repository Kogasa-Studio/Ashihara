package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import kogasastudio.ashihara.utils.json.serializer.FluidStackSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MortarRecipe //extends BaseRecipe
{
    /*@Expose
    public final NonNullList<Ingredient> input;
    @Expose
    public final NonNullList<ItemStack> output;

    @Expose
    @SerializedName("fluid")
    public final FluidStack fluidCost;

    @Expose
    public int progress;
    @Expose
    public byte recipeType;
    @Expose
    public byte[] sequence;

    public MortarRecipe(ResourceLocation idIn, String groupId,
                        NonNullList<Ingredient> inputIn, NonNullList<ItemStack> outputIn,
                        FluidStack fluidCostIn,
                        int progressIn, byte recipeTypeIn, byte[] sequenceIn)
    {
        this.id = idIn;
        this.group = groupId;

        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn;
        this.progress = progressIn;
        this.recipeType = recipeTypeIn;
        this.sequence = sequenceIn;
    }

    public String getInfo()
    {
        return
                "\n{\ninput: " + this.input.toString()
                        + "\noutput: " + this.output.toString()
                        + "\nid: " + this.id.toString()
                        + "\nprogress: " + this.progress
                        + "\nrecipeType: " + this.recipeType
                        + "\nsequence: " + Arrays.toString(this.sequence)
                        + "\n}";
    }

    public boolean testInputFluid(@Nullable FluidTank tank)
    {
        return tank == null ? fluidCost == null :
                tank.drain(fluidCost.copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= fluidCost.getAmount();
    }

    @Override
    public boolean matches(List<ItemStack> inputs)
    {
        if (inputs == null || this.input == null)
        {
            LogManager.getLogger().error("MortarRecipe.matches: input is null. id: " + getId());
            return false;
        }

        inputs = inputs.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return RecipeTypes.MORTAR.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= this.input.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return this.output.get(0).copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input;
    }

    public NonNullList<ItemStack> getOutput()
    {
        return this.output;
    }

    @Override
    public String getGroup()
    {
        return this.group;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    public FluidStack getFluidCost()
    {
        return fluidCost == null ? FluidStack.EMPTY : fluidCost;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.MORTAR.get();
    }

    public static class MortarRecipeSerializer implements RecipeSerializer<MortarRecipe>
    {
        @Override
        public MortarRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context)
        {
            return this.fromJson(recipeLoc, recipeJson);
        }

        @Override
        public MortarRecipe fromJson(ResourceLocation rl, JsonObject json)
        {
            JsonArray ingredients = json.get("input").getAsJsonArray();
            JsonArray output = json.get("output").getAsJsonArray();
            JsonArray sArray = json.get("sequence").getAsJsonArray();

            if (ingredients.isEmpty()) throw new JsonParseException("Ingredients is empty!"); else
            {
                NonNullList<Ingredient> iList = NonNullList.create();
                NonNullList<ItemStack> oList = NonNullList.create();
                byte[] sequence = new byte[sArray.size()];

                String group = json.get("group").getAsString();
                for (JsonElement element : ingredients) iList.add(Ingredient.fromJson(element));
                for (JsonElement element : output) oList.add(CraftingHelper.getItemStack(element.getAsJsonObject(), true));
                FluidStack fluidCost = json.has("fluid") ? FluidStackSerializer.deserialize(json.get("fluid")) : FluidStack.EMPTY;
                int progress = json.get("progress").getAsInt();
                byte recipeType = json.get("recipeType").getAsByte();
                for (int i = 0; i < sArray.size(); i += 1) sequence[i] = sArray.get(i).getAsByte();
                return new MortarRecipe(rl, group, iList, oList, fluidCost, progress, recipeType, sequence);
            }
        }

        @Override
        public @Nullable MortarRecipe fromNetwork(ResourceLocation rl, FriendlyByteBuf buffer)
        {
            String groupN = buffer.readUtf();
            NonNullList<Ingredient> iListN = DataHelper.copyAndCast(buffer.readList(Ingredient::fromNetwork));
            NonNullList<ItemStack> oListN = DataHelper.copyAndCast(buffer.readList(FriendlyByteBuf::readItem));
            FluidStack fCostN = buffer.readFluidStack();
            int progressN = buffer.readInt();
            byte recipeTypeN = buffer.readByte();
            byte[] sequenceN = buffer.readByteArray();
            return new MortarRecipe(rl, groupN, iListN, oListN, fCostN, progressN, recipeTypeN, sequenceN);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MortarRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            buffer.writeCollection(recipe.input, (friendlyByteBuf, ingredient) -> ingredient.toNetwork(friendlyByteBuf));
            buffer.writeCollection(recipe.output, FriendlyByteBuf::writeItem);
            buffer.writeFluidStack(recipe.fluidCost);
            buffer.writeInt(recipe.progress);
            buffer.writeByte(recipe.recipeType);
            buffer.writeByteArray(recipe.sequence);
        }
    }*/
}
