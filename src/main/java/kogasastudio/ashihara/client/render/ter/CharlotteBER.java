package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.helper.InWorldTipRenderHelper;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CharlotteBER implements BlockEntityRenderer<CharlotteTE>, WithLevelRenderer<CharlotteTE>, InWorldToolTipBER<CharlotteTE>
{
    public CharlotteBER(BlockEntityRendererProvider.Context dispatcherIn) {}

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
        float maxX = 2;
        float maxY = 2;
        poseStack.pushPose();
        poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        poseStack.translate(1f, 1f, 0);
        maxX += 1; maxY += 1;
        maxX += InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.literal(be.getBlockPos().toString()), 0x1fcb58, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2);
        maxX += InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.literal("猫咪崽子，小猫崽子，Chicken, Kitten"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2);
        poseStack.translate(1f, 3f, 0);
        maxY += 1; maxY += 3;
        for (int i = 0; i < 5; i++)
        {
            poseStack.pushPose();
            for (int j = 0; j < 5; j++)
            {
                ItemStack stack = new ItemStack(Blocks.CAMPFIRE, i * j + 1);
                InWorldTipRenderHelper.renderItemStack(stack, poseStack, bufferSource, packedLight, 2);
                poseStack.translate(2f, 0, 0);
                maxX += 2;
            }
            poseStack.popPose();
            poseStack.translate(0, 2f, 0);
            maxY += 2;
        }
        poseStack.translate(-1f, -1f, 0);
        maxX -= 1; maxY -= 1;
        maxX += InWorldTipRenderHelper.renderComponent(Minecraft.getInstance().font, Component.translatable("chat.cannotSend"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2);
        if ((maxX != animatable.getScaleX() || maxY != animatable.getScaleY()) && !RenderHelper.animControllerPlaying(animatable, c -> c.getName().equals("internal")))
        {
            be.reScale(maxX, maxY, Minecraft.getInstance().player);
        }
        poseStack.popPose();
    }
}
