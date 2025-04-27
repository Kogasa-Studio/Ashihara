package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.Map;

public class MortarTER implements BlockEntityRenderer<MortarTE>, WithLevelRenderer<MortarTE>
{
    public Map<Integer, ItemStack> items = new HashMap<>(8);

    public MortarTER(BlockEntityRendererProvider.Context rendererDispatcherIn) {}

    @Override
    public void render(MortarTE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (blockEntity.fluid_display_position == null) blockEntity.init(Minecraft.getInstance().player);
        blockEntity.fluid_display_position.handleAnimations(blockEntity.fluid_display_position, blockEntity.fluid_display_position.hashCode(), new AnimationState<>(blockEntity.fluid_display_position, 0, 0, partialTick, false), partialTick);
        blockEntity.fluid_display_position.getBone("main").ifPresent
        (
            b ->
            {
                poseStack.pushPose();
                poseStack.translate(0, b.getPosY() / 16f, 0);
                RenderHelper.renderLeveledFluidStack
                (
                    blockEntity.fluidTank.getFluid(), poseStack, buffer.getBuffer(RenderType.translucent()),
                    packedLight, OverlayTexture.NO_OVERLAY,
                    4 / 16f, 0 / 16f, 4 / 16f,
                    12 / 16f, 12 / 16f, blockEntity.getLevel(), blockEntity.getBlockPos()
                );
                poseStack.popPose();
            }
        );
        //blockEntity.item_display_positions.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public boolean shouldRender(MortarTE blockEntity, Vec3 cameraPos)
    {
        if (blockEntity.needBlockUpdate()) blockEntity.updateBlock();
        return blockEntity.switchFluid.checkRender() && !blockEntity.fluidTank.isEmpty();
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
        PoseStack poseStack = context.poseStack();
        MortarTE te = (MortarTE) context.blockEntity();
        syncItem(te.inventory);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (te.item_display_positions == null) te.init(Minecraft.getInstance().player);
        for (int i = 0; i < items.keySet().size(); i++)
        {
            ItemStack itemStack = items.get(i);
            if (itemStack == null) continue;
            BakedModel model = itemRenderer.getModel(itemStack.copyWithCount(1), te.getLevel(), null, 0);
            te.item_display_positions.getBakedModel(te.item_display_positions.getModelResource(te.item_display_positions)).getBone("level" + i).ifPresent
            (
                bone ->
                {
                    poseStack.pushPose();
                    poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);

                    poseStack.pushPose();

                    poseStack.translate(0.5, 0, 0.5);

                    RenderUtil.rotateMatrixAroundBone(poseStack, bone);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.translate(0, -3/16f, 0);
                    poseStack.scale(6f/16f, 6f/16f, 6f/16f);

                    poseStack.translate(-0.5,-0,-0.5);

                    renderer.renderModel(model, poseStack, RenderType.cutoutMipped(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
                    poseStack.popPose();

                    poseStack.popPose();
                }
            );
        }
        if (!te.switchFluid.doRender() && !te.fluidTank.isEmpty())
        {
            poseStack.pushPose();
            resetToBlock000(te, RenderType.translucent(), poseStack);
            RenderHelper.renderLeveledFluidStack
            (
            te.fluidTank.getFluid(), poseStack, context.consumerFunction().apply(RenderType.translucent()),
            LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos()), OverlayTexture.NO_OVERLAY,
            4 / 16f, (float) (te.getLiquidLevel() / 16f), 4 / 16f,
            12 / 16f, 12 / 16f, te.getLevel(), te.getBlockPos()
            );
            poseStack.popPose();
        }
    }

    public void syncItem(ItemStackHandler inventory)
    {
        this.items.clear();
        int k = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            if (!stack.isEmpty())
            {
                if (stack.getCount() > 32)
                {
                    this.items.put(k, stack);
                    k += 1;
                }
                this.items.put(k, stack);
                k += 1;
            }
        }
    }
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
