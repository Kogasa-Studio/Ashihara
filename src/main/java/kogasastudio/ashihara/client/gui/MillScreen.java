package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MillScreen extends AbstractContainerScreen<MillContainer> {
    private static final ResourceLocation GUI = new ResourceLocation("ashihara:textures/gui/mill.png");
    private int progress;

    public MillScreen(MillContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void containerTick() {
        progress = menu.getArrowWidth();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        FluidTank tankIn = this.menu.getBe().getTank().orElse(new FluidTank(0));
        FluidTank tankOut = this.menu.getBe().tankOut.orElse(new FluidTank(0));

        RenderHelper.drawFluidToolTip
                (this, matrixStack, x, y, i + 17, j + 13, 16, 64, tankIn.getFluid(), tankIn.getCapacity());
        RenderHelper.drawFluidToolTip
                (this, matrixStack, x, y, i + 54, j + 99, 64, 6, tankOut.getFluid(), tankOut.getCapacity());
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        renderBackground(matrixStack);

        if (this.minecraft == null) {
            return;
        }

        this.minecraft.getTextureManager().bindForSetup(GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        blit(matrixStack, i, j, 0, 0, 176, 202, 256, 256);
        blit(matrixStack, i + 91, j + 59, 176, 0, progress, 12);

        this.menu.getBe().getTank().ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty()) {
                                matrixStack.pushPose();
                                int capacity = tank.getCapacity();
                                FluidStack fluid = tank.getFluid();
                                int fluidAmount = fluid.getAmount();
                                int displayHeight = (int) (((float) fluidAmount / (float) capacity) * 64);
                                RenderHelper.renderFluidStackInGUI(matrixStack.last().pose(), fluid, 16, displayHeight, i + 17, j + 77);
                                matrixStack.popPose();
                            }
                        }
                );

        this.menu.getBe().tankOut.ifPresent
                (
                        tank ->
                        {
                            if (!tank.isEmpty()) {
                                matrixStack.pushPose();
                                int capacity = tank.getCapacity();
                                FluidStack fluid = tank.getFluid();
                                int fluidAmount = fluid.getAmount();
                                int displayWidth = (int) (((float) fluidAmount / (float) capacity) * 64);
                                RenderHelper.renderFluidStackInGUI(matrixStack.last().pose(), fluid, displayWidth, 6, i + 54, j + 105);
                                matrixStack.popPose();
                            }
                        }
                );
    }
}
