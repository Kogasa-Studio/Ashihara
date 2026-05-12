package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.block.blockentity.CandleBE;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.model.data.ModelData;

public class CandleTER implements BlockEntityRenderer<CandleBE, BlockEntityRenderState>, WithLevelRenderer<CandleBE>
{
    //public static final CandleModel candleSingle = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(LayerRegistryHandler.CANDLE));
    public CandleTER(BlockEntityRendererProvider.Context dispatcherIn)
    {
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof CandleBE tileEntityIn)) return;
        PoseStack matrixStackIn = context.poseStack();

        //int combinedLightIn = getPackedLight(be);

        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(Blocks.CANDLE.get().defaultBlockState());
        for (double[] d : tileEntityIn.getPosList())
        {
            double x = d[0];
            double z = d[1];
            double y = d[2];

            matrixStackIn.pushPose();
            //resetToBlock000(be, AshiharaRenderTypes.CHUNK_ENTITY_SOLID, matrixStackIn);
            //GlStateManager._enableBlend();
            matrixStackIn.translate(-0.5, 0, -0.5);
            matrixStackIn.translate(x, y/* + 1.5d*/, z);
            //matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
            //VertexConsumer consumer = context.consumerFunction().apply(AshiharaRenderTypes.CHUNK_ENTITY_SOLID);
            //TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "block/candle_java"));
            //consumer = sprite.wrap(consumer);
            renderer.renderBlockStateModel(model, matrixStackIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
            //candleSingle.renderToBuffer(matrixStackIn, consumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFF);
            matrixStackIn.popPose();
        }
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {

    }
}
