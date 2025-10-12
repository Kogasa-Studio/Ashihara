package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.client.gui3d.Screen3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class PotScreen extends Screen3D
{
    public PotScreen()
    {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack pose = guiGraphics.pose();
        VertexConsumer bufferBuilder = guiGraphics.bufferSource().getBuffer(RenderType.lines());
        pose.pushPose();
        guiGraphics.drawString(Minecraft.getInstance().font, "MouseX: "+mouseX()+"; MouseY: "+mouseY(), 0, 0, 0xffffff);
        pose.pushPose();
        bufferBuilder.addVertex(pose.last().pose(), 0, (float) mouseY(), 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), 0, (float) mouseY(), 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), this.width, (float) mouseY(), 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), this.width, (float) mouseY(), 0).setColor(0xcff3fc);
        pose.popPose();

        pose.pushPose();
        bufferBuilder.addVertex(pose.last().pose(), (float) mouseX(), 0, 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), (float) mouseX(), 0, 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), (float) mouseX(), this.height, 0).setColor(0xcff3fc);
        bufferBuilder.addVertex(pose.last().pose(), (float) mouseX(), this.height, 0).setColor(0xcff3fc);
        pose.popPose();
        pose.popPose();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
