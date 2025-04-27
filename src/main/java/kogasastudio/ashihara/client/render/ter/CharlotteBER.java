package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class CharlotteBER implements BlockEntityRenderer<CharlotteTE>, WithLevelRenderer<CharlotteTE>
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
        poseStack.translate(0, 0.8 - 0.16/16f, 0);
        blockEntity.model.RENDERER.setupInformationRenderer(this::renderInfo);
        blockEntity.model.RENDERER.render(poseStack, blockEntity.model, buffer, renderType, buffer.getBuffer(renderType), packedLight, partialTick);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(CharlotteTE blockEntity, Vec3 cameraPos)
    {
        return blockEntity.checkRender();
    }

    public void renderInfo(PoseStack poseStack, UIPanelModel animatable, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer, int packedLight, float partialTick)
    {
        poseStack.pushPose();
        poseStack.scale(2 / 16f, 2 / 16f, 2 / 16f);
        poseStack.translate(0.5f, 0.5f, 0);
        RenderHelper.renderPixelSizedComponent(Minecraft.getInstance().font, Component.literal("猫咪崽子，小猫崽子，Chicken, Kitten"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2);
        poseStack.translate(0.5f, 1.5f, 0);
        for (int i = 0; i < 5; i++)
        {
            poseStack.pushPose();
            for (int j = 0; j < 5; j++)
            {
                ItemStack stack = new ItemStack(Blocks.CAMPFIRE, i * j + 1);
                RenderHelper.renderInWorldToolTipItem(stack, poseStack, bufferSource, packedLight);
                poseStack.translate(1f, 0, 0);
            }
            poseStack.popPose();
            poseStack.translate(0, 1f, 0);
        }
        poseStack.translate(-0.5f, -0.5f, 0);
        RenderHelper.renderPixelSizedComponent(Minecraft.getInstance().font, Component.translatable("chat.cannotSend"), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight, 2);
        poseStack.popPose();
    }
}
