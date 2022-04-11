package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.utils.AshiharaTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Map;

import static kogasastudio.ashihara.block.BlockMortar.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.XTP;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MortarTER extends TileEntityRenderer<MortarTE>
{
    public MortarTER(TileEntityRendererDispatcher rendererDispatcherIn) {super(rendererDispatcherIn);}

    private static final ArrayList<ResourceLocation> textures = new ArrayList<>
            (Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/assistants/", s -> s.endsWith(".png")));
    private static final Map<String, ResourceLocation> cookedTextures = RenderHelper.cookTextureRLsToMap(textures);

    private static final String CEREALS = "cereals_level";
    private static final String PROCESSED = "processed_level";

    @Override
    public void render(MortarTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderHelper.renderLeveledFluidStack
        (
            tileEntityIn, matrixStackIn, bufferIn,
            combinedLightIn, combinedOverlayIn,
            XTP(3.5f), XTP(4.0f), XTP(3.5f),
            XTP(12.5f), XTP(10.0f), XTP(12.5f),
            tileEntityIn.getWorld(), tileEntityIn.getPos()
        );

        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < tileEntityIn.contents.getSlots(); i += 1)
        {
            list.add(i, tileEntityIn.contents.getStackInSlot(i));
        }

        float renderHeight = XTP(3.5f);
        for (ItemStack stack : list)
        {
            if (!stack.isEmpty())
            {
                renderHeight += XTP(2.25f);
                //以Quad方式渲染谷物或产物
                if (stack.getItem().isIn(AshiharaTags.CEREALS) || stack.getItem().isIn(AshiharaTags.CEREAL_PROCESSED))
                {
                    String key = stack.getItem().isIn(AshiharaTags.CEREAL_PROCESSED) ? PROCESSED : CEREALS;

                    RenderType ASSISTANCE = RenderType.getEntityCutout(AshiharaAtlas.ASSISTANCE_ATLAS);
                    IVertexBuilder builder = bufferIn.getBuffer(ASSISTANCE);
                    TextureAtlasSprite level = Minecraft.getInstance().getAtlasSpriteGetter(AshiharaAtlas.ASSISTANCE_ATLAS).apply(cookedTextures.get(key));

                    float u0 = level.getMinU(); float u1 = level.getMaxU();
                    float v0 = level.getMinV(); float v1 = level.getMaxV();

                    //主渲染
                    matrixStackIn.push();
                    matrixStackIn.translate(0.0f, renderHeight, 0.0f);
                    Matrix4f wtf = matrixStackIn.getLast().getMatrix();
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(3.5f), u0, v0, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(12.5f), u0, v1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(12.5f), u1, v1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(3.5f), u1, v0, combinedOverlayIn, combinedLightIn);
                }
                else
                {
                    boolean isXAxis = tileEntityIn.getBlockState().get(FACING).getAxis().equals(Direction.Axis.X);

                    matrixStackIn.push();
                    matrixStackIn.translate(XTP(8.0f), renderHeight, XTP(8.0f));
                    matrixStackIn.scale(0.6f, 0.6f, 0.6f);
                    matrixStackIn.rotate(isXAxis ? Vector3f.ZP.rotationDegrees(90.0f) : Vector3f.XP.rotationDegrees(90.0f));
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(tileEntityIn.getBlockState().get(FACING).getHorizontalAngle()));

                    ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                    renderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                }
                matrixStackIn.pop();
            }
        }
    }
}
