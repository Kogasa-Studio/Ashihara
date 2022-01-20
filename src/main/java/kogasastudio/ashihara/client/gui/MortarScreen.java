package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import kogasastudio.ashihara.inventory.container.MortarContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class MortarScreen extends ContainerScreen<MortarContainer>
{
    private enum MortarToolTypes
    {
        NONE(0, 0, 0, 0, 0, 0),
        HANDS(65, 8, 176, 0, 11, 13),
        PESTLE(82, 7, 187, 0, 14, 14),
        OTSUCHI(99, 7, 201, 0, 15, 13);

        int x;
        int y;
        int texX;
        int texY;
        int width;
        int height;

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

    private MortarToolTypes getNextStep(int stateCode)
    {
        switch (stateCode)
        {
            case 0 : return MortarToolTypes.HANDS;
            case 1 : return MortarToolTypes.PESTLE;
            case 2 : return MortarToolTypes.OTSUCHI;
            default: return MortarToolTypes.NONE;
        }
    }

    private static final ResourceLocation GUI = new ResourceLocation("ashihara:textures/gui/mortar.png");
    private int progress;
    private int stepStateCode;

    public MortarScreen(MortarContainer container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
        this.progress = container.getArrowHeight();
        this.stepStateCode = container.getNextStep();

    }

    @Override
    public void tick()
    {
        super.tick();
        int progressIn = this.container.getArrowHeight();
        int stepStateCodeIn = this.container.getNextStep();
        if (progressIn != this.progress) {LOGGER_MAIN.info("progress: " + container.getArrowHeight());this.progress = progressIn;}
        if (stepStateCodeIn != this.stepStateCode) {LOGGER_MAIN.info("nextStep: " + container.getNextStep());this.stepStateCode = stepStateCodeIn;}
    }

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
        this.minecraft.getTextureManager().bindTexture(GUI);
        MortarToolTypes nextStep = this.getNextStep(this.stepStateCode);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(matrixStack, i, j, 0, 0, 176, 202, 256, 256);
        blit(matrixStack, i + 126, j + 23, 176, 23, 15, progress);
        blit(matrixStack, i + nextStep.x, j + nextStep.y, nextStep.texX, nextStep.texY, nextStep.width, nextStep.height);
    }
}
