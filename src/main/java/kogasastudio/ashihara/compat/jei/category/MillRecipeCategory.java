package kogasastudio.ashihara.compat.jei.category;

/**
 * @author DustW
 **/
public class MillRecipeCategory //extends BaseRecipeCategory<MillRecipe>
{
    /*protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/mill.png");

//    (40, 1, 60, 34);

    public MillRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.MILL,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistryHandler.MILL.get())),
                helper.createDrawable(BACKGROUND, 0, 0, 176, 113));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MillRecipe recipe, IFocusGroup focuses)
    {
        List<Map.Entry<Ingredient, Byte>> entries = new ArrayList<>(recipe.inputCosts.entrySet());

        for (int i = 0; i < entries.size(); i++)
        {
            Ingredient ingredient = entries.get(i).getKey();
            int x = 55 + 18 * (i % 2);
            int y = 39 + 18 * (i / 2);
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(ingredient)
                    .setOverlay(new NumberDrawable(() -> recipe.getCosts(ingredient)), 11, 9);
        }

        NonNullList<ItemStack> output = recipe.output;

        for (int i = 0; i < output.size(); i++)
        {
            ItemStack itemStack = output.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 130 + 18 * (i % 2), 39 + 18 * (i / 2))
                    .addItemStack(itemStack);
        }

        if (recipe.inputFluid != null)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 17, 77 - 64)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(recipe.inputFluid))
                    .setFluidRenderer(4000, true, 16, 64);
        }

        if (recipe.outputFluid != null)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 54, 105 - 6)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(recipe.outputFluid))
                    .setFluidRenderer(4000, true, 64, 6);
        }
    }*/
}
