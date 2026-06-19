package kogasastudio.ashihara.compat.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.compat.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.HeatLevel;
import kogasastudio.ashihara.interaction.recipes.PotRecipe;
import kogasastudio.ashihara.registry.Items;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
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
import net.neoforged.neoforge.fluids.FluidStack;

public class PotRecipeCategory extends BaseRecipeCategory<PotRecipe>
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/pot.png");
    private static final int TEX_W = 256;
    private static final int TEX_H = 256;

    private final IDrawableStatic background;
    private final IDrawableAnimated progressBar;
    private final IDrawableStatic heatHigh;
    private final IDrawableStatic heatMedium;
    private final IDrawableStatic heatLow;

    public PotRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.POT,
            helper.createDrawableItemLike(Items.POT.get()),
            null);
        this.translateKey = "jei.ashihara.category.pot";

        this.background = helper.drawableBuilder(TEXTURE, 0, 0, 144, 64)
            .setTextureSize(TEX_W, TEX_H).build();

        IDrawableStatic progressStatic = helper.drawableBuilder(TEXTURE, 0, 64, 64, 42)
            .setTextureSize(TEX_W, TEX_H).build();
        this.progressBar = helper.createAnimatedDrawable(progressStatic, 200,
            IDrawableAnimated.StartDirection.LEFT, false);

        this.heatHigh = helper.drawableBuilder(TEXTURE, 151, 0, 29, 21)
            .setTextureSize(TEX_W, TEX_H).build();
        this.heatMedium = helper.drawableBuilder(TEXTURE, 151, 21, 29, 21)
            .setTextureSize(TEX_W, TEX_H).build();
        this.heatLow = helper.drawableBuilder(TEXTURE, 151, 42, 29, 21)
            .setTextureSize(TEX_W, TEX_H).build();
    }

    @Override
    public int getWidth() { return 144; }

    @Override
    public int getHeight() { return 64; }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        this.background.draw(guiGraphics);
        this.progressBar.draw(guiGraphics, 53, 3);

        HeatLevel heat = recipe.getHeatLevelRequired();
        if (heat != HeatLevel.NONE)
        {
            IDrawableStatic icon = switch (heat)
            {
                case HIGH -> heatHigh;
                case MEDIUM -> heatMedium;
                case LOW -> heatLow;
                default -> null;
            };
            if (icon != null) icon.draw(guiGraphics, 76, 30);
        }

        Font font = Minecraft.getInstance().font;
        String timeText = recipe.getCookTime() / 20 + "s";
        guiGraphics.text(font, Component.literal(timeText), 119, 53, 0xFF808080);

        // Fluid amount labels
        FluidStack fIn = recipe.getFluidCost();
        if (!fIn.isEmpty()) drawFluidAmount(guiGraphics, font, fIn.getAmount(), 55+8, 33+8);
        FluidStack fOut = recipe.getFluidProduction();
        if (!fOut.isEmpty()) drawFluidAmount(guiGraphics, font, fOut.getAmount(), 120+8, 33+8);
        if (recipe.getMaxFluidAmount() != null) guiGraphics.text(font, Component.translatable("tooltip.ashihara.jei.max", recipe.getMaxFluidAmount()), 55-8, 33+24, 0xFFFFFFFF);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, PotRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        // Heat tooltip hover area: (87,39)~(100,51)
        if (mouseX >= 87 && mouseX <= 100 && mouseY >= 39 && mouseY <= 51)
        {
            HeatLevel heat = recipe.getHeatLevelRequired();
            if (heat != HeatLevel.NONE)
            {
                tooltip.add(Component.translatable("jei.ashihara.pot.heat_required", heat.getDisplayName()));
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses)
    {
        // 2x2 input item slots
        var inputs = recipe.getInput();
        int[][] pos = {{15, 15}, {33, 15}, {15, 33}, {33, 33}};
        for (int i = 0; i < inputs.size() && i < 4; i++)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, pos[i][0], pos[i][1]).add(inputs.get(i).ingredient());
        }

        // Fluid input
        FluidStack fluidCost = recipe.getFluidCost();
        if (!fluidCost.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 55, 33).add(fluidCost.getFluid(), fluidCost.getAmount()).setFluidRenderer(fluidCost.getAmount(), false, 16, 16);
        }

        // Item output
        var output = recipe.getOutput();
        if (!output.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 15).add(output);
        }

        // Fluid output
        FluidStack fluidProd = recipe.getFluidProduction();
        if (!fluidProd.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 33).add(fluidProd.getFluid(), fluidProd.getAmount()).setFluidRenderer(fluidProd.getAmount(), false, 16, 16);
        }
    }

    private void drawFluidAmount(GuiGraphicsExtractor gfx, Font font, int amount, int slotX, int slotY)
    {
        String label = amount >= 1000 ? (amount / 1000) + "B" : amount + "mB";
        int textWidth = font.width(label);
        gfx.text(font, Component.literal(label), slotX + 16 - textWidth, slotY + 8, 0xFFFFFFFF);
    }

    private static final IRecipeType<PotRecipe> POT = IRecipeType.create(Ashihara.MODID, "pot", PotRecipe.class);
    @Override
    public IRecipeType<PotRecipe> getRecipeType()
    {
        return POT;
    }

    @Override
    public Identifier getIdentifier(PotRecipe recipe)
    {
        return recipe.getId();
    }
}