package kogasastudio.ashihara.client.render.ister;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.client.models.PailItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;

import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class PailISTER extends ItemStackTileEntityRenderer
{
    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        ResourceLocation PAIL = new ResourceLocation("ashihara:textures/block/pail_multiple.png");

        PailTE te = new PailTE();
        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null && !nbt.isEmpty()) te.read(BlockRegistryHandler.PAIL.get().getDefaultState(), nbt);

        PailItemModel model = new PailItemModel();

        IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucentNoCrumbling());
        IVertexBuilder builder1 = buffer.getBuffer(RenderType.getEntitySolid(PAIL));

        te.getTank().ifPresent
        (
            bucket ->
            {
                if (!bucket.isEmpty()) {
                    FluidStack fluid = bucket.getFluid();
                    TextureAtlasSprite FLUID =
                        Minecraft.getInstance()
                        .getBlockRendererDispatcher()
                        .getBlockModelShapes()
                        .getTexture(fluid.getFluid().getDefaultState().getBlockState());
                    int color = fluid.getFluid().getAttributes().getColor();
                    float height = ((float) fluid.getAmount() / bucket.getCapacity()) * 0.5f;

                    matrixStack.push();
                    GlStateManager.enableBlend();

                    matrixStack.translate(0.0f, 0.0f, 0.0f);
                    Matrix4f wtf = matrixStack.getLast().getMatrix();
                    //主渲染
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.25f, FLUID.getMinU(), FLUID.getMinV(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.75f, FLUID.getMinU(), FLUID.getMaxV(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.75f, FLUID.getMaxU(), FLUID.getMaxV(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.25f, FLUID.getMaxU(), FLUID.getMinV(), combinedOverlay, color, 1.0f, combinedLight);
//                    Minecraft.getInstance().getRenderManager().getFontRenderer().drawString(matrixStack, "FUCK YOU", 0.0f, 0.0f, 0xFFFFFF);
                    matrixStack.pop();
                }
            }
        );
        matrixStack.push();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
        model.render(matrixStack, builder1, combinedLight, combinedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}
