package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.client.models.CandleModel;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.LayerRegistryHandler;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CandleTER implements BlockEntityRenderer<CandleTE>, WithLevelRenderer<CandleTE>
{
    public static final CandleModel candleSingle = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(LayerRegistryHandler.CANDLE));
    public CandleTER(BlockEntityRendererProvider.Context dispatcherIn)
    {
    }

    @Override
    public void render(CandleTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof CandleTE tileEntityIn)) return;
        PoseStack matrixStackIn = context.poseStack();

        int combinedLightIn = getPackedLight(be);

        for (double[] d : tileEntityIn.getPosList())
        {
            double x = d[0];
            double z = d[1];
            double y = d[2];

            matrixStackIn.pushPose();
            resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_SOLID, matrixStackIn);
            //GlStateManager._enableBlend();
            matrixStackIn.translate(x, y + 1.5d, z);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
            VertexConsumer consumer = context.consumerFunction().apply(AshiharaRenderTypes.CHUNK_ENTITY_SOLID);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "block/candle_java"));
            consumer = sprite.wrap(consumer);
            candleSingle.renderToBuffer(matrixStackIn, consumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFF);
            matrixStackIn.popPose();
        }
    }
}
