package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class MillScreen extends ContainerScreen<MillContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("ashihara:textures/gui/mill.png");
    private int progress;

    public MillScreen(MillContainer container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
    }

    @Override
    public void tick() {super.tick();progress = container.getArrowWidth();}

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        renderBackground(matrixStack);

        if (this.minecraft == null) return;

        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(matrixStack, i, j, 0, 0, 176, 202, 256, 256);
        blit(matrixStack, i + 91, j + 59, 176, 0, progress, 12);

        this.container.getTE().getTank().ifPresent
        (
            tank ->
            {
                if (!tank.isEmpty())
                {
                    int capacity = tank.getCapacity();
                    FluidStack fluid = tank.getFluid();
                    int fluidAmount = fluid.getAmount();
                    int displayHeight = (fluidAmount / capacity) * 64;

                    Tessellator tessellator = Tessellator.getInstance();
                    RenderHelper.renderFluidStackInGUI(fluid, 16, displayHeight, 16f, 13f, tessellator);
                }
            }
        );
    }
}
