package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class InWorldTipRenderHelper
{
    public static float renderComponent(Font font, Component text, int color, boolean dropShadow, PoseStack poseStack, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int bgColor, int light, float sizeInPixel)
    {
        float multiplier = (8f / 9f) * (1f / 16f) * (sizeInPixel * 2);
        poseStack.pushPose();
        poseStack.scale(multiplier, multiplier, multiplier);
        poseStack.translate(1f, 1f, 0f);
        font.drawInBatch(text, 0f, 0f, color, dropShadow, poseStack.last().pose(), bufferSource, displayMode, bgColor, light);
        poseStack.scale(1 / multiplier, 1 / multiplier, 1 / multiplier);
        poseStack.popPose();
        return font.width(text) * multiplier;
    }

    public static void renderItemStack(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 42);
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateXYZ(0f, (float) Math.toRadians(180), (float) Math.toRadians(180)));
        poseStack.scale(sizeInPixel, sizeInPixel, sizeInPixel);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GUI, false, poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.scale(sizeInPixel, sizeInPixel, sizeInPixel);
        poseStack.translate(0.25, 0, -0.25);
        renderComponent(Minecraft.getInstance().font, Component.literal(String.valueOf(stack.getCount())), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, light, 0.5f);
        poseStack.popPose();
    }
}
