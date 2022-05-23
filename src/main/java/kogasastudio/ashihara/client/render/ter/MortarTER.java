package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.utils.AshiharaTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Map;

import static kogasastudio.ashihara.block.BlockMortar.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.XTP;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MortarTER implements BlockEntityRenderer<MortarTE> {
    private static final ArrayList<ResourceLocation> textures = new ArrayList<>
            (Minecraft.getInstance().getResourceManager().listResources("textures/assistants/", s -> s.endsWith(".png")));
    private static final Map<String, ResourceLocation> cookedTextures = RenderHelper.cookTextureRLsToMap(textures);
    private static final String CEREALS = "cereals_level";
    private static final String PROCESSED = "processed_level";
    public MortarTER(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(MortarTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RenderHelper.renderLeveledFluidStack
                (
                        tileEntityIn, matrixStackIn, bufferIn,
                        combinedLightIn, combinedOverlayIn,
                        XTP(3.5f), XTP(4.0f), XTP(3.5f),
                        XTP(12.5f), XTP(12.0f), XTP(12.5f),
                        tileEntityIn.getLevel(), tileEntityIn.getBlockPos()
                );

        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < tileEntityIn.contents.getSlots(); i += 1) {
            list.add(i, tileEntityIn.contents.getStackInSlot(i));
        }

        float renderHeight = XTP(4.0f);
        for (ItemStack stack : list) {
            if (!stack.isEmpty()) {
                renderHeight += XTP(2.0f);
                //以Quad方式渲染谷物或产物
                if (stack.is(AshiharaTags.CEREALS) || stack.is(AshiharaTags.CEREAL_PROCESSED)) {
                    String key = stack.is(AshiharaTags.CEREAL_PROCESSED) ? PROCESSED : CEREALS;

                    RenderType ASSISTANCE = RenderType.entityCutout(AshiharaAtlas.ASSISTANCE_ATLAS);
                    VertexConsumer builder = bufferIn.getBuffer(ASSISTANCE);
                    TextureAtlasSprite level = Minecraft.getInstance().getTextureAtlas(AshiharaAtlas.ASSISTANCE_ATLAS).apply(cookedTextures.get(key));

                    float u0 = level.getU0();
                    float u1 = level.getU1();
                    float v0 = level.getV0();
                    float v1 = level.getV1();

                    //主渲染
                    matrixStackIn.pushPose();
                    matrixStackIn.translate(0.0f, renderHeight, 0.0f);
                    Matrix4f wtf = matrixStackIn.last().pose();
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(3.5f), u0, v0, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(12.5f), u0, v1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(12.5f), u1, v1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(3.5f), u1, v0, combinedOverlayIn, combinedLightIn);
                } else {
                    matrixStackIn.pushPose();
                    matrixStackIn.translate(XTP(8.0f), renderHeight, XTP(8.0f));
                    matrixStackIn.scale(0.6f, 0.6f, 0.6f);
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0f));
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.getBlockState().getValue(FACING).toYRot()));

                    ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                    renderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
                }
                matrixStackIn.popPose();
            }
        }
    }
}
