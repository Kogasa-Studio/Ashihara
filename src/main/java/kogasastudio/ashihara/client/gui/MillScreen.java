package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MillScreen extends AbstractContainerScreen<MillContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("ashihara:textures/gui/mill.png");
    private int progress;

    public MillScreen(MillContainer container, Inventory inv, Component title)
    {
        super(container, inv, title);
    }

    @Override
    protected void containerTick()
    {
        progress = menu.getArrowWidth();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int x, int y)
    {
        super.renderTooltip(gui, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        FluidTank tankIn = this.menu.getBe().getTank().orElse(new FluidTank(0));
        FluidTank tankOut = this.menu.getBe().tankOut.orElse(new FluidTank(0));

        RenderHelper.drawFluidToolTip
                (this, gui, x, y, i + 17, j + 13, 16, 64, tankIn.getFluid(), tankIn.getCapacity());
        RenderHelper.drawFluidToolTip
                (this, gui, x, y, i + 54, j + 99, 64, 6, tankOut.getFluid(), tankOut.getCapacity());
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int x, int y)
    {
        if (this.minecraft == null) return;

        RenderSystem.setShaderTexture(0, GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        gui.blit(GUI, i, j, 0, 0, 176, 202, 256, 256);
        gui.blit(GUI, i + 91, j + 59, 176, 0, progress, 12);

        this.menu.getBe().getTank().ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty())
                            {
                                gui.pose().pushPose();
                                int capacity = tank.getCapacity();
                                FluidStack fluid = tank.getFluid();
                                int fluidAmount = fluid.getAmount();
                                int displayHeight = (int) (((float) fluidAmount / (float) capacity) * 64);
                                RenderHelper.renderFluidStackInGUI(gui.pose().last().pose(), fluid, 16, displayHeight, i + 17, j + 77);
                                gui.pose().popPose();
                            }
                        }
                );

        this.menu.getBe().tankOut.ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty())
                            {
                                gui.pose().pushPose();
                                int capacity = tank.getCapacity();
                                FluidStack fluid = tank.getFluid();
                                int fluidAmount = fluid.getAmount();
                                int displayWidth = (int) (((float) fluidAmount / (float) capacity) * 64);
                                RenderHelper.renderFluidStackInGUI(gui.pose().last().pose(), fluid, displayWidth, 6, i + 54, j + 105);
                                gui.pose().popPose();
                            }
                        }
                );
    }
}
