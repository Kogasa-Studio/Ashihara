package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kogasastudio.ashihara.block.tileentities.MillTE;
import kogasastudio.ashihara.client.models.MillStoneModel;
import kogasastudio.ashihara.client.render.LayerRegistryHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static kogasastudio.ashihara.block.BlockMill.FACING;

public class MillTER implements BlockEntityRenderer<MillTE> {
    private static final ResourceLocation tex = new ResourceLocation("ashihara:textures/block/mill_stone.png");
    private final MillStoneModel millStone;
    public MillTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
        this.millStone = new MillStoneModel(rendererDispatcherIn.bakeLayer(LayerRegistryHandler.MILL_STONE));
    }

    @Override
    public void render(MillTE tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        float facing = tileEntityIn.getBlockState().getValue(FACING).toYRot();
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(facing));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.getMillStoneRotation()));
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entitySolid(tex));
        millStone.renderToBuffer(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }
}
