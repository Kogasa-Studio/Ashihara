package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
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
            "assistance/cubic_selection_frame",
            "textures/geo/highlight_outline.png"
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
        this.selectionFrame.onHoverEnter();
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


    // ---- 交互 ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event)
    {
        if (event.button() != 0) return false;

        boolean lidRemoved = this.modelComponent.toggleLid();
        if (this.toggleCallback != null) this.toggleCallback.accept(lidRemoved);

        //if (lidRemoved) this.selectionFrame.forceHide();

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
