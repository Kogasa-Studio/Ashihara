package kogasastudio.ashihara.dependencies.jei;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.dependencies.jei.category.CuttingBoardRecipeCategory;
import kogasastudio.ashihara.dependencies.jei.category.MillRecipeCategory;
import kogasastudio.ashihara.dependencies.jei.category.MortarRecipeCategory;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.MillRecipe;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

/**
 * @author DustW
 **/
@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin
{
    public static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD =
            new RecipeType<>(new ResourceLocation(Ashihara.MODID, "cutting_board"),
                    CuttingBoardRecipe.class);

    public static final RecipeType<MillRecipe> MILL =
            new RecipeType<>(new ResourceLocation(Ashihara.MODID, "mill"),
                    MillRecipe.class);

    public static final RecipeType<MortarRecipe> MORTAR =
            new RecipeType<>(new ResourceLocation(Ashihara.MODID, "mortar"),
                    MortarRecipe.class);

    protected <C extends Container, T extends Recipe<C>> List<T> getRecipe(net.minecraft.world.item.crafting.RecipeType<T> recipeType)
    {
        return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(recipeType);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new CuttingBoardRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new MillRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new MortarRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(CUTTING_BOARD, getRecipe(RecipeTypes.CUTTING_BOARD.get()));
        registration.addRecipes(MILL, getRecipe(RecipeTypes.MILL.get()));
        registration.addRecipes(MORTAR, getRecipe(RecipeTypes.MORTAR.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ItemRegistryHandler.CUTTING_BOARD.get()), CUTTING_BOARD);
        registration.addRecipeCatalyst(new ItemStack(ItemRegistryHandler.MILL.get()), MILL);
        registration.addRecipeCatalyst(new ItemStack(ItemRegistryHandler.MORTAR.get()), MORTAR);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        //registration.addRecipeClickArea(AirCompressorGui.class, 79, 34, 24, 17, AIR_COMPRESSOR);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        //registration.addRecipeTransferHandler(AirCompressorMenu.class, AIR_COMPRESSOR,
        //        36, 5, 0, 36);
        //registration.addRecipeTransferHandler(ShakerMenu.class, COCKTAIL,
        //        36, 5, 0, 36);
        //registration.addRecipeTransferHandler(BrewingBarrelMenu.class, BREWING_BARREL,
        //        36, 6, 0, 36);
    }

    public static final ResourceLocation UID = new ResourceLocation(Ashihara.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid()
    {
        return UID;
    }
}
