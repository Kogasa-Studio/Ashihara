package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.network.GuidebookProgressPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class GuideBookScreen extends Screen
{
    private final GuideBookModel book = new GuideBookModel();
    private final Player player;
    private final RenderType renderType = RenderType.ENTITY_CUTOUT.apply(book.getTextureResource(book));
    private int ticks = 0;

    public GuideBookScreen(Component title, Player player)
    {
        super(title);
        this.player = player;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void tick()
    {
        ticks += 1;
        super.tick();
    }

    @Override
    protected void init()
    {
        book.triggerAnim(player, book.hashCode(), "Intro", "intro");
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        book.triggerAnim(player, book.hashCode(), "Open", "open");
        if (mouseX > 100 && mouseY > 100)
        {
            PacketDistributor.sendToServer(new GuidebookProgressPacket(0, 0));
            this.onClose();
        }
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate((float) (this.width / 2d - 72.5), (float) (this.height - 40), 0);
        pose.mulPose(Axis.YP.rotationDegrees(90));
        pose.scale(64, -64, 64);
        book.RENDERER.render(guiGraphics.pose(), book, guiGraphics.bufferSource(), renderType, guiGraphics.bufferSource().getBuffer(renderType), 15728880, partialTick);
        pose.popPose();

        pose.pushPose();
        guiGraphics.drawString(Minecraft.getInstance().font, "X: " + mouseX + ", Y: " + mouseY, 0, 0, 0xffffff);
        pose.popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        //this.renderBlurredBackground(partialTick);
    }
}
