package kogasastudio.ashihara.compat.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.compat.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.registry.Items;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CuttingBoardRecipeCategory extends BaseRecipeCategory<CuttingBoardRecipe>
{
    protected static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/cutting_board.png");
    protected static final IRecipeType<CuttingBoardRecipe> CUTTING_BOARD = IRecipeType.create(Ashihara.MODID, "cutting_board", CuttingBoardRecipe.class);

    public CuttingBoardRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.CUTTING_BOARD,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.CUTTING_BOARD.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 166 - 90));
    }

    @Override
    public int getWidth()
    {
        return 60;
    }

    @Override
    public int getHeight()
    {
        return 34;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 1).add(recipe.getTool());

        var output = recipe.getOutput();

        for (int i = 0; i < output.size(); i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 1 + i * 20).add(output.get(i));
        }
    }

    @Override
    public IRecipeType<CuttingBoardRecipe> getRecipeType()
    {
        return CUTTING_BOARD;
    }
}