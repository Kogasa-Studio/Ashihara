package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;

import static kogasastudio.ashihara.block.HangingLanternLongBlock.FACING;
import static kogasastudio.ashihara.helper.BlockActionHelper.getRotationByFacing;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MarkableLanternTER implements BlockEntityRenderer<MarkableLanternTE>
{


    public MarkableLanternTER(BlockEntityRendererProvider.Context dispatcher)
    {
    }

    @Override
    public void render(MarkableLanternTE tileEntityIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderType icon = RenderType.entityTranslucent(tileEntityIn.getIcon());
        //获取IVertexBuilder
        VertexConsumer builder = bufferIn.getBuffer(icon);
        //通过小纹理的源文件获取该小纹理在Atlas上的位置

        //主渲染
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING))));
        Matrix4f wtf = poseStackIn.last().pose();/*icon.getU0(), icon.getV0()*/
        buildMatrix(wtf, builder, 0.25f, 0.25f, -0.2505f,0,0 , combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, 0.25f, -0.2505f, 1,0, combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, -0.25f, -0.2505f, 1,1, combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, 0.25f, -0.25f, -0.2505f,0,1, combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();

        //渲染背面图标
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING)) + 180));
        Matrix4f wth = poseStackIn.last().pose();
        buildMatrix(wth, builder, 0.25f, 0.25f, -0.2505f,0,0 , combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, 0.25f, -0.2505f, 1,0, combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, -0.25f, -0.2505f, 1,1, combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, 0.25f, -0.25f, -0.2505f,0,1, combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();
    }
}
