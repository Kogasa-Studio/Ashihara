package kogasastudio.ashihara.client.render.ister;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.client.models.PailItemModel;
import kogasastudio.ashihara.client.render.LayerRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

import static com.mojang.math.Axis.XP;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

@OnlyIn(Dist.CLIENT)
public class PailISTER extends BlockEntityWithoutLevelRenderer
{

    private final PailItemModel model;

    public PailISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet)
    {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
        this.model = new PailItemModel(pEntityModelSet.bakeLayer(LayerRegistryHandler.PAIL_ITEM));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        ResourceLocation PAIL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/block/pail_multiple.png");

        PailTE te = new PailTE(BlockPos.ZERO, BlockRegistryHandler.PAIL.get().defaultBlockState());
        CustomData customData = stack.getComponents().getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (!customData.isEmpty() && Minecraft.getInstance().level != null) customData.loadInto(te, Minecraft.getInstance().level.registryAccess());

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        VertexConsumer builder1 = buffer.getBuffer(RenderType.entitySolid(PAIL));

        FluidTank bucket = te.getTank();
        if (!bucket.isEmpty())
        {
            FluidStack fluid = bucket.getFluid();
            TextureAtlasSprite FLUID =
                    Minecraft.getInstance()
                            .getBlockRenderer()
                            .getBlockModelShaper()
                            .getParticleIcon(fluid.getFluid().defaultFluidState().createLegacyBlock());
            int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();
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
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.mulPose(XP.rotationDegrees(180));
        model.renderToBuffer(matrixStack, builder1, combinedLight, combinedOverlay, 0xFFFFFF);
        matrixStack.popPose();
    }
}
