package kogasastudio.ashihara.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.item.GuideBook;
import kogasastudio.ashihara.loading.ReloadableResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class PageRenderer extends GeoObjectRenderer<GuideBookModel>
{
    public PageRenderer(GeoModel<GuideBookModel> model)
    {
        super(model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, GuideBookModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        if (bone.isTrackingMatrices())
        {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
        }

        poseStack.pushPose();
        RenderUtil.prepMatrixForBone(poseStack, bone);
        buffer = this.checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
        this.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
        if (bone.getName().equals("rightcover"))
        {
            poseStack.pushPose();
            poseStack.scale(1f / 64f, 1f / 64f, 1f / 64f);
            poseStack.translate(75,12.1,-55);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            //Minecraft.getInstance().font.drawInBatch("小猫崽子小猫崽子小猫崽子", 0, 0, 0x943943, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
            GuideBook.Page page = ReloadableResources.getGuidebookPagesReordered().get(0);
            renderFormattedText(poseStack, page.getTextFields()[0].text(), 0, 0, 0x943943, true, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
            poseStack.popPose();
        }
        //setBoneRotX(bone, "spine", 60);
        //setBoneRotX(bone, "right", 120);
        if (!isReRender) {
            this.applyRenderLayersForBone(poseStack, this.getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }

        this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

    protected void renderFormattedText(PoseStack poseStack, String text, int x, int y, int color, boolean dropShadow, MultiBufferSource bufferSource, Font.DisplayMode mode, int backgroundColor, int packedLight)
    {
        Font font = Minecraft.getInstance().font;
        StringSplitter splitter = font.getSplitter();
        List<String> list = new ArrayList<>();
        splitter.splitLines(text, 96, Style.EMPTY, true, ((style, currentPos, contentWidth) ->
        {
            String lineText = text.substring(currentPos, contentWidth);
            lineText = StringUtils.stripEnd(lineText, "\n");
            list.add(lineText);
        }));
        for (int i = 0; i < list.size(); i++)
        {
            String line = list.get(i);
            poseStack.pushPose();
            font.drawInBatch(line, x, y + i * 9, color, dropShadow, poseStack.last().pose(), bufferSource, mode, backgroundColor, packedLight);
            poseStack.popPose();
        }
    }

    protected void setBoneRotIfNameMatches(GeoBone bone, String name, float x, float y, float z)
    {
        if (bone.getName().equals(name))
        {
            bone.setRotX((float) Math.toRadians(x));
            bone.setRotY((float) Math.toRadians(y));
            bone.setRotZ((float) Math.toRadians(z));
        }
    }

    protected void setBoneRotX(GeoBone bone, String name, float x)
    {
        if (bone.getName().equals(name))
        {
            bone.setRotX((float) Math.toRadians(x));
        }
    }
}
