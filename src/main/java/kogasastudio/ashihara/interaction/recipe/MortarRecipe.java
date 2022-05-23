package kogasastudio.ashihara.interaction.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class MortarRecipe implements IRecipe<RecipeWrapper>
{
    public static IRecipeType<MortarRecipe> TYPE = IRecipeType.register(Ashihara.MODID + ":mortar");
    public static final Serializer SERIALIZER = new Serializer();

    private final NonNullList<Ingredient> input;
    private final NonNullList<ItemStack> output;

    private final FluidStack fluidCost;

    public ResourceLocation id;
    public String group;

    public int progress;
    public byte recipeType;
    public byte[] sequence;

    public MortarRecipe(ResourceLocation idIn, String groupId, NonNullList<Ingredient> inputIn, NonNullList<ItemStack> outputIn, FluidStack fluidCostIn, int progressIn, byte recipeTypeIn, byte[] sequenceIn)
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

    public FluidStack getFluidCost() {return this.fluidCost.copy();}

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn)
    {
        ArrayList<ItemStack> inputs = new ArrayList<>();

        for (int j = 0; j < 4; ++j)
        {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {inputs.add(itemstack);}
        }
        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {return SERIALIZER;}

    @Override
    public IRecipeType<?> getType() {return TYPE;}

    @Override
    public ItemStack assemble(RecipeWrapper inv) {return this.output.get(0);}

    @Override
    public boolean canCraftInDimensions(int width, int height) {return width * height >= this.input.size();}

    @Override
    public ItemStack getResultItem() {return this.output.get(0).copy();}

    @Override
    public NonNullList<Ingredient> getIngredients() {return this.input;}

    public NonNullList<ItemStack> getOutput() {return this.output;}

    @Override
    public String getGroup() {return this.group;}

    @Override
    public ResourceLocation getId() {return this.id;}
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MortarRecipe>
    {

        @Override
        public MortarRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final String groupIn = JSONUtils.getAsString(json, "group", "");
            final NonNullList<Ingredient> inputIn = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));
            if (inputIn.isEmpty()) {throw new JsonParseException("No ingredients for mortar recipe");}
            else if (inputIn.size() > 4) {throw new JsonParseException("Too many ingredients for mortar recipe! The max is 4");}
            else
            {
                final NonNullList<ItemStack> outputIn = readOutput(JSONUtils.getAsJsonArray(json, "result"));
                final byte recipeTypeIn = JSONUtils.getAsByte(json, "recipeType", (byte) 1);
                final byte[] sequenceIn = readSequence(JSONUtils.getAsJsonArray(json, "sequence"));
                if (recipeTypeIn != 2 && sequenceIn.length < 1) {throw new JsonParseException("No sequence provided!");}
                else
                {
                    final int progressIn = recipeTypeIn == 2 ? JSONUtils.getAsInt(json, "progress", 1) : sequenceIn.length;
                    if (json.has("fluid"))
                    {
                        JsonObject fluid = json.get("fluid").getAsJsonObject();
                        FluidStack fluidCost = readFluid(fluid);
                        return new MortarRecipe(recipeId, groupIn, inputIn, outputIn, fluidCost, progressIn, recipeTypeIn, sequenceIn);
                    }
                    else return new MortarRecipe(recipeId, groupIn, inputIn, outputIn, FluidStack.EMPTY, progressIn, recipeTypeIn, sequenceIn);
                }
            }
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

        private static NonNullList<ItemStack> readOutput(JsonArray itemStackArray)
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for (int i = 0; i < itemStackArray.size(); ++i)
            {
                ItemStack stack = CraftingHelper.getItemStack(itemStackArray.get(i).getAsJsonObject(), true);
                if (!stack.isEmpty()) {nonnulllist.add(stack);}
            }

            return nonnulllist;
        }

        private static byte[] readSequence(JsonArray sequenceArray)
        {
            byte[] sequences = new byte[sequenceArray.size()];

            for (int i = 0; i < sequenceArray.size(); ++i)
            {
                byte b = sequenceArray.get(i).getAsByte();
                if (b <= 2) {sequences[i] = b;}
                else throw new JsonParseException("Unsupported tool type!");
            }
            return sequences;
        }

        private static FluidStack readFluid(JsonObject object)
        {
            if (object == null) return FluidStack.EMPTY;

            String fluid = JSONUtils.getAsString(object, "fluid");
            int amount = JSONUtils.getAsInt(object, "amount");

            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("FluidName", fluid);
            nbt.putInt("Amount", amount);

            return FluidStack.loadFluidStackFromNBT(nbt);
        }

        @Override
        public MortarRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            String groupIn = buffer.readUtf(32767);
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(4, Ingredient.EMPTY);
            NonNullList<ItemStack> outputItemsIn = NonNullList.withSize(4, ItemStack.EMPTY);

            FluidStack fluidCost = FluidStack.readFromPacket(buffer);

            for (int j = 0; j < inputItemsIn.size(); ++j) {inputItemsIn.set(j, Ingredient.fromNetwork(buffer));}
            CompoundNBT nbt = buffer.readNbt();
            if (nbt != null) ItemStackHelper.loadAllItems(nbt, outputItemsIn);

            int progressIn = buffer.readVarInt();
            byte recipeTypeIn = buffer.readByte();
            byte[] sequenceIn = buffer.readByteArray();

            return new MortarRecipe(recipeId, groupIn, inputItemsIn, outputItemsIn, fluidCost, progressIn, recipeTypeIn, sequenceIn);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, MortarRecipe recipe)
        {
            buffer.writeUtf(recipe.group);

            for (Ingredient ingredient : recipe.input) {ingredient.toNetwork(buffer);}
            CompoundNBT nbt = buffer.readNbt();
            if (nbt != null) ItemStackHelper.saveAllItems(nbt, recipe.output);

            recipe.fluidCost.writeToPacket(buffer);

            buffer.writeByte(recipe.recipeType);
            buffer.writeVarInt(recipe.progress);

            buffer.writeBytes(recipe.sequence);
        }
    }
}
