package kogasastudio.ashihara.client.render.state;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * PiP 渲染状态，携带指南书模型渲染所需的全部数据。
 *
 * <p>在 {@code GuideBookScreen.extractRenderState} 中构建并提交到
 * {@link net.minecraft.client.gui.GuiGraphicsExtractor#submitPictureInPictureRenderState}。
 *
 * @param x0           PiP 区域左边界（屏幕 GUI 坐标）
 * @param y0           PiP 区域上边界（屏幕 GUI 坐标）
 * @param x1           PiP 区域右边界（屏幕 GUI 坐标）
 * @param y1           PiP 区域下边界（屏幕 GUI 坐标）
 * @param scale        渲染缩放（1.0 = 原生尺寸，通常配合 GUI 缩放一起起效）
 * @param scissorArea  裁剪区域（null = 无裁剪）
 * @param components        当前帧的 Model 实例（AnimatableInstanceCache 计数独立）
 * @param packedLight  打包光照坐标（GUI ctx 下使用 0xF000F0 = FULL_BRIGHT）
 * @param partialTick  帧内插值 tick（传自 extractRenderState）
 */

public record Screen3DPiPRenderState
(
    int x0,
    int y0,
    int x1,
    int y1,
    float scale,
    @Nullable ScreenRectangle scissorArea,
    Collection<GUI3DComponentRenderState> components,
    int packedLight,
    float partialTick
) implements PictureInPictureRenderState
{
    @Override
    public @Nullable ScreenRectangle bounds()
    {
        return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
    }
}
