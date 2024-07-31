package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class MillScreen //extends AbstractContainerScreen<MillContainer>
{/*
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/mill.png");
    private int progress;
    private final FluidStackRenderer inputRenderer = new FluidStackRenderer(4000, 16, 64);
    private final FluidStackRenderer outputRenderer = new FluidStackRenderer(4000, 64, 6);

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
        renderBackground(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
        super.render(gui, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int x, int y)
    {
        super.renderTooltip(gui, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        FluidTank tankIn = this.menu.getBe().getTank().orElse(new FluidTank(0));
        FluidTank tankOut = this.menu.getBe().tankOut.orElse(new FluidTank(0));

        RenderHelper.drawFluidToolTip(this, gui, x, y, i + 17, j + 13, 16, 64, tankIn.getFluid(), tankIn.getCapacity());
        RenderHelper.drawFluidToolTip(this, gui, x, y, i + 54, j + 99, 64, 6, tankOut.getFluid(), tankOut.getCapacity());
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
                                FluidStack fluid = tank.getFluid();
                                inputRenderer.render(gui.pose(), leftPos + 17, topPos + 13, fluid);
                            }
                        }
                );

        this.menu.getBe().tankOut.ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty())
                            {
                                FluidStack fluid = tank.getFluid();
                                outputRenderer.render(gui.pose(), i + 54, j + 99, fluid);
                            }
                        }
                );
    }*/
}
