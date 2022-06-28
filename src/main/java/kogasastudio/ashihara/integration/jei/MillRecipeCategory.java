package kogasastudio.ashihara.integration.jei;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.interaction.recipe.MillRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MillRecipeCategory implements IRecipeCategory<MillRecipe>
{
    public final ResourceLocation UID = new ResourceLocation(Ashihara.MODID, "mill");
    protected final IDrawableAnimated arrow;
    private final String title;
    private final IDrawable background;
    private final IDrawable icon;

    public MillRecipeCategory(IGuiHelper helper)
    {
        this.title = I18n.format(Ashihara.MODID + ".jei.mill");
        ResourceLocation bg = new ResourceLocation(Ashihara.MODID, "textures/gui/mill.png");
        this.icon = helper.createDrawableIngredient(new ItemStack(BlockRegistryHandler.MILL.get()));
        this.background = helper.createDrawable(bg, 0, 0, 176, 202);
        this.arrow = helper.drawableBuilder(bg, 176, 0, 17, 12).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public ResourceLocation getUid()
    {
        return this.UID;
    }

    @Override
    public Class<? extends MillRecipe> getRecipeClass()
    {
        return MillRecipe.class;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public IDrawable getBackground()
    {
        return this.background;
    }

    @Override
    public IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setIngredients(MillRecipe millRecipe, IIngredients iIngredients)
    {
        List<Ingredient> ingredients = millRecipe.getIngredients();
        List<List<ItemStack>> finalItems = new ArrayList<>();

        for (Ingredient ing : ingredients)
        {
            List<ItemStack> items = new ArrayList<>();
            ItemStack[] matchingItems = ing.getMatchingStacks();
            byte costCount = millRecipe.getCosts(ing);
            for (int j = 0; j < matchingItems.length; j += 1)
            {
                items.add(j, new ItemStack(matchingItems[j].getItem(), j <= costCount ? costCount : 1));
            }
            finalItems.add(items);
        }

        iIngredients.setInputLists(VanillaTypes.ITEM, finalItems);
        iIngredients.setOutputs(VanillaTypes.ITEM, millRecipe.getCraftingResult());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, MillRecipe millRecipe, IIngredients iIngredients)
    {

    }
}
