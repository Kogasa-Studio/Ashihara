package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.CandleBlock;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.models.CandleModel;
import kogasastudio.ashihara.client.models.baked.BakedModels;
import kogasastudio.ashihara.client.models.baked.UnbakedModelPart;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.LayerRegistryHandler;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.SignalGetter.DIRECTIONS;

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
    public void renderStatic(SectionRenderContext context)
    {
        BlockEntity be = context.blockEntity();
        if (!(be instanceof CandleTE tileEntityIn)) return;
        BlockPos pos = context.pos();

        //RandomSource pRandom = tileEntityIn.getLevel() == null ? Ashihara.getRandom() : be.getLevel().getRandom();
        //BlockState pState = tileEntityIn.getBlockState();
        //long pSeed = pState.getSeed(pos);
        PoseStack matrixStackIn = context.poseStack();

        int combinedLightIn = getPackedLight(be);

        for (double[] d : tileEntityIn.getPosList())
        {
            double x = d[0];
            double z = d[1];
            double y = d[2];

            matrixStackIn.pushPose();
            resetToBlock000(be, AshiharaRenderTypes.CANDLE, matrixStackIn);
            //GlStateManager._enableBlend();
            matrixStackIn.translate(x, y + 1.5d, z);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
            VertexConsumer consumer = context.consumerFunction() == null ? createMultiBufferSource(context, AshiharaRenderTypes.CANDLE).getBuffer(AshiharaRenderTypes.CANDLE) : context.consumerFunction().apply(AshiharaRenderTypes.CANDLE);//createMultiBufferSource(RenderType.solid()).getBuffer(RenderType.solid());
            //BakedModel model = BakedModels.bake(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "builtin"), "candle"), new UnbakedModelPart(candleSingle.candle, new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "block/candle_java"))));
            /*if (ModList.get().isLoaded("sodium"))
            {
            }*/
            //TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "block/candle_java"));
            //consumer = sprite.wrap(consumer);

            /*QuadLighter lighter = context.getQuadLighter(true);
            lighter.setup(context.getRegion(), pos, pState);
            for (Direction direction : DIRECTIONS)
            {
                pRandom.setSeed(pSeed);
                List<BakedQuad> list = model.getQuads(pState, direction, pRandom, ModelData.EMPTY, RenderType.solid());
                if (!list.isEmpty())
                {
                    list.forEach(bakedQuad -> lighter.process(consumer, matrixStackIn.last(), bakedQuad, OverlayTexture.NO_OVERLAY));
                }
            }*/

            //Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(context.getRegion(), model, be.getBlockState(), pos, matrixStackIn, consumer, true, be.getLevel().random, be.getBlockState().getSeed(pos), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
            //BakedModels.render(matrixStackIn.last(), consumer, model, RenderType.solid(), be.getBlockState(), combinedLightIn);
            //LightPipelineAwareModelBlockRenderer.render(consumer, lighter, context.getRegion(), model, pState, pos, matrixStackIn, true, pRandom, pSeed, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
            candleSingle.renderToBuffer(matrixStackIn, consumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFF);
            matrixStackIn.popPose();
        }
    }
}
