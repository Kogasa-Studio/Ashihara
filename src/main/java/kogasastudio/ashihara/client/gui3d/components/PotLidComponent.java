package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

/**
 * 土锅盖子组件 - 管理锅盖交互和动画
 *
 * 职责：
 * - 处理锅盖碰撞盒（来自 ModelComponent 的骨骼追踪）
 * - 处理点击交互和锅盖打开/关闭动画
 * - 持有并驱动 SelectionFrameComponent（通过 onHoverEnter/Exit 钩子）
 */
public class PotLidComponent extends AbstractComponent implements ISelectable
{
    protected static final String LID_BONE = "lid";

    protected final PotModelComponent modelComponent;
    protected final SelectionFrameComponent selectionFrame;
    protected Consumer<Boolean> toggleCallback;

    public PotLidComponent(PotModelComponent modelComponent)
    {
        this.modelComponent = modelComponent;
        this.interactionPriority = 200;
        this.scaleX = 56;
        this.scaleY = 20;

        SelectionFrameModel frameModel = new SelectionFrameModel(
            "geo/assistance/cubic_selection_frame.geo.json",
            "textures/geo/highlight_outline.png",
            Minecraft.getInstance().player
        );
        this.selectionFrame = new SelectionFrameComponent(frameModel);
        this.selectionFrame.setThickness(0.25d);
    }

    @Override
    public void init()
    {
        super.init();
        this.addChild(selectionFrame);
    }

    public PotLidComponent setToggleCallback(Consumer<Boolean> toggleCallback)
    {
        this.toggleCallback = toggleCallback;
        return this;
    }

    // ---- 悬停钩子：驱动选框 ----

    @Override
    protected void onHoverEnter()
    {
        if (!this.modelComponent.isLidRemoved())
        {
            this.selectionFrame.onHoverEnter(this.getCollisionBoxes());
        }
    }

    @Override
    protected void onHoverExit()
    {
        this.selectionFrame.onHoverExit();
    }

    // ---- 碰撞盒 ----

    @Override
    public List<OBB> getCollisionBoxes()
    {
        List<OBB> boxes = this.modelComponent.getBoneCollisionBoxes(LID_BONE);
        return boxes.isEmpty() ? List.of(this.buildFallbackBox()) : boxes;
    }

    // ---- 渲染 ----

    @Override
    protected void renderSelf(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        /*// 锅盖打开后强制隐藏选框
        if (this.modelComponent.isLidRemoved()
            && this.selectionFrame.getFrameState() != SelectionFrameComponent.FrameState.HIDDEN)
        {
            this.selectionFrame.forceHide();
        }*/

        // 调试模式下绘制2D包围框
        Screen3D screen = this.getScreen();
        if (screen == null || !screen.debugOverlayEnabled || !this.hovered) return;

        float[] bounds = this.getScreenBounds();
        if (bounds == null) return;

        int left   = Math.round(bounds[0]);
        int top    = Math.round(bounds[1]);
        int right  = Math.round(bounds[2]);
        int bottom = Math.round(bounds[3]);
        int color  = 0xFFFFD36B;

        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
    }

    // ---- 交互 ----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button != 0) return false;

        boolean lidRemoved = this.modelComponent.toggleLid();
        if (this.toggleCallback != null) this.toggleCallback.accept(lidRemoved);

        if (lidRemoved) this.selectionFrame.forceHide();

        this.dragging = false;
        return true;
    }

    // ---- ISelectable ----

    @Override
    public SelectionFrameComponent getSelectionFrame()
    {
        return this.selectionFrame;
    }

    // ---- fallback ----

    private OBB buildFallbackBox()
    {
        // 从 presetTransform 中读出屏幕空间中心点
        Matrix4f t = this.modelComponent.getPresetTransform();
        float centerX = t.m30();
        float centerY = t.m31();
        return new OBB(
            new Vector3f(),
            new Vector3f(),
            new Vector3f(this.scaleX, this.scaleY, 0),
            new Matrix4f().translation(centerX - this.scaleX / 2.0f, centerY - this.scaleY / 2.0f, 0)
        );
    }
}
