package kogasastudio.ashihara.compat.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.compat.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.registry.Items;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CuttingBoardRecipeCategory extends BaseRecipeCategory<CuttingBoardRecipe>
{
    protected static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/cutting_board.png");

    //text area scale:(x: 40, y: 1, width: 60, height: 34);
    private int x = 40;
    private int y = 1;

    public CuttingBoardRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.CUTTING_BOARD,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.CUTTING_BOARD.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 166 - 90));
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Component toolName = Component.translatable(recipe.getTool().getName());
        guiGraphics.text(font, toolName, Math.round((this.x - font.width(toolName)) / 2f), Math.round((this.y - font.lineHeight) / 2f), 0xFF808080);
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

        var output = recipe.getOutput();

        for (int i = 0; i < output.size(); i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 1 + i * 20).add(output.get(i));
        }
    }
}
