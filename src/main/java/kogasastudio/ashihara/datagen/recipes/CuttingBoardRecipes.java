package kogasastudio.ashihara.datagen.recipes;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author DustW
 **/
public class CuttingBoardRecipes extends ModGenRecipes
{
    @Override
    protected void addRecipes()
    {
        addRecipe("cut_dried_bamboo",
                Ingredient.of(ItemRegistryHandler.DRIED_BAMBOO.get()),
                CuttingBoardToolType.KNIFE,
                new ItemStack(ItemRegistryHandler.BAMBOO_MATERIAL.get(), 4));

        addRecipe("cut_log_acacia",
                Ingredient.of(Items.ACACIA_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_ACACIA_LOG));

        addRecipe("cut_log_birch",
                Ingredient.of(Items.BIRCH_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_BIRCH_LOG));

        addRecipe("cut_log_crimson",
                Ingredient.of(Items.CRIMSON_STEM),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_CRIMSON_STEM));

        addRecipe("cut_log_dark_oak",
                Ingredient.of(Items.DARK_OAK_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_DARK_OAK_LOG));

        addRecipe("cut_log_jungle",
                Ingredient.of(Items.JUNGLE_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_JUNGLE_LOG));

        addRecipe("cut_log_oak",
                Ingredient.of(Items.OAK_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_OAK_LOG));

        addRecipe("cut_log_spruce",
                Ingredient.of(Items.SPRUCE_LOG),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_SPRUCE_LOG));

        addRecipe("cut_log_warped",
                Ingredient.of(Items.WARPED_STEM),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.STRIPPED_WARPED_STEM));

        addRecipe("cut_melon",
                Ingredient.of(Items.MELON),
                CuttingBoardToolType.AXE,
                new ItemStack(Items.MELON_SLICE, 9));
    }

    protected void addRecipe(String name, Ingredient ingredient, CuttingBoardToolType type, ItemStack... itemStacks)
    {
        CuttingBoardRecipe recipe = new CuttingBoardRecipe(
                new ResourceLocation(Ashihara.MODID, name),
                ingredient,
                NonNullList.of(ItemStack.EMPTY, itemStacks),
                type
        );
        recipe.type = RecipeSerializers.CUTTING_BOARD.getId().getPath();
        addRecipe(recipe.getId(), baseRecipe(recipe), "cutting");
    }
}
