package kogasastudio.ashihara.dependencies.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.dependencies.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author DustW
 **/
public class MortarRecipeCategory extends BaseRecipeCategory<MortarRecipe> {
    protected static final ResourceLocation BACKGROUND =
            new ResourceLocation(Ashihara.MODID, "textures/gui/jei/mortar.png");
    private final ImmutableRect2i textArea =
            new ImmutableRect2i(40, 1, 60, 34);

    public MortarRecipeCategory(IGuiHelper helper) {
        super(JeiPlugin.MORTAR,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistryHandler.MORTAR.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 113));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> input = recipe.input;

        for (int i = 0; i < input.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 80, 26 + i * 18)
                    .addIngredients(input.get(i));
        }

        NonNullList<ItemStack> output = recipe.output;

        for (int i = 0; i < output.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80 + 18, 26 + i * 18)
                    .addItemStack(output.get(i));
        }

        if (recipe.fluidCost != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 21, 15)
                    .addIngredient(ForgeTypes.FLUID_STACK, recipe.fluidCost)
                    .setFluidRenderer(4000, true, 37 - 21, 79 - 15);
        }
    }
}
