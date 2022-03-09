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

    public ResourceLocation id;
    public String group;

    public int progress;
    public byte recipeType;
    public byte[] sequence;

    public MortarRecipe(ResourceLocation idIn, String groupId, NonNullList<Ingredient> inputIn, NonNullList<ItemStack> outputIn, int progressIn, byte recipeTypeIn, byte[] sequenceIn)
    {
        this.id = idIn;
        this.group = groupId;

        this.input = inputIn;
        this.output = outputIn;
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

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn)
    {
        ArrayList<ItemStack> inputs = new ArrayList<>();

        for (int j = 0; j < 4; ++j)
        {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {inputs.add(itemstack);}
        }
        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {return SERIALIZER;}

    @Override
    public IRecipeType<?> getType() {return TYPE;}

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) {return this.output.get(0);}

    @Override
    public boolean canFit(int width, int height) {return width * height >= this.input.size();}

    @Override
    public ItemStack getRecipeOutput() {return this.output.get(0).copy();}

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
        public MortarRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            final String groupIn = JSONUtils.getString(json, "group", "");
            final NonNullList<Ingredient> inputIn = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (inputIn.isEmpty()) {throw new JsonParseException("No ingredients for mortar recipe");}
            else if (inputIn.size() > 4) {throw new JsonParseException("Too many ingredients for mortar recipe! The max is 4");}
            else
            {
                final NonNullList<ItemStack> outputIn = readOutput(JSONUtils.getJsonArray(json, "result"));
                final byte recipeTypeIn = JSONUtils.getByte(json, "recipeType", (byte) 1);
                final byte[] sequenceIn = readSequence(JSONUtils.getJsonArray(json, "sequence"));
//                final int progressIn = JSONUtils.getInt(json, "progress", 100);
                if (recipeTypeIn != 2 && sequenceIn.length < 1) {throw new JsonParseException("No sequence provided!");}
                else
                {
                    final int progressIn = recipeTypeIn == 2 ? JSONUtils.getInt(json, "progress", 1) : sequenceIn.length;
                    return new MortarRecipe(recipeId, groupIn, inputIn, outputIn, progressIn, recipeTypeIn, sequenceIn);
                }
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray)
        {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i)
            {
                Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
                if (!ingredient.hasNoMatchingItems()) {nonnulllist.add(ingredient);}
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

        @Override
        public MortarRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            String groupIn = buffer.readString(32767);
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(4, Ingredient.EMPTY);
            NonNullList<ItemStack> outputItemsIn = NonNullList.withSize(4, ItemStack.EMPTY);

            for (int j = 0; j < inputItemsIn.size(); ++j) {inputItemsIn.set(j, Ingredient.read(buffer));}
            CompoundNBT nbt = buffer.readCompoundTag();
            if (nbt != null) ItemStackHelper.loadAllItems(nbt, outputItemsIn);

            int progressIn = buffer.readVarInt();
            byte recipeTypeIn = buffer.readByte();
            byte[] sequenceIn = buffer.readByteArray();
            return new MortarRecipe(recipeId, groupIn, inputItemsIn, outputItemsIn, progressIn, recipeTypeIn, sequenceIn);
        }

        @Override
        public void write(PacketBuffer buffer, MortarRecipe recipe)
        {
            buffer.writeString(recipe.group);

            for (Ingredient ingredient : recipe.input) {ingredient.write(buffer);}
            CompoundNBT nbt = buffer.readCompoundTag();
            if (nbt != null) ItemStackHelper.saveAllItems(nbt, recipe.output);

            buffer.writeByte(recipe.recipeType);
            buffer.writeVarInt(recipe.progress);

            buffer.writeBytes(recipe.sequence);
        }
    }
}
