package kogasastudio.ashihara.compat.jei.category;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.compat.jei.JeiPlugin;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class MortarRecipeCategory extends BaseRecipeCategory<MortarRecipe>
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/jei/mortar.png");
    private static final int TEX_W = 256;
    private static final int TEX_H = 256;
    private static final int BG_W = 192;
    private static final int BG_H = 64;
    private static final int ICON_SIZE = 9;
    private static final int ENTRY_GAP = 3;
    private static final int MAX_PER_ROW = 6;
    private static final int ROW_HEIGHT = ICON_SIZE + 3;

    private final IDrawableStatic background;
    private final IDrawableAnimated progressBar;
    private final IDrawableStatic iconHand;
    private final IDrawableStatic iconPestle;
    private final IDrawableStatic iconOtsuchi;

    private final List<int[]> lastIconPositions = new ArrayList<>();
    private final List<MortarBE.MortarToolType> lastIconTypes = new ArrayList<>();

    public MortarRecipeCategory(IGuiHelper helper)
    {
        super(JeiPlugin.MORTAR, helper.createDrawableItemLike(Items.MORTAR.get()), null);
        this.translateKey = "jei.ashihara.category.mortar";

        this.background = helper.drawableBuilder(TEXTURE, 0, 0, BG_W, BG_H).setTextureSize(TEX_W, TEX_H).build();

        IDrawableStatic progressStatic = helper.drawableBuilder(TEXTURE, 0, 64, 46, 6).setTextureSize(TEX_W, TEX_H).build();
        this.progressBar = helper.createAnimatedDrawable(progressStatic, 200, IDrawableAnimated.StartDirection.LEFT, false);

        this.iconHand = helper.drawableBuilder(TEXTURE, 0, 70, 9, 9).setTextureSize(TEX_W, TEX_H).build();
        this.iconPestle = helper.drawableBuilder(TEXTURE, 9, 70, 9, 9).setTextureSize(TEX_W, TEX_H).build();
        this.iconOtsuchi = helper.drawableBuilder(TEXTURE, 18, 70, 9, 9).setTextureSize(TEX_W, TEX_H).build();
    }

    @Override
    public int getWidth() { return BG_W; }

    @Override
    public int getHeight() { return 94; }

    @Override
    public void draw(MortarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        this.background.draw(guiGraphics);
        this.progressBar.draw(guiGraphics, 73, 42);

        Font font = Minecraft.getInstance().font;

        guiGraphics.text(font, Component.translatable("jei.ashihara.mortar.sequence_label"), 14, 52, 0xFFFFFFFF);

        FluidStack fluid = recipe.getFluidCost();
        if (!fluid.isEmpty())
        {
            int fluidSlotX = recipe.getFluidOpcode() == 0 ? 55 : 121;
            drawFluidAmount(guiGraphics, font, fluid.getAmount(), fluidSlotX, 33);
        }

        drawSequence(guiGraphics, font, recipe);
    }

    private void drawSequence(GuiGraphicsExtractor gfx, Font font, MortarRecipe recipe)
    {
        lastIconPositions.clear();
        lastIconTypes.clear();

        List<MortarBE.MortarToolType> seq = new ArrayList<>(recipe.getSequence());
        if (seq.isEmpty()) return;

        int startY = 64;
        int totalSteps = seq.size();
        int rowCount = (totalSteps + MAX_PER_ROW - 1) / MAX_PER_ROW;

        int stepIdx = 0;
        for (int row = 0; row < rowCount; row++)
        {
            int stepsInRow = Math.min(MAX_PER_ROW, totalSteps - row * MAX_PER_ROW);
            int rowWidth = 0;
            for (int i = 0; i < stepsInRow; i++)
            {
                int num = stepIdx + i + 1;
                rowWidth += font.width(num + ".") + 1 + ICON_SIZE;
                if (i < stepsInRow - 1) rowWidth += ENTRY_GAP;
            }

            int xOff = (BG_W - rowWidth) / 2;
            int y = startY + row * ROW_HEIGHT;

            for (int i = 0; i < stepsInRow; i++)
            {
                int num = stepIdx + 1;
                MortarBE.MortarToolType type = seq.get(stepIdx);
                String numStr = num + ".";
                gfx.text(font, Component.literal(numStr), xOff, y, 0xFFFFFFFF);
                xOff += font.width(numStr) + 1;

                getIconDrawable(type).draw(gfx, xOff, y);
                lastIconPositions.add(new int[]{xOff, y});
                lastIconTypes.add(type);

                xOff += ICON_SIZE + ENTRY_GAP;
                stepIdx++;
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MortarRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        for (int i = 0; i < lastIconPositions.size(); i++)
        {
            int[] pos = lastIconPositions.get(i);
            if (mouseX >= pos[0] && mouseX <= pos[0] + ICON_SIZE && mouseY >= pos[1] && mouseY <= pos[1] + ICON_SIZE)
            {
                tooltip.add(lastIconTypes.get(i).name);
                break;
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipe recipe, IFocusGroup focuses)
    {
        var inputs = recipe.getSizedIngredients();
        int[][] inPos = {{15, 15}, {33, 15}, {15, 33}, {33, 33}};
        for (int i = 0; i < inputs.size() && i < 4; i++)
        {
            builder.addSlot(RecipeIngredientRole.INPUT, inPos[i][0], inPos[i][1]).add(inputs.get(i).ingredient());
        }

        var outputs = recipe.getOutput();
        int[][] outPos = {{161, 15}, {143, 15}, {161, 33}, {143, 33}};
        for (int i = 0; i < outputs.size() && i < 4; i++)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, outPos[i][0], outPos[i][1]).add(outputs.get(i));
        }

        FluidStack fluid = recipe.getFluidCost();
        if (!fluid.isEmpty())
        {
            if (recipe.getFluidOpcode() == 0)
            {
                builder.addSlot(RecipeIngredientRole.INPUT, 55, 33).add(fluid.getFluid(), fluid.getAmount()).setFluidRenderer(fluid.getAmount(), false, 16, 16);
            }
            else
            {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 33).add(fluid.getFluid(), fluid.getAmount()).setFluidRenderer(fluid.getAmount(), false, 16, 16);
            }
        }
    }

    @Override
    public Identifier getIdentifier(MortarRecipe recipe)
    {
        return recipe.getId();
    }

    private IDrawableStatic getIconDrawable(MortarBE.MortarToolType type)
    {
        return switch (type)
        {
            case PESTLE -> iconPestle;
            case OTSUCHI -> iconOtsuchi;
            case HAND -> iconHand;
        };
    }

    private void drawFluidAmount(GuiGraphicsExtractor gfx, Font font, int amount, int slotX, int slotY)
    {
        String label = amount >= 1000 ? (amount / 1000) + "B" : amount + "mB";
        int textWidth = font.width(label);
        gfx.text(font, Component.literal(label), slotX + 16 - textWidth, slotY + 8, 0xFFFFFFFF);
    }
}