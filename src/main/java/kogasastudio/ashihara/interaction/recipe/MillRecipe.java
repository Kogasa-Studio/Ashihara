package kogasastudio.ashihara.interaction.recipe;

import com.google.gson.*;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.NonNullList;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.item.ItemStack.EMPTY;

public class MillRecipe implements Recipe<RecipeWrapper>
{
    public static RecipeType<MillRecipe> TYPE = RecipeType.register(Ashihara.MODID + ":mill");
    public static final Serializer SERIALIZER = new Serializer();

    private final NonNullList<Ingredient> input;
    private final Map<Ingredient, Byte> inputCosts;
    private final NonNullList<ItemStack> output;

    private final FluidStack inputFluid;
    private final FluidStack outputFluid;

    public ResourceLocation id;
    public String group;
    public byte round;
    public int roundTicks;
    public float exp;

    public MillRecipe
    (
        ResourceLocation recipeId,
        String groupId,
        NonNullList<Ingredient> inputIn,
        Map<Ingredient, Byte> inputCostsIn,
        NonNullList<ItemStack> outputIn,
        FluidStack inFluid,
        FluidStack outFluid,
        byte roundIn, int roundTicksIn, float expIn
    )
    {
        this.id = recipeId;
        this.group = groupId;

        this.input = inputIn;
        this.inputCosts = inputCostsIn;
        this.output = outputIn;
        this.inputFluid = inFluid;
        this.outputFluid = outFluid;
        this.round = roundIn;
        this.roundTicks = roundTicksIn;
        this.exp = expIn;
    }

    public FluidStack getInputFluid() {return this.inputFluid.copy();}

    public FluidStack getOutputFluid() {return this.outputFluid.copy();}

    public byte getCosts(Ingredient ingredient)
    {
        return this.inputCosts.get(ingredient);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {return this.input;}

    @Override
    public String getGroup() {return this.group;}

    @Override
    public ResourceLocation getId() {return this.id;}

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn)
    {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        boolean costMatches = false;

        for (int j = 0; j < 4; ++j)
        {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {inputs.add(itemstack);}
        }
        for (Ingredient ingredient : this.input)
        {
            for (ItemStack stack : inputs)
            {
                if (ingredient.test(stack) && stack.getCount() >= this.inputCosts.get(ingredient)) {costMatches = true;break;}
                else costMatches = false;
            }
            if (!costMatches) break;
        }
        return RecipeMatcher.findMatches(inputs, this.input) != null && costMatches;
    }

    //这个没啥用
    @Override
    public ItemStack assemble(RecipeWrapper inv) {return this.output.get(0).copy();}

    //这个也没啥用
    @Override
    public ItemStack getResultItem() {return this.output.get(0).copy();}

    //出结果的
    public NonNullList<ItemStack> getCraftingResult() {return this.output;}

    @Override
    public boolean canCraftInDimensions(int width, int height) {return width * height >= this.input.size();}

    @Override
    public RecipeType<?> getType() {return TYPE;}

    @Override
    public RecipeSerializer<?> getSerializer() {return SERIALIZER;}

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MillRecipe>
    {
        @Override
        public MillRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final String groupIn = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> inputIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputIn.isEmpty()) {throw new JsonParseException("No ingredients for mill recipe");}
            else if (inputIn.size() > 4) {throw new JsonParseException("Too many ingredients for mill recipe! The max is 4");}
            else
            {
                final Map<Ingredient, Byte> costsIn = serializeCosts(inputIn, GsonHelper.getAsJsonArray(json, "costs"));
                final NonNullList<ItemStack> outputIn = readOutput(GsonHelper.getAsJsonArray(json, "result", null));
                final byte roundIn = GsonHelper.getAsByte(json, "rounds", (byte) 1);
                final int roundTicksIn = GsonHelper.getAsInt(json, "roundTicks", 100);
                final float expIn = GsonHelper.getAsFloat(json, "experience", 20.0f);
                if (json.has("fluids"))
                {
                    JsonElement fluids = json.get("fluids");
                    JsonObject in = fluids.getAsJsonObject().has("input") ? fluids.getAsJsonObject().get("input").getAsJsonObject() : null;
                    JsonObject out = fluids.getAsJsonObject().has("output") ? fluids.getAsJsonObject().get("output").getAsJsonObject() : null;
                    if (!(in != null || out != null)) {throw new JsonParseException("Fluid ingredient announced but is content empty!");}
                    FluidStack inFluid = readFluid(in);
                    FluidStack outFluid = readFluid(out);
                    return new MillRecipe(recipeId, groupIn, inputIn, costsIn, outputIn, inFluid, outFluid, roundIn, roundTicksIn, expIn);
                }
                else return new MillRecipe(recipeId, groupIn, inputIn, costsIn, outputIn, FluidStack.EMPTY, FluidStack.EMPTY, roundIn, roundTicksIn, expIn);
            }
        }

        private static FluidStack readFluid(JsonObject object)
        {
            if (object == null) return FluidStack.EMPTY;

            String fluid = GsonHelper.getAsString(object, "fluid");
            int amount = GsonHelper.getAsInt(object, "amount");

            CompoundTag nbt = new CompoundTag();
            nbt.putString("FluidName", fluid);
            nbt.putInt("Amount", amount);

            return FluidStack.loadFluidStackFromNBT(nbt);
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray)
        {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i)
            {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {nonnulllist.add(ingredient);}
            }

            return nonnulllist;
        }

        private static Map<Ingredient, Byte> serializeCosts(NonNullList<Ingredient> ingredients, JsonArray costArray)
        {
            if (costArray.size() > ingredients.size()) {throw new JsonParseException("Cost counts does not match ingredient counts!");}
            Map<Ingredient, Byte> map = new HashMap<>(ingredients.size());
            for (int i = 0; i < ingredients.size(); i += 1)
            {
                byte b = costArray.get(i).getAsByte();
                if (b > 64) {throw new JsonParseException("Costing over 64!");}
                map.put(ingredients.get(i), costArray.get(i).getAsByte());
            }
            return map;
        }

        private static NonNullList<ItemStack> readOutput(JsonArray itemStackArray)
        {
            if (itemStackArray == null) return NonNullList.withSize(1, EMPTY);

            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for (int i = 0; i < itemStackArray.size(); ++i)
            {
                ItemStack stack = CraftingHelper.getItemStack(itemStackArray.get(i).getAsJsonObject(), true);
                if (!stack.isEmpty()) {nonnulllist.add(stack);}
            }

            return nonnulllist;
        }

        @Override
        public MillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String groupIn = buffer.readUtf(32767);
            Map<Ingredient, Byte> costsIn = new HashMap<>(4);
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(4, Ingredient.EMPTY);
            NonNullList<ItemStack> outputItemsIn = NonNullList.withSize(4, EMPTY);

            for (int j = 0; j < inputItemsIn.size(); ++j)
            {
                inputItemsIn.set(j, Ingredient.fromNetwork(buffer));
                costsIn.put(Ingredient.fromNetwork(buffer), buffer.readByteArray()[j]);
            }
            CompoundTag nbt = buffer.readNbt();
            if (nbt != null) ItemStackHelper.loadAllItems(nbt, outputItemsIn);

            FluidStack inFluid = FluidStack.readFromPacket(buffer);
            FluidStack outFluid = FluidStack.readFromPacket(buffer);

            byte roundIn = buffer.readByte();
            int roundTicksIn = buffer.readVarInt();
            float expIn = buffer.readFloat();
            return new MillRecipe(recipeId, groupIn, inputItemsIn, costsIn, outputItemsIn, inFluid, outFluid, roundIn, roundTicksIn, expIn);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MillRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            byte[] bytes = new byte[recipe.input.size()];
            int i = 0;

            for (Ingredient ingredient : recipe.input)
            {
                ingredient.toNetwork(buffer);
                bytes[i] = recipe.inputCosts.get(ingredient);
                i += 1;
            }
            CompoundTag nbt = buffer.readNbt();
            if (nbt != null) ItemStackHelper.saveAllItems(nbt, recipe.output);

            recipe.inputFluid.writeToPacket(buffer);
            recipe.outputFluid.writeToPacket(buffer);

            buffer.writeByte(recipe.round);
            buffer.writeVarInt(recipe.roundTicks);
            buffer.writeFloat(recipe.exp);

            buffer.writeBytes(bytes);
        }
    }
}
