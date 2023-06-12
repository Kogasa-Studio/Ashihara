package kogasastudio.ashihara.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public interface IHasCustomModel
{
    Model getModel();

    ResourceLocation getTex();

    default void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, int stackSize)
    {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entitySolid(this.getTex()));
        this.getModel().renderToBuffer(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }

    default float getScale()
    {
        return 1.0f;
    }

    default int getModelStackSize()
    {
        return 4;
    }

    default float[] getTranslation(int stackSize)
    {
        return new float[]{8.0f, 0.0f, 8.0f};
    }

    default float getXOffset(int stackSize)
    {
        return 0.0f;
    }

    default float getYOffset(int stackSize)
    {
        return stackSize == 0 ? 0.0f : 1.2f;
    }

    default float getZOffset(int stackSize)
    {
        return 0.0f;
    }
}
