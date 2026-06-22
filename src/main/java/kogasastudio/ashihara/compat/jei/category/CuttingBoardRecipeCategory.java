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
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CuttingBoardRecipeCategory extends BaseRecipeCategory<CuttingBoardRecipe>
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/cutting_board.png");
    private static final int TEX_W = 128;
    private static final int TEX_H = 128;
    private static final int BG_W = 82;
    private static final int BG_H = 61;

    protected static final IRecipeType<CuttingBoardRecipe> CUTTING_BOARD = IRecipeType.create(Ashihara.MODID, "cutting", CuttingBoardRecipe.class);

    public CuttingBoardRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.CUTTING_BOARD,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.CUTTING_BOARD.get())),
                helper.drawableBuilder(TEXTURE, 0, 0, BG_W, BG_H).setTextureSize(TEX_W, TEX_H).build());
        this.translateKey = "jei.ashihara.category.cutting";
    }

    @Override
    public int getWidth() { return BG_W; }

    @Override
    public int getHeight() { return BG_H; }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        this.background.draw(guiGraphics);
        if (recipe.shouldConsume())
        {
            Font font = Minecraft.getInstance().font;
            guiGraphics.text(font, Component.translatable("jei.ashihara.cutting_board.consume_durability"), 54, 17, 0xFFFFFFFF);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 36).add(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 8).add(recipe.getTool());

        var output = recipe.getOutput();
        for (int i = 0; i < output.size() && i < 4; i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 57 + (i % 2) * 18, 36 + (i / 2) * 18).add(output.get(i));
        }
    }

    @Override
    public IRecipeType<CuttingBoardRecipe> getRecipeType()
    {
        return CUTTING_BOARD;
    }
}