package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR;
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

    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int RGBA, float alpha)
    {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA) & 0xFF) / 255f;

        builder.pos(matrix, x, y, z)
        .tex(u, v)
        .color(red, green, blue, alpha)
        .endVertex();
    }

    public static void renderFluidStackInGUI(Matrix4f matrix, FluidStack fluid, int width, int height, float x, float y)
    {
        RenderSystem.enableBlend();

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

        LOGGER_MAIN.info
        (
            "\n{\n    hFloors: " + hFloors
            + ";\n    wFloors: " + wFloors
            + ";\n    extraHeight: " + extraHeight
            + ";\n    extraWidth: " + extraWidth
            + ";\n    fluidAtlas: " + FLUID.getName().toString()
            + ";\n}"
        );

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        for (int i = hFloors; i >= 0; i --)
        {
            if (i == 0 && extraHeight == 0) break;
            LOGGER_MAIN.info("Start rendering by height;");
            LOGGER_MAIN.info("    i: " + i + ";");
            float yStart = y + (i * 16);
            float yOffset = i == 0 ? extraHeight : 16;
            float v1 = i == 0 ? FLUID.getMinV() + ((FLUID.getMaxV() - v0) * ((float) extraHeight / 16f)) : FLUID.getMaxV();
            LOGGER_MAIN.info("    yStart: " + yStart + ";");
            LOGGER_MAIN.info("    yOffset: " + yOffset + ";");
            LOGGER_MAIN.info("    v0: " + v0 + ";");
            LOGGER_MAIN.info("    v1: " + v1 + ";");

            for (int j = wFloors; j >= 0; j --)
            {
                if (j == 0 && extraWidth == 0) break;
                LOGGER_MAIN.info("    Start rendering by width;");
                LOGGER_MAIN.info("        j: " + j + ";");
                float xStart = x + (wFloors - j) * 16;
                float xOffset = i == 0 ? extraWidth : 16;
                float u1 = i == 0 ? FLUID.getMinU() + ((FLUID.getMaxU() - v0) * ((float) extraWidth / 16f)) : FLUID.getMaxU();
                LOGGER_MAIN.info("        xStart: " + xStart + ";");
                LOGGER_MAIN.info("        xOffset: " + xOffset + ";");
                LOGGER_MAIN.info("        u0: " + u0 + ";");
                LOGGER_MAIN.info("        u1: " + u1 + ";");

                builder.begin(GL11.GL_QUADS, POSITION_TEX_COLOR);
                buildMatrix(matrix, builder, xStart, yStart, 0.0f, u0, v0, color, 1.0f);
                LOGGER_MAIN.info("            rendered quad{x: " + xStart + ", y: " + yStart + ", u: " + u0 + ", v: " + v0 + ";}");
                buildMatrix(matrix, builder, xStart, yStart - yOffset, 0.0f, u0, v1, color, 1.0f);
                LOGGER_MAIN.info("            rendered quad{x: " + xStart + ", y: " + (yStart - yOffset) + ", u: " + u0 + ", v: " + v1 + ";}");
                buildMatrix(matrix, builder, xStart + xOffset, yStart - yOffset, 0.0f, u1, v1, color, 1.0f);
                LOGGER_MAIN.info("            rendered quad{x: " + (xStart + xOffset) + ", y: " + (yStart - yOffset) + ", u: " + u1 + ", v: " + v1 + ";}");
                buildMatrix(matrix, builder, xStart + xOffset, yStart, 0.0f, u1, v0, color, 1.0f);
                LOGGER_MAIN.info("            rendered quad{x: " + (xStart + xOffset) + ", y: " + yStart + ", u: " + u1 + ", v: " + v0 + ";}");
                tessellator.draw();
                LOGGER_MAIN.info("    Width rendering finished;");
            }
            LOGGER_MAIN.info("Height rendering finished;");
        }
        RenderSystem.disableBlend();
    }
}
