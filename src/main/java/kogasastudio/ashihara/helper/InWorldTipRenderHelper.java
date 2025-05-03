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
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class InWorldTipRenderHelper
{
    public static XY renderComponent(Font font, Component text, int color, boolean dropShadow, PoseStack poseStack, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int bgColor, int light, float sizeInPixel, int maxLineWidth)
    {
        float multiplier = (8f / 9f) * (1f / 16f) * (sizeInPixel * 2);
        poseStack.pushPose();
        poseStack.scale(multiplier, multiplier, multiplier);
        poseStack.translate(1f, 1f, 0f);
        font.drawInBatch(text, 0f, 0f, color, dropShadow, poseStack.last().pose(), bufferSource, displayMode, bgColor, light);

        List<String> list = FontHelper.processText(text.getString(), maxLineWidth);
        int maxWidth = font.width(list.getFirst());
        for (String s : list) {maxWidth = Math.max(maxWidth, font.width(s));}
        FontHelper.renderFormattedText(poseStack, list, 0, 0, color, dropShadow, false, bufferSource, displayMode, bgColor, light);

        poseStack.scale(1 / multiplier, 1 / multiplier, 1 / multiplier);
        poseStack.popPose();
        return XY.of(maxWidth * multiplier,  list.size() * font.lineHeight * multiplier);
    }

    public static void renderItemStack(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 42);
        poseStack.pushPose();
        poseStack.translate(1f, 1f, 0f);
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateXYZ(0f, (float) Math.toRadians(180), (float) Math.toRadians(180)));
        poseStack.scale(sizeInPixel, sizeInPixel, sizeInPixel);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GUI, false, poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.scale(sizeInPixel, sizeInPixel, sizeInPixel);
        poseStack.translate(0.25, 0, -0.25);
        renderComponent(Minecraft.getInstance().font, Component.literal(String.valueOf(stack.getCount())), 0xffffff, false, poseStack, bufferSource, Font.DisplayMode.NORMAL, 0, light, 0.5f, 64);
        poseStack.popPose();
        poseStack.popPose();
    }

    public static XY renderItemStacks(PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel, int maxLineWidth, List<ItemStack> itemStacks)
    {
        float maxX = 0;
        float maxY = 0;
        poseStack.pushPose();
        for (int i = 0; i < itemStacks.size(); i += maxLineWidth)
        {
            poseStack.pushPose();
            for (int j = 0; j < maxLineWidth; j++)
            {
                if (i + j >= itemStacks.size()) break;
                ItemStack itemStack = itemStacks.get(i + j);
                if (itemStack.isEmpty()) continue;
                renderItemStack(itemStack, poseStack, bufferSource, light, sizeInPixel);
                maxX = Math.max(maxX, j * sizeInPixel);
                poseStack.translate(sizeInPixel, 0, 0);
            }
            poseStack.popPose();
            maxY += sizeInPixel;
            poseStack.translate(0, sizeInPixel, 0);
        }
        poseStack.popPose();
        return XY.of(maxX, maxY);
    }

    public static class XY extends Pair<Float, Float>
    {
        /**
         * Create a pair and store two objects.
         *
         * @param x the first object to store
         * @param y the second object to store
         */
        public XY(float x, float y)
        {
            super(x, y);
        }

        public static XY of(float x, float y)
        {
            return new XY(x, y);
        }

        public float getX() {return this.getA();}

        public float getY() {return this.getB();}
    }
}
