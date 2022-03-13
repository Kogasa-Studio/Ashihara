package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR;

public class RenderHelper
{
    //贴图半透明部分渲染黑色
    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int overlay, int light)
    {
        buildMatrix(matrix, builder, x, y, z, u, v, overlay, 0x000000, 1.0f, light);
    }

    /**
     * 渲染顶点
     * @param matrix 矩阵 (?
     * @param builder builder
     * @param x 顶点x坐标
     * @param y 顶点y坐标
     * @param z 顶点z坐标
     * @param u 顶点对应贴图的u坐标
     * @param v 顶点对应贴图的v坐标
     * @param overlay 覆盖
     * @param light 透明度?
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

    public static void buildMatrix(IVertexBuilder builder, float x, float y, float z, float u, float v, int RGBA, float alpha)
    {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA) & 0xFF) / 255f;

        builder.pos(x, y, z)
        .tex(u, v)
        .color(red, green, blue, alpha)
        .endVertex();
    }

    public static void renderFluidStackInGUI(FluidStack fluid, int width, int height, float x, float y, Tessellator tessellator)
    {
        TextureAtlasSprite FLUID =
            Minecraft.getInstance()
            .getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getTexture(fluid.getFluid().getDefaultState().getBlockState());

        int color = fluid.getFluid().getAttributes().getColor();

        int wFloors = width / 16;
        int extraWidth = wFloors == 0 ? width : width % 16;
        int hFloors = height / 16;
        int extraHeight = hFloors == 0 ? height : height % 16;

        float u0 = FLUID.getMinU();
        float v0 = FLUID.getMinV();

        BufferBuilder builder = tessellator.getBuffer();

        for (int i = hFloors; i >= 0; i --)
        {
            float yStart = y + (i * 16);
            float yOffset = i == 0 ? extraHeight : 16;
            float v1 = i == 0 ? FLUID.getMaxV() - (16 - extraHeight) : FLUID.getMaxV();

            for (int j = wFloors; j >= 0; j --)
            {
                float xStart = x + (wFloors - j) * 16;
                float xOffset = i == 0 ? extraWidth : 16;
                float u1 = i == 0 ? FLUID.getMaxU() - (16 - extraWidth) : FLUID.getMaxU();

                builder.begin(7, POSITION_TEX_COLOR);
                buildMatrix(builder, xStart, yStart, u0, v0, 0.0f, color, 1.0f);
                buildMatrix(builder, xStart, yStart - yOffset, u0, v1, 0.0f, color, 1.0f);
                buildMatrix(builder, xStart + xOffset, yStart - yOffset, u1, v1, 0.0f, color, 1.0f);
                buildMatrix(builder, xStart + xOffset, yStart, u1, v0, 0.0f, color, 1.0f);
                tessellator.draw();
            }
        }
    }
}
