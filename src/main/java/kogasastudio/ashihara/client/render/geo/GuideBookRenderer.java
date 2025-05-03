package kogasastudio.ashihara.client.render.geo;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.helper.FontHelper;
import kogasastudio.ashihara.item.GuideBook;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

import static kogasastudio.ashihara.helper.FontHelper.*;

public class GuideBookRenderer extends GeoObjectRenderer<GuideBookModel>
{
    public GuideBookRenderer(GeoModel<GuideBookModel> model)
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

        GuideBookModel book = (GuideBookModel) this.model;
        GuideBookModel.DoubleSidedPage doubleSidedPage = book.getDoubleSidedPage(bone.getName());

        if (doubleSidedPage != null)
        {
            if (doubleSidedPage.right() != null)
            {
                poseStack.pushPose();
                poseStack.scale(1f / 64f, 1f / 64f, 1f / 64f);
                poseStack.translate(80,0, -60);
                float yOffset = bone.getName().equals("rightcover") || bone.getName().equals("part_right") ? 12.1f : bone.getName().equals("leftcover") ? 0.1f : 6.1f;
                for (GuideBook.Page.TextField textField : doubleSidedPage.right().getTextFields())
                {
                    poseStack.pushPose();
                    poseStack.translate(-textField.x(),yOffset, textField.y());
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    List<String> list = FontHelper.processText(textField.text(), textField.widthInFullWidthChar() * 9);
                    if (textField.isTextColumned()) list = FontHelper.transformToColumn(list);
                    poseStack.scale(4f/9f, 4f/9f, 4f/9f);
                    poseStack.scale(textField.charSize(), textField.charSize(), textField.charSize());
                    renderFormattedText(poseStack, list, 0, 0, textField.textColor(), false, textField.isTextColumned(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
            if (doubleSidedPage.left() != null)
            {
                poseStack.pushPose();
                poseStack.scale(1f / 64f, -1f / 64f, 1f / 64f);
                poseStack.translate(80,0, 27);
                float yOffset = bone.getName().equals("rightcover") || bone.getName().equals("part_right") ? -11.9f : bone.getName().equals("leftcover") ? -0.1f : -5.9f;
                for (GuideBook.Page.TextField textField : doubleSidedPage.left().getTextFields())
                {
                    poseStack.pushPose();
                    poseStack.translate(-textField.x(),yOffset, textField.y());
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    List<String> list = FontHelper.processText(textField.text(), textField.widthInFullWidthChar() * 9);
                    if (textField.isTextColumned()) list = FontHelper.transformToColumn(list);
                    poseStack.scale(4f/9f, 4f/9f, 4f/9f);
                    poseStack.scale(textField.charSize(), textField.charSize(), textField.charSize());
                    renderFormattedText(poseStack, list, 0, 0, textField.textColor(), false, textField.isTextColumned(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
        }

        if (!isReRender)
        {
            this.applyRenderLayersForBone(poseStack, this.getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }

        this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }
}
