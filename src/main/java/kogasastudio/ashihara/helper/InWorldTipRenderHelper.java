package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.util.RenderUtil;

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
        //font.drawInBatch(text, 0f, 0f, color, dropShadow, poseStack.last().pose(), bufferSource, displayMode, bgColor, light);

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
                maxX = Math.max(maxX, (j + 1) * sizeInPixel);
                poseStack.translate(sizeInPixel, 0, 0);
            }
            poseStack.popPose();
            maxY += sizeInPixel;
            poseStack.translate(0, sizeInPixel, 0);
        }
        poseStack.popPose();
        return XY.of(maxX, maxY);
    }

    public static void renderIngredient(SizedIngredient ingredient, PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel)
    {
        List<ItemStack> stacks = List.of(ingredient.getItems());
        double ticks = RenderUtil.getCurrentTick();
        int i = (int) (ticks / 20);
        int cat = i % stacks.size();
        cat = Math.clamp(cat, 0, stacks.size() - 1);
        ItemStack itemStack = stacks.get(cat);
        renderItemStack(itemStack, poseStack, bufferSource, light, sizeInPixel);
    }

    public static XY renderIngredients(PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel, int maxLineWidth, List<SizedIngredient> ingredients)
    {
        float maxX = 0;
        float maxY = 0;
        poseStack.pushPose();
        for (int i = 0; i < ingredients.size(); i += maxLineWidth)
        {
            poseStack.pushPose();
            for (int j = 0; j < maxLineWidth; j++)
            {
                if (i + j >= ingredients.size()) break;
                SizedIngredient ingredient = ingredients.get(i + j);
                if (ingredient.ingredient().isEmpty()) continue;
                renderIngredient(ingredient, poseStack, bufferSource, light, sizeInPixel);
                maxX = Math.max(maxX, (j + 1) * sizeInPixel);
                poseStack.translate(sizeInPixel, 0, 0);
            }
            poseStack.popPose();
            maxY += sizeInPixel;
            poseStack.translate(0, sizeInPixel, 0);
        }
        poseStack.popPose();
        return XY.of(maxX, maxY);
    }

    public static XY renderIngredientsUnsized(PoseStack poseStack, MultiBufferSource bufferSource, int light, float sizeInPixel, int maxLineWidth, List<Ingredient> ingredients)
    {
        List<SizedIngredient> list = new ArrayList<>();
        for (Ingredient ingredient : ingredients)
        {
            list.add(new SizedIngredient(ingredient, 1));
        }
        return renderIngredients(poseStack, bufferSource, light, sizeInPixel, maxLineWidth, list);
    }

    public static XY renderFluid(PoseStack poseStack, MultiBufferSource bufferSource, FluidStack fluid, float width, float height, int overlay, int light, float fullSpriteSizeInPixel)
    {
        poseStack.pushPose();
        poseStack.scale(fullSpriteSizeInPixel / 16, fullSpriteSizeInPixel / 16, fullSpriteSizeInPixel / 16);
        poseStack.translate(width / 2, height / 2, 0);
        poseStack.mulPose(new Quaternionf().rotateXYZ(0, (float) Math.toRadians(180d), (float) Math.toRadians(180d)));
        poseStack.translate(-width / 2, -height / 2, 0);
        poseStack.scale(14f / 16f, 14f / 16f, 14f / 16f);
        poseStack.translate(0.5f, 0.5f, 0f);
        RenderHelper.blitFluid(poseStack, bufferSource, fluid, 0, width, 0, height, 0, overlay, light);
        poseStack.popPose();
        return XY.of((width / 16) * fullSpriteSizeInPixel, (height / 16) * fullSpriteSizeInPixel);
    }

    public static XY blit(PoseStack poseStack, VertexConsumer consumer, float width, float height, float z, float minU, float maxU, float minV, float maxV, int overlay, int light, float sizeOf16xSprite)
    {
        poseStack.pushPose();
        poseStack.scale(sizeOf16xSprite / 16, sizeOf16xSprite / 16, sizeOf16xSprite / 16);
        poseStack.translate(width / 2, height / 2, 0);
        poseStack.mulPose(new Quaternionf().rotateXYZ(0, (float) Math.toRadians(180d), (float) Math.toRadians(180d)));
        poseStack.translate(-width / 2, -height / 2, 0);
        RenderHelper.blit(poseStack, consumer, 0, width, 0, height, z, minU, maxU, minV, maxV, overlay, light);
        poseStack.popPose();
        return XY.of((width / 16) * sizeOf16xSprite, (height / 16) * sizeOf16xSprite);
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
