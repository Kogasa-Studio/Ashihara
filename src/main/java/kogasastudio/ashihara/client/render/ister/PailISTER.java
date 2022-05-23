package kogasastudio.ashihara.client.render.ister;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.client.models.PailItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class PailISTER extends BlockEntityWithoutLevelRenderer
{
    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        ResourceLocation PAIL = new ResourceLocation("ashihara:textures/block/pail_multiple.png");

        PailTE te = new PailTE();
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && !nbt.isEmpty()) te.load(BlockRegistryHandler.PAIL.get().defaultBlockState(), nbt);

        PailItemModel model = new PailItemModel();

        VertexConsumer builder = buffer.getBuffer(RenderType.translucentNoCrumbling());
        VertexConsumer builder1 = buffer.getBuffer(RenderType.entitySolid(PAIL));

        te.getTank().ifPresent
        (
            bucket ->
            {
                if (!bucket.isEmpty()) {
                    FluidStack fluid = bucket.getFluid();
                    TextureAtlasSprite FLUID =
                        Minecraft.getInstance()
                        .getBlockRenderer()
                        .getBlockModelShaper()
                        .getParticleIcon(fluid.getFluid().defaultFluidState().createLegacyBlock());
                    int color = fluid.getFluid().getAttributes().getColor();
                    float height = ((float) fluid.getAmount() / bucket.getCapacity()) * 0.5f;

                    matrixStack.pushPose();
                    GlStateManager._enableBlend();

                    matrixStack.translate(0.0f, 0.0f, 0.0f);
                    Matrix4f wtf = matrixStack.last().pose();
                    //主渲染
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.25f, FLUID.getU0(), FLUID.getV0(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.25f, 0.09375f + height, 0.75f, FLUID.getU0(), FLUID.getV1(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.75f, FLUID.getU1(), FLUID.getV1(), combinedOverlay, color, 1.0f, combinedLight);
                    buildMatrix(wtf, builder, 0.75f, 0.09375f + height, 0.25f, FLUID.getU1(), FLUID.getV0(), combinedOverlay, color, 1.0f, combinedLight);
//                    Minecraft.getInstance().getRenderManager().getFontRenderer().drawString(matrixStack, "FUCK YOU", 0.0f, 0.0f, 0xFFFFFF);
                    matrixStack.popPose();
                }
            }
        );
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
        model.renderToBuffer(matrixStack, builder1, combinedLight, combinedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.popPose();
    }
}
