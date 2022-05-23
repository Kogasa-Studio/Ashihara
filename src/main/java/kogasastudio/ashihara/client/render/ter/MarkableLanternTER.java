package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import static kogasastudio.ashihara.block.BlockLantern.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;
import static kogasastudio.ashihara.helper.BlockActionHelper.getRotationByFacing;

public class MarkableLanternTER extends TileEntityRenderer<MarkableLanternTE>
{
    public MarkableLanternTER(TileEntityRendererDispatcher dispatcher){super(dispatcher);}

    private static final RenderType ICONS = RenderType.entityTranslucent(AshiharaAtlas.ICON_ATLAS);

    @Override
    public void render(MarkableLanternTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        //获取IVertexBuilder
        IVertexBuilder builder = bufferIn.getBuffer(ICONS);
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
