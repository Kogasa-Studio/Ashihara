package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.math.Vector3f;

import static kogasastudio.ashihara.block.BlockLantern.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;
import static kogasastudio.ashihara.helper.BlockActionHelper.getRotationByFacing;

public class MarkableLanternTER extends BlockEntityRenderer<MarkableLanternTE>
{
    public MarkableLanternTER(BlockEntityRenderDispatcher dispatcher){super(dispatcher);}

    private static final RenderType ICONS = RenderType.entityTranslucent(AshiharaAtlas.ICON_ATLAS);

    @Override
    public void render(MarkableLanternTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        //获取IVertexBuilder
        VertexConsumer builder = bufferIn.getBuffer(ICONS);
        //通过小纹理的源文件获取该小纹理在Atlas上的位置
        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(AshiharaAtlas.ICON_ATLAS).apply(tileEntityIn.getIcon());

        //主渲染
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING))));
        Matrix4f wtf = matrixStackIn.last().pose();
        buildMatrix(wtf, builder, 0.25f, 0.25f,-0.2505f, icon.getU0(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, 0.25f,-0.2505f, icon.getU1(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, -0.25f,-0.2505f, icon.getU1(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, 0.25f, -0.25f,-0.2505f, icon.getU0(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        matrixStackIn.popPose();

        //渲染背面图标
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING)) + 180));
        Matrix4f wth = matrixStackIn.last().pose();
        buildMatrix(wth, builder, 0.25f, 0.25f,-0.2505f, icon.getU0(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, 0.25f,-0.2505f, icon.getU1(), icon.getV0(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, -0.25f,-0.2505f, icon.getU1(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, 0.25f, -0.25f,-0.2505f, icon.getU0(), icon.getV1(), combinedOverlayIn, combinedLightIn);
        matrixStackIn.popPose();
    }
}
