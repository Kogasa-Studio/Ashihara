package kogasastudio.ashihara.client.gui.widget;

import kogasastudio.ashihara.client.models.geo.TestButtonModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class TestButton extends SpaceFixedWidget3D
{
    private final TestButtonModel testButton = new TestButtonModel();
    public final RenderType renderType = RenderType.entityTranslucent(testButton.getTextureResource(testButton));

    public TestButton(int x, int y, int sizeX, int sizeY)
    {
        super(x, y, sizeX, sizeY, Component.empty());
    }

    public void updateParent()
    {
        if (this.parent == null)
        {
            this.parent = testButton.getBone("main").orElse(null);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        testButton.RENDERER.render(guiGraphics.pose(), testButton, guiGraphics.bufferSource(), renderType, guiGraphics.bufferSource().getBuffer(renderType), 15728880, partialTick);
    }

    public void actualRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
