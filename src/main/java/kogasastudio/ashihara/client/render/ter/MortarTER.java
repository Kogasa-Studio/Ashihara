package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.render.AshiharaAtlas;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.utils.AshiharaTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import static kogasastudio.ashihara.block.MortarBlock.FACING;
import static kogasastudio.ashihara.helper.RenderHelper.XTP;
import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MortarTER //implements BlockEntityRenderer<MortarTE>
{
    /*private static final String CEREALS = "cereals_level";
    private static final String PROCESSED = "processed_level";

    *//*public static Map<String, ResourceLocation> assistanceMap()
    {
        ImmutableMap.Builder<String, ResourceLocation> builder = new ImmutableMap.Builder<>();

        for (ResourceLocation resourceLocation : AshiharaAtlas.ALL_ASSISTANCE)
        {
            String path = resourceLocation.getPath();
            builder.put(path.substring(path.lastIndexOf("/") + 1), resourceLocation);
        }

        return builder.build();
    }*//*

    public MortarTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public void render(MortarTE tileEntityIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderHelper.renderLeveledFluidStack
                (
                        tileEntityIn, poseStackIn, bufferIn,
                        combinedLightIn, combinedOverlayIn,
                        XTP(3.5f), XTP(4.0f), XTP(3.5f),
                        XTP(12.5f), XTP(12.0f), XTP(12.5f),
                        tileEntityIn.getLevel(), tileEntityIn.getBlockPos()
                );

        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < tileEntityIn.contents.getSlots(); i += 1)
        {
            list.add(i, tileEntityIn.contents.getStackInSlot(i));
        }

        float renderHeight = XTP(4.0f);
        for (ItemStack stack : list)
        {
            if (!stack.isEmpty())
            {
                renderHeight += XTP(2.0f);
                //以Quad方式渲染谷物或产物
                if (stack.is(AshiharaTags.CEREALS) || stack.is(AshiharaTags.CEREAL_PROCESSED))
                {
                    String key = stack.is(AshiharaTags.CEREAL_PROCESSED) ? PROCESSED : CEREALS;

                    RenderType ASSISTANCE = RenderType.entityCutout(AshiharaAtlas.ALL_ASSISTANCE.get(key));
                    VertexConsumer builder = bufferIn.getBuffer(ASSISTANCE);;

                    //主渲染
                    poseStackIn.pushPose();
                    poseStackIn.translate(0.0f, renderHeight, 0.0f);
                    Matrix4f wtf = poseStackIn.last().pose();
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(3.5f), 0, 0, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(3.5f), 0.0f, XTP(12.5f), 0, 1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(12.5f), 1, 1, combinedOverlayIn, combinedLightIn);
                    buildMatrix(wtf, builder, XTP(12.5f), 0.0f, XTP(3.5f), 1, 0, combinedOverlayIn, combinedLightIn);
                } else
                {
                    poseStackIn.pushPose();
                    poseStackIn.translate(XTP(8.0f), renderHeight, XTP(8.0f));
                    poseStackIn.scale(0.6f, 0.6f, 0.6f);
                    poseStackIn.mulPose(Axis.XP.rotationDegrees(90.0f));
                    poseStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.getBlockState().getValue(FACING).toYRot()));

                    ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                    renderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, bufferIn, tileEntityIn.getLevel(), 0);
                }
                poseStackIn.popPose();
            }
        }
    }*/
}
