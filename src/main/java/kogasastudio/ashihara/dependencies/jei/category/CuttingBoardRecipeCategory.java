package kogasastudio.ashihara.dependencies.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.dependencies.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @author DustW
 **/
public class CuttingBoardRecipeCategory extends BaseRecipeCategory<CuttingBoardRecipe>
{
    protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/cutting_board.png");

    //text area scale:(x: 40, y: 1, width: 60, height: 34);
    private int x = 40;
    private int y = 1;
    private int width = 60;
    private int height = 34;

    public CuttingBoardRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.CUTTING_BOARD,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistryHandler.CUTTING_BOARD.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 166 - 90));
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Component toolName = Component.translatable(recipe.getTool().getName());
        guiGraphics.drawString(font, toolName, Math.round((this.x - font.width(toolName)) / 2f), Math.round((this.y - font.lineHeight) / 2f), 0xFF808080);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());

        var output = recipe.getOutput();

        for (int i = 0; i < output.size(); i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 1 + i * 20).addItemStack(output.get(i));
        }
    }
}
