package kogasastudio.ashihara.compat.jei.category;

/**
 * @author DustW
 **/
public class MortarRecipeCategory //extends BaseRecipeCategory<MortarRecipe>
{
    /*protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/mortar.png");

//    (40, 1, 60, 34);

    public MortarRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.MORTAR,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistryHandler.MORTAR.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 113));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipe recipe, IFocusGroup focuses)
    {
        NonNullList<Ingredient> input = recipe.input;

        for (int i = 0; i < input.size(); i++)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 80, 26 + i * 18)
                    .addIngredients(input.get(i));
        }

        NonNullList<ItemStack> output = recipe.output;

        for (int i = 0; i < output.size(); i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80 + 18, 26 + i * 18)
                    .addItemStack(output.get(i));
        }

        if (recipe.fluidCost != null)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 21, 15)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.fluidCost)
                    .setFluidRenderer(4000, true, 37 - 21, 79 - 15);
        }
    }*/
}
