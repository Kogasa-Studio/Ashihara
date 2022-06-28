package kogasastudio.ashihara.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public interface IHasCustomModel
{
    Model getModel();

    ResourceLocation getTex();

    default void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, int stackSize)
    {
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getEntitySolid(this.getTex()));
        this.getModel().render(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
    }

    default float getScale() {return 1.0f;}

    default int getModelStackSize() {return 4;}

    default float[] getTranslation(int stackSize) {return new float[]{8.0f, 0.0f, 8.0f};}

    default float getXOffset(int stackSize) {return 0.0f;}

    default float getYOffset(int stackSize) {return stackSize == 0 ? 0.0f : 1.2f;}

    default float getZOffset(int stackSize) {return 0.0f;}
}
