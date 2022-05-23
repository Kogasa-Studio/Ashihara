package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import kogasastudio.ashihara.block.tileentities.IFluidHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;

public class RenderHelper
{
    /**
     * 将像素坐标转换为方块内坐标
     * @param pixels 像素坐标数值, 如5, 13
     * @return 方块内坐标数值, 如0.625,  0.25
     */
    public static float XTP(float pixels) {return pixels / 16f;}

    /**
     * 将方块内坐标转换为像素坐标
     */
    public static double PTX(double pos) {return pos * 16d;}

    /**
     * 将世界内坐标转换为方块内坐标
     */
    public static double ATP(double absolutePos) {return absolutePos - Math.abs(absolutePos);}

    /**
     * 将世界内坐标转换为像素坐标
     */
    public static double ATX(double absolutePos) {return PTX(ATP(absolutePos));}

    //贴图半透明部分渲染黑色
    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int overlay, int light)
    {
        buildMatrix(matrix, builder, x, y, z, u, v, overlay, 0xffffff, 1.0f, light);
    }

    /**
     * 将给定列表中的RL统一变换为ashihara:xxx形式
     * 去掉头和尾巴就可以吃了
     * @param textures 需进行操作的列表(textures/xxx/xxx.png 形式)
     * @return 操作过的列表(ashihara:xxx/xxx形式)
     */
    public static ArrayList<ResourceLocation> cookTextureRLs(List<ResourceLocation> textures)
    {
        ArrayList<ResourceLocation> cooked = new ArrayList<>();
        for (ResourceLocation location : textures)
        {
            //这里还是用幻数
            String path = location.getPath().substring(9, location.getPath().length() - 4);
            cooked.add(new ResourceLocation(location.getNamespace(), path));
        }
        return cooked;
    }

    public static Map<String, ResourceLocation> cookTextureRLsToMap(List<ResourceLocation> textures)
    {
        Map<String, ResourceLocation> cooked = new HashMap<>();
        for (ResourceLocation location : textures)
        {
            //这里还是用幻数
            String path = location.getPath().substring(9, location.getPath().length() - 4);
            String name = path.substring(path.lastIndexOf("/") + 1);
            cooked.put(name, new ResourceLocation(location.getNamespace(), path));
        }
        return cooked;
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

        builder.vertex(matrix, x, y, z)
        .color(red, green, blue, alpha)
        .uv(u, v)
        .overlayCoords(overlay)
        .uv2(light)
        .normal(0f, 1f, 0f)
        .endVertex();
    }

    /**
     * 通过RGBA色值渲染顶点
     */
    public static void buildMatrix(Matrix4f matrix, IVertexBuilder builder, float x, float y, float z, float u, float v, int RGBA)
    {
        int red = ColorHelper.PackedColor.red(RGBA);
        int green = ColorHelper.PackedColor.green(RGBA);
        int blue = ColorHelper.PackedColor.blue(RGBA);
        int alpha = ColorHelper.PackedColor.alpha(RGBA);

        builder.vertex(matrix, x, y, z)
        .color(red, green, blue, alpha)
        .uv(u, v)
        .endVertex();
    }

    /**
     * 渲染tooltip
     * @param gui 目标Screen
     * @param matrix 渲染矩阵
     * @param mouseX 鼠标所指x
     * @param mouseY 鼠标所指y
     * @param x 鼠标悬停在上会渲染渲染tooltip的区域的起始x
     * @param y 鼠标悬停在上会渲染渲染tooltip的区域的起始y
     * @param weight 区域宽度
     * @param height 区域高度
     * @param list 渲染文本列表
     */
    public static void drawTooltip(Screen gui, MatrixStack matrix, int mouseX, int mouseY, int x, int y, int weight, int height, List<ITextComponent> list)
    {
        if ((x <= mouseX && mouseX <= x + weight) && (y <= mouseY && mouseY <= y + height))
        {
            gui.renderComponentTooltip(matrix, list, mouseX, mouseY);
        }
    }

    public static void drawFluidToolTip(Screen gui, MatrixStack matrix, int mouseX, int mouseY, int x, int y, int weight, int height, FluidStack stack, int Capacity)
    {
        if (!stack.isEmpty())
        {
            ArrayList<ITextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.ashihara.fluid_existence"));
            list.add(new StringTextComponent
            (
            "    " + I18n.get(stack.getTranslationKey())
                + ": " + stack.getAmount()
                + (Capacity > 0 ? (" mB / " + Capacity + " mB") : " mB")
            ));
            drawTooltip(gui, matrix, mouseX, mouseY, x, y, weight, height, list);
        }
    }

    /**
     * 在GUI中渲染流体
     * @param matrix 渲染矩阵
     * @param fluid 需要渲染的流体（FluidStack）
     * @param width 需要渲染的流体宽度
     * @param height 需要渲染的流体高度
     * @param x x（绝对）
     * @param y y（绝对）
     */
    public static void renderFluidStackInGUI(Matrix4f matrix, FluidStack fluid, int width, int height, float x, float y)
    {
        //正常渲染透明度
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        //获取sprite
        TextureAtlasSprite FLUID =
            Minecraft.getInstance()
            .getTextureAtlas(PlayerContainer.BLOCK_ATLAS)
            .apply(fluid.getFluid().getAttributes().getStillTexture());

        //绑atlas
        Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);

        int color = fluid.getFluid().getAttributes().getColor();

        /*
         * 获取横向和纵向层数
         * 每16像素为1层，通过将给定渲染长宽不加类型转换除16来获取层数
         * 通过取余获取数值大小在16以下的额外数值
         */
        int wFloors = width / 16;
        int extraWidth = wFloors == 0 ? width : width % 16;
        int hFloors = height / 16;
        int extraHeight = hFloors == 0 ? height : height % 16;

        float u0 = FLUID.getU0();
        float v0 = FLUID.getV0();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        /*
         * 渲染循环
         * 该循环通过两个嵌套循环完成
         * 外层循环处理 y 和 v 的变更（高度层），内层处理 x 和 u 的变更（宽度层），渲染主代码存于内层
         * 渲染逻辑是 [先从最下面的高度层开始，向右渲染此高度层含的所有宽度层并渲染额外宽度层]
         * [第一层（高）渲染完毕后渲染第二层，依此类推渲染所有高度层和额外高度层，以达成渲染任意长宽的流体矩形的目的]
         * 对于层，若层数为0（渲染数值小于16），则直接将渲染数值设为额外层数值。
         */
        for (int i = hFloors; i >= 0; i --)
        {
            //i为流程控制码，若i=0则代表高度层已全部渲染完毕，此时若额外层高度为0（渲染高度参数本来就是16的整数倍）则跳出
            if (i == 0 && extraHeight == 0) break;
            float yStart = y - ((hFloors - i) * 16);
            //获取本层/额外层的高度，若高度层渲染完毕则设为额外层高度
            float yOffset = i == 0 ? (float) extraHeight : 16;
            //获取v1
            float v1 = i == 0 ? FLUID.getV0() + ((FLUID.getV1() - v0) * ((float) extraHeight / 16f)) : FLUID.getV1();

            //x层以此类推
            for (int j = wFloors; j >= 0; j --)
            {
                if (j == 0 && extraWidth == 0) break;
                float xStart = x + (wFloors - j) * 16;
                float xOffset = j == 0 ? (float) extraWidth : 16;
                float u1 = j == 0 ? FLUID.getU0() + ((FLUID.getU1() - u0) * ((float) extraWidth / 16f)) : FLUID.getU1();

                //渲染主代码
                builder.begin(GL11.GL_QUADS, POSITION_COLOR_TEX);
                buildMatrix(matrix, builder, xStart, yStart - yOffset, 0.0f, u0, v0, color);
                buildMatrix(matrix, builder, xStart, yStart, 0.0f, u0, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart, 0.0f, u1, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart - yOffset, 0.0f, u1, v0, color);
                tessellator.end();
            }
        }
        RenderSystem.disableBlend();
    }

    public static void renderLeveledFluidStack
    (
        FluidStack fluidIn, MatrixStack stackIn, IRenderTypeBuffer bufferIn,
        int combinedLightIn, int combinedOverlayIn,
        float xStart, float heightIn, float zStart,
        float xEnd, float zEnd,
        World worldIn, BlockPos posIn
    )
    {
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.translucentNoCrumbling());

        TextureAtlasSprite FLUID = (worldIn != null && posIn != null)
            ? Minecraft.getInstance()
            .getBlockRenderer()
            .getBlockModelShaper()
            .getTexture(fluidIn.getFluid().defaultFluidState().createLegacyBlock(), worldIn, posIn)
            : Minecraft.getInstance()
            .getTextureAtlas(PlayerContainer.BLOCK_ATLAS)
            .apply(fluidIn.getFluid().getAttributes().getStillTexture());

        int color = fluidIn.getFluid().getAttributes().getColor();

        stackIn.pushPose();
        GlStateManager._enableBlend();

        stackIn.translate(0.0f, heightIn, 0.0f);
        Matrix4f wtf = stackIn.last().pose();
        //主渲染
        buildMatrix(wtf, builder, xStart, 0, zStart, FLUID.getU0(), FLUID.getV0(), combinedOverlayIn, color, 1.0f, combinedLightIn);
        buildMatrix(wtf, builder, xStart, 0, zEnd, FLUID.getU0(), FLUID.getV1(), combinedOverlayIn, color, 1.0f, combinedLightIn);
        buildMatrix(wtf, builder, xEnd, 0, zEnd, FLUID.getU1(), FLUID.getV1(), combinedOverlayIn, color, 1.0f, combinedLightIn);
        buildMatrix(wtf, builder, xEnd, 0, zStart, FLUID.getU1(), FLUID.getV0(), combinedOverlayIn, color, 1.0f, combinedLightIn);

        GlStateManager._disableBlend();
        stackIn.popPose();
    }

    public static void renderLeveledFluidStack
    (
        IFluidHandler teIn, MatrixStack stackIn, IRenderTypeBuffer bufferIn,
        int combinedLightIn, int combinedOverlayIn,
        float xStart, float minHeight, float zStart,
        float xEnd, float maxHeight, float zEnd,
        World worldIn, BlockPos posIn
    )
    {
        teIn.getTank().ifPresent
        (
            bucket ->
            {
                if (!bucket.isEmpty())
                {
                    FluidStack fluid = bucket.getFluid();
                    float height = minHeight + ((float) fluid.getAmount() / bucket.getCapacity()) * (maxHeight - minHeight);

                    renderLeveledFluidStack(fluid, stackIn, bufferIn, combinedLightIn, combinedOverlayIn, xStart, height, zStart, xEnd, zEnd, worldIn, posIn);
                }
            }
        );
    }
}
