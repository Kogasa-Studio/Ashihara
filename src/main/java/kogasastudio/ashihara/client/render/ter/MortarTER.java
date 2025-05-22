package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.helper.InWorldTipRenderHelper;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.utils.InWorldTooltipInfoWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.Map;

public class MortarTER implements BlockEntityRenderer<MortarBE>, WithLevelRenderer<MortarBE>, InWorldToolTipBER<MortarBE>
{
    public Map<Integer, ItemStack> items = new HashMap<>(8);
    private final InWorldTooltipInfoWrapper info = new InWorldTooltipInfoWrapper();

    public MortarTER(BlockEntityRendererProvider.Context rendererDispatcherIn) {}

    RenderType renderType = RenderType.entityTranslucent(UIPanelModel.DEFAULT_TEXTURE);

    @Override
    public void render(MortarBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (blockEntity.switchFluid.checkRender() && !blockEntity.fluidTank.isEmpty())
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
        }
        //blockEntity.item_display_positions.render(poseStack, buffer, packedLight, packedOverlay);
        if (blockEntity.checkRender())
        {
            poseStack.pushPose();
            poseStack.translate(0, 0.8 - 0.16 / 16f, 0);
            blockEntity.ui_panel_model.RENDERER.setupInformationRenderer(this.getInfoRenderer(blockEntity));
            blockEntity.ui_panel_model.RENDERER.render(poseStack, blockEntity.ui_panel_model, buffer, renderType, buffer.getBuffer(renderType), packedLight, partialTick);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(MortarBE blockEntity, Vec3 cameraPos)
    {
        if (blockEntity.needBlockUpdate()) blockEntity.updateBlock();
        return blockEntity.checkRender() || (blockEntity.switchFluid.checkRender() && !blockEntity.fluidTank.isEmpty());
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
        PoseStack poseStack = context.poseStack();
        MortarBE te = (MortarBE) context.blockEntity();
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

    public static final ResourceLocation PROGRESS = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/mortar_progress_bar.png");
    public static final ResourceLocation PROGRESS_OUTLINE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/mortar_progress_bar_outline.png");

    @Override
    public void renderInfo(MortarBE be, PoseStack poseStack, UIPanelModel animatable, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer, int packedLight, float partialTick)
    {
        info.init(poseStack);
        info.translate(1f, 1f);
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.translatable("block.ashihara.mortar"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        info.translate(0, 1f);

        MutableComponent contents = Component.translatable("tooltip.ashihara.contents");
        if (be.inventory.isEmpty()) contents.append(Component.translatable("tooltip.ashihara.none"));
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, contents, 0x23f17d, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        if (!be.inventory.isEmpty())
        {
            info.checkAndOffsetY(InWorldTipRenderHelper.renderItemStacks(poseStack, bufferSource, packedLight, 2f, 4, be.inventory.getAllContents()));
            //info.translate(0, 2f);
        }

        MutableComponent fluid =  Component.translatable("tooltip.ashihara.fluid_existence");
        if (be.fluidTank.isEmpty()) fluid.append(Component.translatable("tooltip.ashihara.none"));
        else fluid.append(be.fluidTank.getFluid().getHoverName()).append(" ").append(String.valueOf(be.fluidTank.getFluid().getAmount())).append(" mB");
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, fluid, 0x237df1, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        if (!be.fluidTank.isEmpty())
        {
            info.checkAndOffsetY(InWorldTipRenderHelper.renderFluid(poseStack, bufferSource, be.fluidTank.getFluid(), 16f, 16f, OverlayTexture.NO_OVERLAY, packedLight, 2));
        }

        if (be.currentRecipe != null)
        {
            MutableComponent recipe = Component.translatable("tooltip.ashihara.current_recipe");
            info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, recipe, 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));

            info.pushPose();
            InWorldTipRenderHelper.XY ingredients = InWorldTipRenderHelper.renderIngredients(poseStack, bufferSource, packedLight, 2f, 4, be.currentRecipe.getSizedIngredients());
            poseStack.translate(ingredients.getX(), 0, 0);

            InWorldTipRenderHelper.XY progress = InWorldTipRenderHelper.blit(poseStack, bufferSource.getBuffer(RenderType.entityCutout(PROGRESS_OUTLINE)), 32, 16, 0, 0, 1, 0, 1, OverlayTexture.NO_OVERLAY, packedLight, 2);
            InWorldTipRenderHelper.blit(poseStack, bufferSource.getBuffer(RenderType.entityCutout(PROGRESS)), 32, 16, 0, 0, be.getProgress(), 0, 1, OverlayTexture.NO_OVERLAY, packedLight, 2);
            poseStack.translate(progress.getX(), 0, 0);

            InWorldTipRenderHelper.XY output = InWorldTipRenderHelper.renderItemStacks(poseStack, bufferSource, packedLight, 2f, 4, be.currentRecipe.getOutput());
            poseStack.translate(output.getX(), 0, 0);
            info.popPose();

            float maxX = ingredients.getX() + progress.getX() + output.getX();
            float maxY = Math.max(Math.max(ingredients.getY(), progress.getY()), output.getY());
            info.check(maxX, 0);
            info.translate(0, maxY);

            MutableComponent current_parrel = Component.translatable("tooltip.ashihara.current_parallel").append(String.valueOf(be.productionMultiplier));
            info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, current_parrel, 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));

            if (!be.getQueue().isEmpty())
            {
                MutableComponent next_tool = Component.translatable("tooltip.ashihara.mortar.next_tool").append(be.getQueue().peek().getName());
                info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, next_tool, 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
            }

            if (be.currentRecipe.getFluidCost() != null && !be.currentRecipe.getFluidCost().isEmpty())
            {
                MutableComponent fluid_action = be.currentRecipe.fluidOpcode == 0 ? Component.translatable("tooltip.ashihara.fluid_consume_exception") : Component.translatable("tooltip.ashihara.fluid_output_exception");
                fluid_action.append(be.currentRecipe.getFluidCost().getHoverName()).append(" ").append(String.valueOf(be.currentRecipe.getFluidCost().getAmount())).append(" mB");
                info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, fluid_action, 0x237df1, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));

                info.checkAndOffsetY(InWorldTipRenderHelper.renderFluid(poseStack, bufferSource, be.currentRecipe.getFluidCost(), 16f, 16f, OverlayTexture.NO_OVERLAY, packedLight, 2));
            }
        }
        if ((info.getMaxX() != animatable.getScaleX() || info.getMaxY() != animatable.getScaleY()) && !RenderHelper.animControllerPlaying(animatable, c -> c.getName().equals("internal")))
        {
            be.reScale(info.getMaxX() + 2, info.getMaxY(), Minecraft.getInstance().player);
        }
        info.cast();
    }
}
