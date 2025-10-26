package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class PotScreen extends Screen3D
{
    public PotScreen()
    {
        super(Component.empty());
    }

    private final SimpleInternalControlGeoModel test_block = new SimpleInternalControlGeoModel("geo/block/pot.geo.json", "textures/block/pot.png", Minecraft.getInstance().player);

    public final BoneTracer test_tracer = new BoneTracer(b -> b.getName().equals("movable"));

    @Override
    protected void init()
    {
        super.init();
        test_block.getRendererPoseSync().ashihara_1_21$addTracer(this.test_tracer);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack pose = guiGraphics.pose();
        VertexConsumer bufferBuilder = guiGraphics.bufferSource().getBuffer(RenderType.lines());
        pose.pushPose();
        bufferBuilder.addVertex(pose.last(), 0, (float) mouseY(), 0).setNormal(pose.last(), 1, 0, 0).setColor(0xffffffff);
        bufferBuilder.addVertex(pose.last(), this.width, (float) mouseY(), 0).setNormal(pose.last(), 1, 0, 0).setColor(0xffffffff);

        bufferBuilder.addVertex(pose.last(), (float) mouseX(), 0, 0).setNormal(pose.last(), 0, 1, 0).setColor(0xffffffff);
        bufferBuilder.addVertex(pose.last(), (float) mouseX(), this.height, 0).setNormal(pose.last(), 0, 1, 0).setColor(0xffffffff);

        guiGraphics.drawString(Minecraft.getInstance().font, "MouseX: "+mouseX()+"; MouseY: "+mouseY(), 0, 0, 0xffffff);
        pose.popPose();

        pose.pushPose();
        pose.translate(this.width / 2f, this.height / 2f, 19);
        pose.scale(64f, 64f, 64f);
        pose.mulPose(Axis.XP.rotationDegrees(45));
        pose.mulPose(Axis.YP.rotation((float) (RenderUtil.getCurrentTick() % (Math.PI * 22)) / 11));
        test_block.render(pose, guiGraphics.bufferSource(), 15728880, 0);
        pose.popPose();

        pose.pushPose();
        pose.mulPose(test_tracer.matrix());
        RenderHelper.INDICATOR.render(pose, guiGraphics.bufferSource(), 15728880, 0);
        pose.popPose();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
