package kogasastudio.ashihara.interaction.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Arrays;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardRecipe implements IRecipe<RecipeWrapper>
{
    public static IRecipeType<CuttingBoardRecipe> TYPE = IRecipeType.register(Ashihara.MODID + ":cutting");
    private final Serializer serializer = new Serializer();

    private final Ingredient input;
    private final NonNullList<ItemStack> output;
    private final CuttingBoardToolType type;

    public ResourceLocation id;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, NonNullList<ItemStack> outputIn, CuttingBoardToolType typeIn)
    {
        this.id = idIn;
        this.input = inputIn;
        this.output = outputIn;
        this.type = typeIn;
    }

    public String getInfo()
    {
        return
        "\n{\n    input: " + Arrays.toString(this.input.getMatchingStacks())
        + "\n    output: " + this.output.toString()
        + "\n    id: " + this.id.toString()
        + "\n    type: " + this.type.getName()
        + "\n}";
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn)
    {
        ArrayList<Ingredient> contained = new ArrayList<>();
        contained.add(this.input);
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(inv.getStackInSlot(0));
        boolean b = RecipeMatcher.findMatches(list, contained) != null;
//        LOGGER_MAIN.info
//        (
//            "\n{\n    recipe: " + Arrays.toString(contained.get(0).getMatchingStacks())
//            + ";\n    provided: " + list.get(0).toString()
//            + ";\n    matches: " + b
//            + ";\n}"
//        );
        return RecipeMatcher.findMatches(list, contained) != null;
    }

    @Override
    public boolean canFit(int width, int height) {return width * height >= 1;}

    @Override
    public NonNullList<Ingredient> getIngredients() {return NonNullList.from(this.input);}

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) {return this.output.get(0);}

    @Override
    public ItemStack getRecipeOutput() {return this.output.get(0);}

    public NonNullList<ItemStack> getOutput() {return this.output;}

    public CuttingBoardToolType getTool() {return this.type;}

    @Override
    public ResourceLocation getId() {return this.id;}

    @Override
    public IRecipeSerializer<?> getSerializer() {return this.serializer;}

    @Override
    public IRecipeType<?> getType() {return TYPE;}

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CuttingBoardRecipe>
    {
        @Override
        public CuttingBoardRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient input = Ingredient.deserialize(json.get("ingredient"));
            if (input.hasNoMatchingItems()) throw new JsonParseException("No ingredient provided!");
            final NonNullList<ItemStack> output = readOutput(json.getAsJsonArray("result"));
            if (output.isEmpty()) throw new JsonParseException("No output items!");
            final CuttingBoardToolType type = CuttingBoardToolType.nameMatches(json.get("tool").getAsString());
            if (type.isEmpty()) throw new JsonParseException("No tool type chosen!");
            return new CuttingBoardRecipe(recipeId, input, output, type);
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

        @Override
        public CuttingBoardRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient input = Ingredient.read(buffer);
            CompoundNBT nbt = buffer.readCompoundTag();
            NonNullList<ItemStack> output = NonNullList.create();
            if (nbt != null)
            {
                output = NonNullList.withSize(nbt.getList("Items", 10).size(), ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(nbt, output);
            }
            CuttingBoardToolType type = CuttingBoardToolType.nameMatches(buffer.readString());

            return new CuttingBoardRecipe(recipeId, input, output, type);
        }

        @Override
        public void write(PacketBuffer buffer, CuttingBoardRecipe recipe)
        {
            recipe.input.write(buffer);
            CompoundNBT nbt = buffer.readCompoundTag();
            if (nbt != null) ItemStackHelper.saveAllItems(nbt, recipe.output);
            buffer.writeString(recipe.type.getName());
        }
    }
}
