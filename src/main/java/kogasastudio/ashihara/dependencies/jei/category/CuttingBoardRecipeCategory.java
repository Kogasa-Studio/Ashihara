package kogasastudio.ashihara.dependencies.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
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
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @author DustW
 **/
public class CuttingBoardRecipeCategory extends BaseRecipeCategory<CuttingBoardRecipe>
{
    protected static final ResourceLocation BACKGROUND =
            new ResourceLocation(Ashihara.MODID, "textures/gui/jei/cutting_board.png");
    private final ImmutableRect2i textArea =
            new ImmutableRect2i(40, 1, 60, 34);

    public CuttingBoardRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.CUTTING_BOARD,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistryHandler.CUTTING_BOARD.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 166 - 90));
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Component toolName = new TextComponent(recipe.getTool().getName());
        ImmutableRect2i centerArea = MathUtil.centerTextArea(this.textArea, font, toolName);
        font.draw(stack, toolName, centerArea.getX(), centerArea.getY(), 0xFF808080);
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
