package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;

import static kogasastudio.ashihara.block.LanternBlock.FACING;
import static kogasastudio.ashihara.helper.BlockActionHelper.getRotationByFacing;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MarkableLanternTER implements BlockEntityRenderer<MarkableLanternTE>
{
    private static final RenderType ICONS = RenderType.entityTranslucent(AshiharaAtlas.ICON_ATLAS);

    public MarkableLanternTER(BlockEntityRendererProvider.Context dispatcher)
    {
    }

    @Override
    public void render(MarkableLanternTE tileEntityIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        //获取IVertexBuilder
        VertexConsumer builder = bufferIn.getBuffer(ICONS);
        //通过小纹理的源文件获取该小纹理在Atlas上的位置
        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(tileEntityIn.getIcon());

        //主渲染
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING))));
        Matrix4f wtf = poseStackIn.last().pose();
        buildMatrix(wtf, builder, 0.25f, 0.25f, -0.2505f, icon.getU0(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, 0.25f, -0.2505f, icon.getU1(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, -0.25f, -0.2505f, icon.getU1(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, 0.25f, -0.25f, -0.2505f, icon.getU0(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();

        //渲染背面图标
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING)) + 180));
        Matrix4f wth = poseStackIn.last().pose();
        buildMatrix(wth, builder, 0.25f, 0.25f, -0.2505f, icon.getU0(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, 0.25f, -0.2505f, icon.getU1(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, -0.25f, -0.2505f, icon.getU1(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, 0.25f, -0.25f, -0.2505f, icon.getU0(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();
    }
}
