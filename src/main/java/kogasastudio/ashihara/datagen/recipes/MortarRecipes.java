package kogasastudio.ashihara.datagen.recipes;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author DustW
 **/
public class MortarRecipes extends ModGenRecipes {
    @Override
    protected void addRecipes() {
        addRecipe("mortar_bone_meal",
                new Ingredient[]{
                        Ingredient.of(Items.CARROT),
                        Ingredient.of(Items.CARROT)
                },
                new ItemStack[] {
                        new ItemStack(Items.BONE_MEAL)
                },
                null, 0, (byte) 1, new byte[] {2, 0, 2, 0});

        addRecipe("mortar_mochi",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get()),
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.MOCHI.get())
                },
                new FluidStack(Fluids.WATER, 1000),
                0, (byte) 1, new byte[] {2, 0, 2, 0, 2, 0, 2, 0});

        addRecipe("mortar_mochi_2x",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get()),
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get()),
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get()),
                        Ingredient.of(ItemRegistryHandler.RICE_POWDER.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.MOCHI.get()),
                        new ItemStack(ItemRegistryHandler.MOCHI.get())
                },
                new FluidStack(Fluids.WATER, 2000),
                0, (byte) 1, new byte[] {2, 0, 2, 0, 2, 0, 2, 0});

        addRecipe("mortar_rice_unthreshing",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.PADDY.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE.get())
                }, null, 0, (byte) 0, new byte[] {1, 1, 1, 1, 0});

        addRecipe("mortar_rice_unthreshing_2x",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get())
                }, null, 0, (byte) 0, new byte[] {1, 1, 1, 1, 0});

        addRecipe("mortar_rice_unthreshing_3x",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get())
                }, null, 0, (byte) 0, new byte[] {1, 1, 1, 1, 0});

        addRecipe("mortar_rice_unthreshing_4x",
                new Ingredient[]{
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get()),
                        Ingredient.of(ItemRegistryHandler.PADDY.get())
                },
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get()),
                        new ItemStack(ItemRegistryHandler.RICE.get())
                }, null, 0, (byte) 0, new byte[] {1, 1, 1, 1, 0});
    }

    void addRecipe(String name, Ingredient[] inputs, ItemStack[] outputs, FluidStack fluidCostIn,
                   int progressIn, byte recipeTypeIn, byte[] sequenceIn) {
        MortarRecipe recipe = new MortarRecipe(new ResourceLocation(Ashihara.MODID, name), "",
                NonNullList.of(Ingredient.EMPTY, inputs),
                NonNullList.of(ItemStack.EMPTY, outputs),
                fluidCostIn, progressIn, recipeTypeIn, sequenceIn);
        recipe.type = RecipeSerializers.MORTAR.get().getRegistryName().toString();
        addRecipe(recipe.getId(),baseRecipe(recipe), "mortar");
    }
}
