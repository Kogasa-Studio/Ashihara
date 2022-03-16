package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static net.minecraft.inventory.container.PlayerContainer.LOCATION_BLOCKS_TEXTURE;

public class RenderHelper
{
    //贴图半透明部分渲染黑色
    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int overlay, int light)
    {
        buildMatrix(matrix, builder, x, y, z, u, v, overlay, 0x000000, 1.0f, light);
    }

    /**
     * 渲染顶点
     * @param matrix 渲染矩阵
     * @param builder builder
     * @param x 顶点x坐标
     * @param y 顶点y坐标
     * @param z 顶点z坐标
     * @param u 顶点对应贴图的u坐标
     * @param v 顶点对应贴图的v坐标
     * @param overlay 覆盖
     * @param light 光照
     */
    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int overlay, int RGBA, float alpha, int light)
    {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA) & 0xFF) / 255f;

        builder.pos(matrix, x, y, z)
        .color(red, green, blue, alpha)
        .tex(u, v)
        .overlay(overlay)
        .lightmap(light)
        .normal(0f, 1f, 0f)
        .endVertex();
    }

    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int RGBA)
    {
        int red = ColorHelper.PackedColor.getRed(RGBA);
        int green = ColorHelper.PackedColor.getGreen(RGBA);
        int blue = ColorHelper.PackedColor.getBlue(RGBA);
        int alpha = ColorHelper.PackedColor.getAlpha(RGBA);

        builder.pos(matrix, x, y, z)
        .color(red, green, blue, alpha)
        .tex(u, v)
        .endVertex();
    }

    public static void renderFluidStackInGUI(Matrix4f matrix, FluidStack fluid, int width, int height, float x, float y)
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        TextureAtlasSprite FLUID =
            Minecraft.getInstance()
            .getAtlasSpriteGetter(LOCATION_BLOCKS_TEXTURE)
            .apply(fluid.getFluid().getAttributes().getStillTexture());

        Minecraft.getInstance().getTextureManager().bindTexture(LOCATION_BLOCKS_TEXTURE);

        int color = fluid.getFluid().getAttributes().getColor();

        int wFloors = width / 16;
        int extraWidth = wFloors == 0 ? width : width % 16;
        int hFloors = height / 16;
        int extraHeight = hFloors == 0 ? height : height % 16;

        float u0 = FLUID.getMinU();
        float v0 = FLUID.getMinV();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        for (int i = hFloors; i >= 0; i --)
        {
            if (i == 0 && extraHeight == 0) break;
            float yStart = y - ((hFloors - i) * 16);
            float yOffset = i == 0 ? (float) extraHeight : 16;
            float v1 = i == 0 ? FLUID.getMinV() + ((FLUID.getMaxV() - v0) * ((float) extraHeight / 16f)) : FLUID.getMaxV();

            for (int j = wFloors; j >= 0; j --)
            {
                if (j == 0 && extraWidth == 0) break;
                float xStart = x + (wFloors - j) * 16;
                float xOffset = j == 0 ? (float) extraWidth : 16;
                float u1 = j == 0 ? FLUID.getMinU() + ((FLUID.getMaxU() - u0) * ((float) extraWidth / 16f)) : FLUID.getMaxU();

                builder.begin(GL11.GL_QUADS, POSITION_COLOR_TEX);
                buildMatrix(matrix, builder, xStart, yStart - yOffset, 0.0f, u0, v0, color);
                buildMatrix(matrix, builder, xStart, yStart, 0.0f, u0, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart, 0.0f, u1, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart - yOffset, 0.0f, u1, v0, color);
                tessellator.draw();
            }
        }

        RenderSystem.disableBlend();
    }
}
