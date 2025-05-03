package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.helper.InWorldTipRenderHelper;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.utils.InWorldTooltipInfoWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CharlotteBER implements BlockEntityRenderer<CharlotteTE>, WithLevelRenderer<CharlotteTE>, InWorldToolTipBER<CharlotteTE>
{
    public CharlotteBER(BlockEntityRendererProvider.Context dispatcherIn) {}

    private final InWorldTooltipInfoWrapper info = new InWorldTooltipInfoWrapper();

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
    }

    RenderType renderType = RenderType.entityTranslucent(UIPanelModel.DEFAULT_TEXTURE);

    @Override
    public void render(CharlotteTE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.translate(0, 0.8 - 0.16 / 16f, 0);
        blockEntity.model.RENDERER.setupInformationRenderer(this.getInfoRenderer(blockEntity));
        blockEntity.model.RENDERER.render(poseStack, blockEntity.model, buffer, renderType, buffer.getBuffer(renderType), packedLight, partialTick);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(CharlotteTE blockEntity, Vec3 cameraPos)
    {
        return blockEntity.checkRender();
    }

    @Override
    public void renderInfo(CharlotteTE be, PoseStack poseStack, UIPanelModel animatable, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer, int packedLight, float partialTick)
    {
        info.init(poseStack);
        info.translate(1f, 1f);
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.literal(be.getBlockPos().toString()), 0x1fcb58, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.literal("猫咪崽子，小猫崽子，Chicken, Kitten"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        info.translate(0, 1f);
        info.checkAndOffsetY(InWorldTipRenderHelper.renderItemStacks(poseStack, bufferSource, packedLight, 2f, 2, List.of(new ItemStack(Blocks.ACACIA_PLANKS, 22), new ItemStack(Blocks.AMETHYST_BLOCK, 7), new ItemStack(Items.GHAST_TEAR))));
        info.checkAndOffsetY(InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.translatable("chat.cannotSend"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2, 20 * 9));
        if ((info.getMaxX() != animatable.getScaleX() || info.getMaxY() != animatable.getScaleY()) && !RenderHelper.animControllerPlaying(animatable, c -> c.getName().equals("internal")))
        {
            be.reScale(info.getMaxX() + 2, info.getMaxY(), Minecraft.getInstance().player);
        }
        info.cast();
    }
}
