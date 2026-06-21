package kogasastudio.ashihara.compat.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.compat.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.FermentationRecipe;
import kogasastudio.ashihara.registry.Items;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import java.util.List;

public class FermentationRecipeCategory extends BaseRecipeCategory<FermentationRecipe>
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/fermentation.png");
    private static final int TEX_W = 256;
    private static final int TEX_H = 256;

    private final IDrawableStatic background;
    private final IDrawableAnimated progressBar;
    private final IDrawableStatic ventilationIcon;
    private final IDrawableStatic sealedIcon;

    // Left input slot positions (16 slots in 4 groups of 4)
    private static final int[][] INPUT_SLOTS = {
        {15, 15}, {33, 15}, {51, 15}, {69, 15},
        {22, 33}, {40, 33}, {58, 33}, {76, 33},
        {15, 51}, {33, 51}, {51, 51}, {69, 51},
        {22, 69}, {40, 69}, {58, 69}, {76, 69}
    };

    // Right output slot positions (mirrored: 240 - x, listed left to right per group)
    private static final int[][] OUTPUT_SLOTS = {
        {171, 15}, {189, 15}, {207, 15}, {225, 15},
        {164, 33}, {182, 33}, {200, 33}, {218, 33},
        {171, 51}, {189, 51}, {207, 51}, {225, 51},
        {164, 69}, {182, 69}, {200, 69}, {218, 69}
    };

    public FermentationRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.FERMENTATION,
            helper.createDrawableItemLike(Items.WOODEN_BASIN.get()),
            null);
        this.translateKey = "jei.ashihara.category.fermentation";

        this.background = helper.drawableBuilder(TEXTURE, 0, 0, 256, 100)
            .setTextureSize(TEX_W, TEX_H).build();

        IDrawableStatic progressStatic = helper.drawableBuilder(TEXTURE, 38, 100, 82, 31)
            .setTextureSize(TEX_W, TEX_H).build();
        this.progressBar = helper.createAnimatedDrawable(progressStatic, 400,
            IDrawableAnimated.StartDirection.LEFT, false);

        this.ventilationIcon = helper.drawableBuilder(TEXTURE, 0, 126, 32, 16)
            .setTextureSize(TEX_W, TEX_H).build();
        this.sealedIcon = helper.drawableBuilder(TEXTURE, 0, 100, 38, 21)
            .setTextureSize(TEX_W, TEX_H).build();
    }

    @Override
    public int getWidth() { return 256; }

    @Override
    public int getHeight() { return 100; }

    @Override
    public void draw(FermentationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        this.background.draw(guiGraphics);
        this.progressBar.draw(guiGraphics, 87, 48);

        // Ventilation / sealed icon
        Boolean needAir = recipe.getNeedAir();
        if (needAir != null)
        {
            if (needAir) ventilationIcon.draw(guiGraphics, 112, 16);
            else sealedIcon.draw(guiGraphics, 109, 27);
        }

        Font font = Minecraft.getInstance().font;

        // Time display at (218, 91)
        Component timeText = FermentationBlockEntity.stylizeTime(recipe.getTime() / 20);
        guiGraphics.text(font, timeText, 218, 91, 0xFF808080);

        // Container size requirement at (22, 93)
        List<String> sizes = recipe.getNeedSize();
        if (!sizes.isEmpty())
        {
            Component sizeLabel = Component.translatable("jei.ashihara.fermentation.need_size");
            guiGraphics.text(font, sizeLabel, 22, 93, 0xFF808080);
            int xOff = 22 + font.width(sizeLabel) + 2;
            for (String size : sizes)
            {
                Component sizeName = Component.translatable("jei.ashihara.fermentation.size." + size);
                guiGraphics.text(font, sizeName, xOff, 93, 0xFFFFFFFF);
                xOff += font.width(sizeName) + 4;
            }
        }

        // Fluid amount range labels
        int tolerance = recipe.getAmountTolerance();
        if (!recipe.getInputFluids().isEmpty())
        {
            int consume = recipe.getTotalConsumeAmount();
            drawFluidRange(guiGraphics, font, consume, consume + tolerance, 96, 72);

            // Min/max fluid amount text below input fluid slot
            Integer minFluid = recipe.getMinFluidAmount();
            Integer maxFluid = recipe.getMaxFluidAmount();
            if (minFluid != null || maxFluid != null)
            {
                String minMax = (minFluid != null ? formatFluid(minFluid) : "0") + "~" + (maxFluid != null ? formatFluid(maxFluid) : "\u221E");
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(96 - font.width(minMax) / 4f + 8, 87);
                guiGraphics.pose().scale(0.5f);
                guiGraphics.text(font, Component.literal(minMax), 0, 0, 0xFFFFFFFF);
                guiGraphics.pose().popMatrix();
            }
        }
        FluidStack fOut = recipe.getOutputFluid();
        if (!fOut.isEmpty() && !recipe.getInputFluids().isEmpty())
        {
            int consume = recipe.getTotalConsumeAmount();
            int outBase = fOut.getAmount();
            int outMax = consume > 0 ? outBase + tolerance * outBase / consume : outBase;
            drawFluidRange(guiGraphics, font, outBase, outMax, 144, 69);
        }
        else if (!fOut.isEmpty())
        {
            drawFluidAmount(guiGraphics, font, fOut.getAmount(), 144, 69);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FermentationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        // Ventilation tooltip hover area: (112,16)~(144,48)
        if (mouseX >= 112 && mouseX <= 144 && mouseY >= 16 && mouseY <= 48)
        {
            Boolean needAir = recipe.getNeedAir();
            if (needAir != null)
            {
                if (needAir)
                    tooltip.add(Component.translatable("tooltip.ashihara.fermentation.need_air"));
                else
                    tooltip.add(Component.translatable("tooltip.ashihara.fermentation.need_sealed"));
            }
        }
        // Fluid range tooltip hover area: (88,85)~(120,93)
        if (mouseX >= 88 && mouseX <= 120 && mouseY >= 85 && mouseY <= 93 && !recipe.getInputFluids().isEmpty())
        {
            tooltip.add(Component.translatable("jei.ashihara.fermentation.fluid_range_hint_1"));
            tooltip.add(Component.translatable("jei.ashihara.fermentation.fluid_range_hint_2"));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focuses)
    {
        // Input item slots
        List<SizedIngredient> inputs = recipe.getInputItems();
        for (int i = 0; i < inputs.size() && i < 16; i++)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]).add(inputs.get(i).ingredient());
        }

        // Fluid input
        List<FluidStackTemplate> fluidInputs = recipe.getInputFluids();
        if (!fluidInputs.isEmpty())
        {
            FluidStack fIn = fluidInputs.getFirst().create();
            builder.addSlot(RecipeIngredientRole.INPUT, 96, 69).add(fIn.getFluid(), fIn.getAmount()).setFluidRenderer(fIn.getAmount(), false, 16, 16);
        }

        // Output item slots
        List<ItemStack> outputs = recipe.getOutputStacks();
        for (int i = 0; i < outputs.size() && i < 16; i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOTS[i][0], OUTPUT_SLOTS[i][1]).add(outputs.get(i));
        }

        // Fluid output
        FluidStack fOut = recipe.getOutputFluid();
        if (!fOut.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 144, 69).add(fOut.getFluid(), fOut.getAmount()).setFluidRenderer(fOut.getAmount(), false, 16, 16);
        }
    }

    @Override
    public Identifier getIdentifier(FermentationRecipe recipe)
    {
        return recipe.getId();
    }

    private void drawFluidAmount(GuiGraphicsExtractor gfx, Font font, int amount, int slotX, int slotY)
    {
        String label = formatFluid(amount);
        int textWidth = font.width(label);
        gfx.pose().pushMatrix();
        gfx.pose().translate(slotX + 16 - textWidth / 2f, slotY + 8);
        gfx.pose().scale(0.5f);
        gfx.text(font, Component.literal(label), 0, 0, 0xFFFFFFFF);
        gfx.pose().popMatrix();
    }

    private void drawFluidRange(GuiGraphicsExtractor gfx, Font font, int min, int max, int slotX, int slotY)
    {
        String label = min == max ? formatFluid(min) : formatFluid(min) + "~" + formatFluid(max);
        int textWidth = font.width(label);
        gfx.pose().pushMatrix();
        gfx.pose().translate(slotX + 16 - textWidth / 2f, slotY + 8);
        gfx.pose().scale(0.5f);
        gfx.text(font, Component.literal(label), 0, 0, 0xFFFFFFFF);
        gfx.pose().popMatrix();
    }

    private static String formatFluid(int amount)
    {
        return amount >= 1000 ? (amount / 1000) + "B" : amount + "mB";
    }

}
