package kogasastudio.ashihara.compat.jei;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.compat.jei.category.CuttingBoardRecipeCategory;
import kogasastudio.ashihara.compat.jei.category.PotRecipeCategory;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.PotRecipe;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.registry.RecipeTypes;
import kogasastudio.ashihara.registry.Items;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.List;

/**
 * @author DustW
 **/
@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin
{
    public static final IRecipeType<CuttingBoardRecipe> CUTTING_BOARD = IRecipeType.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "cutting_board"), CuttingBoardRecipe.class);

    //public static final IRecipeType<MillRecipe> MILL = IRecipeType.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "mill"), MillRecipe.class);

    public static final IRecipeType<PotRecipe> POT = IRecipeType.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "pot"), PotRecipe.class);

    public static final IRecipeType<MortarRecipe> MORTAR = IRecipeType.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "mortar"), MortarRecipe.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new CuttingBoardRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new PotRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        //registry.addRecipeCategories(new MillRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        //registry.addRecipeCategories(new MortarRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null)
        {
            List<CuttingBoardRecipe> cuttingRecipes = RecipeHelper.getRecipesByType(level, RecipeTypes.CUTTING_BOARD.get()).stream().map(RecipeHolder::value).toList();
            registration.addRecipes(CUTTING_BOARD, cuttingRecipes);

            List<PotRecipe> potRecipes = RecipeHelper.getRecipesByType(level, RecipeTypes.POT.get()).stream().map(RecipeHolder::value).toList();
            registration.addRecipes(POT, potRecipes);
        }
        //registration.addRecipes(MILL, getRecipe(RecipeTypes.MILL.get()));
        //registration.addRecipes(MORTAR, getRecipe(RecipeTypes.MORTAR.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addCraftingStation(CUTTING_BOARD, Items.CUTTING_BOARD.get());
        registration.addCraftingStation(POT, Items.POT.get());
        //registration.addCraftingStation(MILL, new ItemStack(Items.MILL.get()));
        registration.addCraftingStation(MORTAR, new ItemStack(Items.MORTAR.get()));
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

    public static final Identifier UID = Identifier.fromNamespaceAndPath(Ashihara.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid()
    {
        return UID;
    }
}
