package kogasastudio.ashihara.interaction.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class CuttingBoardRecipe implements Recipe<RecipeWrapper> {
    public static RecipeType<CuttingBoardRecipe> TYPE = RecipeType.register(Ashihara.MODID + ":cutting");
    private final Serializer serializer = new Serializer();

    private final Ingredient input;
    private final NonNullList<ItemStack> output;
    private final CuttingBoardToolType type;

    public ResourceLocation id;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, NonNullList<ItemStack> outputIn, CuttingBoardToolType typeIn) {
        this.id = idIn;
        this.input = inputIn;
        this.output = outputIn;
        this.type = typeIn;
    }

    public String getInfo() {
        return
                "\n{\n    input: " + Arrays.toString(this.input.getItems())
                        + "\n    output: " + this.output.toString()
                        + "\n    id: " + this.id.toString()
                        + "\n    type: " + this.type.getName()
                        + "\n}";
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        ArrayList<Ingredient> contained = new ArrayList<>();
        contained.add(this.input);
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(inv.getItem(0));
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(this.input);
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.get(0);
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.get(0);
    }

    public NonNullList<ItemStack> getOutput() {
        return this.output;
    }

    public CuttingBoardToolType getTool() {
        return this.type;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CuttingBoardRecipe> {
        private static NonNullList<ItemStack> readOutput(JsonArray itemStackArray) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for (int i = 0; i < itemStackArray.size(); ++i) {
                ItemStack stack = CraftingHelper.getItemStack(itemStackArray.get(i).getAsJsonObject(), true);
                if (!stack.isEmpty()) {
                    nonnulllist.add(stack);
                }
            }

            return nonnulllist;
        }

        @Override
        public CuttingBoardRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            if (input.isEmpty()) throw new JsonParseException("No ingredient provided!");
            final NonNullList<ItemStack> output = readOutput(json.getAsJsonArray("result"));
            if (output.isEmpty()) throw new JsonParseException("No output items!");
            final CuttingBoardToolType type = CuttingBoardToolType.nameMatches(json.get("tool").getAsString());
            if (type.isEmpty()) throw new JsonParseException("No tool type chosen!");
            return new CuttingBoardRecipe(recipeId, input, output, type);
        }

        @Override
        public CuttingBoardRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            CompoundTag nbt = buffer.readNbt();
            NonNullList<ItemStack> output = NonNullList.create();
            if (nbt != null) {
                output = NonNullList.withSize(nbt.getList("Items", 10).size(), ItemStack.EMPTY);
                ContainerHelper.loadAllItems(nbt, output);
            }
            CuttingBoardToolType type = CuttingBoardToolType.nameMatches(buffer.readUtf());

            return new CuttingBoardRecipe(recipeId, input, output, type);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
            recipe.input.toNetwork(buffer);
            CompoundTag nbt = buffer.readNbt();
            if (nbt != null) ContainerHelper.saveAllItems(nbt, recipe.output);
            buffer.writeUtf(recipe.type.getName());
        }
    }
}
