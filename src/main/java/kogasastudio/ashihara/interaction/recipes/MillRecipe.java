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
import kogasastudio.ashihara.utils.json.serializer.FluidStackSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MillRecipe //extends BaseRecipe
{
    /*private NonNullList<Ingredient> input;

    private NonNullList<Ingredient> input()
    {
        return input != null ? input :
                (input = NonNullList.of(Ingredient.EMPTY, inputCosts.keySet().toArray(new Ingredient[0])));
    }

    @Expose
    public final Map<Ingredient, Byte> inputCosts;
    @Expose
    public final NonNullList<ItemStack> output;

    @Expose
    public final FluidStack inputFluid;
    @Expose
    public final FluidStack outputFluid;

    @Expose
    public byte round;
    @Expose
    public int roundTicks;
    @Expose
    public float exp;

    public MillRecipe
            (
                    ResourceLocation recipeId,
                    String groupId,
                    Map<Ingredient, Byte> inputCostsIn,
                    NonNullList<ItemStack> outputIn,
                    FluidStack inFluid,
                    FluidStack outFluid,
                    byte roundIn, int roundTicksIn, float expIn
            )
    {
        this.id = recipeId;
        this.group = groupId;

        this.inputCosts = inputCostsIn;
        this.output = outputIn;
        this.inputFluid = inFluid;
        this.outputFluid = outFluid;
        this.round = roundIn;
        this.roundTicks = roundTicksIn;
        this.exp = expIn;
    }

    public MillRecipe copy()
    {
        return new MillRecipe(id, group, inputCosts, output, inputFluid, outputFluid, round, roundTicks, exp);
    }

    public boolean testInputFluid(FluidTank tank)
    {
        return tank == null ? getInputFluid().isEmpty() :
                getInputFluid().isEmpty() ||
                        tank.drain(getInputFluid().copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= getInputFluid().getAmount();
    }

    public FluidStack getInputFluid()
    {
        return inputFluid == null ? FluidStack.EMPTY : inputFluid;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid == null ? FluidStack.EMPTY : this.outputFluid.copy();
    }

    public byte getCosts(Ingredient ingredient)
    {
        if (ingredient == Ingredient.EMPTY)
        {
            return 0;
        }

        return this.inputCosts.get(ingredient);
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input();
    }

    @Override
    public boolean matches(List<ItemStack> inputs)
    {
        if (inputs == null || this.input() == null)
        {
            LogManager.getLogger().error("MortarRecipe.matches: input is null. id: " + getId());
            return false;
        }

        inputs = inputs.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        return RecipeMatcher.findMatches(inputs, this.input()) != null;
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

    //这个也没啥用
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return output.size() > 0 ? this.output.get(0).copy() : ItemStack.EMPTY;
    }

    //出结果的
    public NonNullList<ItemStack> getCraftingResult()
    {
        return DataHelper.copyAndCast(output);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= this.input().size();
    }

    @Override
    public RecipeType<?> getType()
    {
        return RecipeTypes.MILL.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.MILL.get();
    }

    public static class MillRecipeSerializer implements RecipeSerializer<MillRecipe>
    {
        @Override
        public MillRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context)
        {
            return this.fromJson(recipeLoc, recipeJson);
        }

        @Override
        public MillRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson)
        {
            JsonArray ingredients = recipeJson.getAsJsonArray("inputCosts");
            JsonArray output = recipeJson.getAsJsonArray("output");
            if (ingredients.isEmpty()) throw new JsonParseException("Ingredients is empty!");
            else
            {
                Map<Ingredient, Byte> iMap = new HashMap<>();
                NonNullList<ItemStack> oList = NonNullList.create();

                for (JsonElement element : ingredients)
                {
                    Ingredient item = Ingredient.fromJson(element);
                    byte count = element.getAsJsonObject().get("count").getAsByte();
                    iMap.put(item, count);
                }
                for (JsonElement element : output)
                {
                    ItemStack item = CraftingHelper.getItemStack(element.getAsJsonObject(), true);
                    oList.add(item);
                }
                FluidStack fluidIn = recipeJson.has("inputFluid") ? FluidStackSerializer.deserialize(recipeJson.get("inputFluid")) : FluidStack.EMPTY;
                FluidStack fluidOut = recipeJson.has("outputFluid") ? FluidStackSerializer.deserialize(recipeJson.get("outputFluid")) : FluidStack.EMPTY;
                byte round = recipeJson.get("round").getAsByte();
                int roundTicks = recipeJson.get("roundTicks").getAsInt();
                float exp = recipeJson.get("exp").getAsFloat();
                String group = recipeJson.get("group").getAsString();
                return new MillRecipe(recipeLoc, group, iMap, oList, fluidIn, fluidOut, round, roundTicks, exp);
            }
        }

        @Override
        public @Nullable MillRecipe fromNetwork(ResourceLocation rl, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            Map<Ingredient, Byte> iMap = buffer.readMap(Ingredient::fromNetwork, FriendlyByteBuf::readByte);
            NonNullList<ItemStack> oList = DataHelper.copyAndCast(buffer.readList(FriendlyByteBuf::readItem));
            FluidStack inputFluid = buffer.readFluidStack();
            FluidStack outputFluid = buffer.readFluidStack();
            byte round = buffer.readByte();
            int roundTicks = buffer.readInt();
            float exp = buffer.readFloat();
            return new MillRecipe(rl, group, iMap, oList, inputFluid, outputFluid, round, roundTicks, exp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MillRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            buffer.writeMap(recipe.inputCosts, (friendlyByteBuf, ingredient) -> ingredient.toNetwork(friendlyByteBuf), (friendlyByteBuf, aByte) -> friendlyByteBuf.writeByte(aByte));
            buffer.writeCollection(recipe.output, (FriendlyByteBuf::writeItem));
            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeByte(recipe.round);
            buffer.writeInt(recipe.roundTicks);
            buffer.writeFloat(recipe.exp);
        }
    }*/
}
