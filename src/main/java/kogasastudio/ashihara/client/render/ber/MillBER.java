package kogasastudio.ashihara.client.render.ber;

public class MillBER //implements BlockEntityRenderer<MillBE>
{
    /*private static final ResourceLocation tex = new ResourceLocation("ashihara:textures/block/mill_stone.png");
    private final MillStoneModel millStone;

    public MillBER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
        this.millStone = new MillStoneModel(rendererDispatcherIn.bakeLayer(LayerRegistryHandler.MILL_STONE));
    }

    @Override
    public void render(MillBE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        float facing = tileEntityIn.getBlockState().getValue(FACING).toYRot();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(facing));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.getMillStoneRotation()));
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entitySolid(tex));
        millStone.renderToBuffer(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }*/
}
