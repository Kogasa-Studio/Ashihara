package kogasastudio.ashihara.interaction.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;

public class CuttingBoardRecipe implements IRecipe<RecipeWrapper>
{
    private static IRecipeType<CuttingBoardRecipe> TYPE = IRecipeType.register(Ashihara.MODID + ":cutting_board");

    private final Ingredient input;
    private final ItemStack output;
    private final CuttingBoardToolType type;

    public ResourceLocation id;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, ItemStack outputIn, CuttingBoardToolType typeIn)
    {
        this.id = idIn;
        this.input = inputIn;
        this.output = outputIn;
        this.type = typeIn;
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn)
    {
        ArrayList<Ingredient> contained = new ArrayList<>();
        contained.add(this.input);
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(inv.getStackInSlot(0));
        return RecipeMatcher.findMatches(list, contained) != null;
    }

    @Override
    public boolean canFit(int width, int height) {return width * height >= 1;}

    @Override
    public NonNullList<Ingredient> getIngredients() {return NonNullList.from(this.input);}

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) {return this.output;}

    @Override
    public ItemStack getRecipeOutput() {return this.output;}

    public ITag<Item> getTool() {return this.type.getTool();}

    @Override
    public ResourceLocation getId() {return this.id;}

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {return TYPE;}

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CuttingBoardRecipe>
    {
        @Override
        public CuttingBoardRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient input = Ingredient.deserialize(json.get("ingredient"));
            if (input.hasNoMatchingItems()) throw new JsonParseException("No ingredient provided!");
            final ItemStack output = CraftingHelper.getItemStack(json, true);
            if (output.isEmpty()) throw new JsonParseException("No output items!");
            final CuttingBoardToolType type = CuttingBoardToolType.nameMatches(json.get("tool").getAsString());
            if (type.isEmpty()) throw new JsonParseException("No tool type chosen!");
            return new CuttingBoardRecipe(recipeId, input, output, type);
        }

        @Override
        public CuttingBoardRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient input = Ingredient.read(buffer);
            CompoundNBT nbt = buffer.readCompoundTag();
            ItemStack output = nbt == null ? ItemStack.EMPTY : ItemStack.read(nbt);
            CuttingBoardToolType type = CuttingBoardToolType.nameMatches(buffer.readString());

            return new CuttingBoardRecipe(recipeId, input, output, type);
        }

        @Override
        public void write(PacketBuffer buffer, CuttingBoardRecipe recipe)
        {
            recipe.input.write(buffer);
            CompoundNBT nbt = buffer.readCompoundTag();
            if (nbt != null) recipe.output.write(nbt);
            buffer.writeString(recipe.type.getName());
        }
    }
}
