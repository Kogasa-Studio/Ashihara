package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.container.MortarContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MortarScreen extends AbstractContainerScreen<MortarContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("ashihara:textures/gui/mortar.png");
    private int progress;
    private int stepStateCode;

    public MortarScreen(MortarContainer container, Inventory inv, Component title)
    {
        super(container, inv, title);
        this.progress = container.getArrowHeight();
        this.stepStateCode = container.getNextStep();
    }

    private MortarToolTypes getNextStep(int stateCode)
    {
        return switch (stateCode)
        {
            case 0 -> MortarToolTypes.HANDS;
            case 1 -> MortarToolTypes.PESTLE;
            case 2 -> MortarToolTypes.OTSUCHI;
            default -> MortarToolTypes.NONE;
        };
    }

    @Override
    public void containerTick()
    {
        int progressIn = this.menu.getArrowHeight();
        int stepStateCodeIn = this.menu.getNextStep();
        if (progressIn != this.progress)
        {
            this.progress = progressIn;
        }
        if (stepStateCodeIn != this.stepStateCode)
        {
            this.stepStateCode = stepStateCodeIn;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y)
    {
        super.renderTooltip(guiGraphics, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        FluidTank tank = this.menu.getTE().getTank().orElse(new FluidTank(0));

        RenderHelper.drawFluidToolTip
                (this, guiGraphics, x, y, i + 17, j + 13, 16, 64, tank.getFluid(), tank.getCapacity());
    }

    final FluidStackRenderer renderer = new FluidStackRenderer(4000, 37 - 21, 79 - 15);

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY)
    {
        if (this.minecraft == null)
        {
            return;
        }

        RenderSystem.setShaderTexture(0, GUI);
        MortarToolTypes nextStep = this.getNextStep(this.stepStateCode);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, i, j, 0, 0, 176, 202, 256, 256);
        guiGraphics.blit(GUI, i + 126, j + 23, 176, 23, 15, progress);
        guiGraphics.blit(GUI, i + nextStep.x, j + nextStep.y, nextStep.texX, nextStep.texY, nextStep.width, nextStep.height);

        this.menu.getTE().getTank().ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty())
                            {
                                guiGraphics.pose().pushPose();
                                FluidStack fluid = tank.getFluid();
                                renderer.render(guiGraphics.pose(), leftPos + 21, topPos + 15, fluid);
                            }
                        }
                );
    }

    private enum MortarToolTypes
    {
        NONE(0, 0, 0, 0, 0, 0),
        HANDS(65, 8, 176, 0, 11, 13),
        PESTLE(82, 7, 187, 0, 14, 14),
        OTSUCHI(99, 7, 201, 0, 15, 13);

        final int x;
        final int y;
        final int texX;
        final int texY;
        final int width;
        final int height;

        MortarToolTypes(int xIn, int yIn, int texXIn, int texYIn, int widthIn, int heightIn)
        {
            this.x = xIn;
            this.y = yIn;
            this.texX = texXIn;
            this.texY = texYIn;
            this.width = widthIn;
            this.height = heightIn;
        }
    }
}
