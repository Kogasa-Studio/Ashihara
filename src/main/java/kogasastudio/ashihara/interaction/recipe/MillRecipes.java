package kogasastudio.ashihara.interaction.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;

import static kogasastudio.ashihara.item.ItemRegistryHandler.*;

public class MillRecipes
{
    private static final ArrayList<ItemStack> rice = new ArrayList<>();
    private static final ArrayList<ItemStack> paddy = new ArrayList<>();

    private static ArrayList<ItemStack> initRice() {rice.add(new ItemStack(RICE.get(), 2));return rice;}

    public static final MillRecipe RICE_TO_RICE_POWDER = new MillRecipe(Ingredient.fromStacks((new ItemStack(UNTHRESHED_RICE.get()))), initRice(), (byte) 3, 100);

    public static ArrayList<MillRecipe> getRecipes()
    {
        ArrayList<MillRecipe> recipes = new ArrayList<>();
        recipes.add(RICE_TO_RICE_POWDER);
        return recipes;
    }
}
