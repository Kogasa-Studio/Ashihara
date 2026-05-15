package kogasastudio.ashihara.client.gui3d.components;

import com.geckolib.animation.object.EasingType;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Locale;

/**
 * 立体选框组件，作为子组件附加到实现了 ISelectable 的组件上。
 * 由父组件在覆写 onHoverEnter / onHoverExit 时驱动。
 * <p>
 * 状态机：
 *   HIDDEN → (onHoverEnter) → EXPANDING → (anim done) → EXPANDED
 *   EXPANDING/EXPANDED → (onHoverExit) → CONTRACTING → (anim done, scale=0) → HIDDEN
 *   CONTRACTING → (onHoverEnter) → EXPANDING（从当前尺寸继续膨胀）
 */
public class SelectionFrameComponent extends AbstractComponent
{
    public enum FrameState { HIDDEN, EXPANDING, EXPANDED, CONTRACTING }

    public void setAnimDuration(double animDuration)
    {
        this.animDuration = animDuration;
    }

    public void setThickness(double thickness)
    {
        this.thickness = thickness;
    }

    public void setContractEasing(EasingType contractEasing)
    {
        this.contractEasing = contractEasing;
    }

    public void setExpandEasing(EasingType expandEasing)
    {
        this.expandEasing = expandEasing;
    }

    /** 默认动画时长（ticks）。可在实例上直接赋值以覆盖。 */
    public double animDuration = 8.0d;
    public double thickness = 1.0d;
    public EasingType expandEasing  = EasingType.EASE_OUT_CUBIC;
    public EasingType contractEasing = EasingType.EASE_IN_CUBIC;

    public final SelectionFrameModel frameModel;
    public final Matrix4f targetMatrix = new Matrix4f();
    public final Vector3f minBounds = new Vector3f();
    public final Vector3f maxBounds = new Vector3f();

    protected FrameState state = FrameState.HIDDEN;

    public SelectionFrameComponent(SelectionFrameModel model)
    {
        super();
        this.frameModel = model;
        this.interactionPriority = -1000;
        this.enabled = false;
        this.visible = false;
        this.enableTick();
    }

    @Override
    public void init()
    {
        super.init();
        // 零初始化：所有棱缩放为0，位于原点
        this.frameModel.syncFrame(new Vector3f(), new Vector3f());
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!this.visible)
        {
            return;
        }

        // 收缩动画结束 → 隐藏
        if (this.state == FrameState.CONTRACTING && this.frameModel.isInternalAnimFinished())
        {
            this.frameModel.syncFrame(this.minBounds, this.maxBounds);
            this.state = FrameState.HIDDEN;
            this.visible = false;
            return;
        }

        // 膨胀动画结束 → 进入稳定状态
        if (this.state == FrameState.EXPANDING && this.frameModel.isInternalAnimFinished())
        {
            this.state = FrameState.EXPANDED;
        }
    }


    // ---- 外部驱动接口 ----

    /** 光标进入：从当前尺寸膨胀到OBB目标尺寸。使用默认 animDuration。 */
    public void onHoverEnter(List<OBB> obbs)
    {
        onHoverEnter(obbs, this.animDuration);
    }

    /** 光标进入：从当前尺寸膨胀到OBB目标尺寸。使用自定义 duration。 */
    public void onHoverEnter(List<OBB> obbs, double duration)
    {
        if (obbs == null || obbs.isEmpty()) return;

        // 合并所有OBB边界（以第一个OBB的矩阵为参考系）
        OBB ref = obbs.getFirst();
        Vector3f min = new Vector3f(ref.minXYZ()).mul(16f);
        Vector3f max = new Vector3f(ref.maxXYZ()).mul(16f);
        for (int i = 1; i < obbs.size(); i++)
        {
            min.min(obbs.get(i).minXYZ());
            max.max(obbs.get(i).maxXYZ());
        }

        this.targetMatrix.set(ref.pose());
        this.minBounds.set(min);
        this.maxBounds.set(max);

        // 从打断处的当前尺寸出发
        float[] cur = this.frameModel.readCurrentEdgeScales();

        this.frameModel.syncFrame(min, max);
        this.frameModel.triggerFrameAnimation(min, max, cur[0], cur[1], cur[2], 1, 1, 1, duration, this.expandEasing);

        this.visible = true;
        this.state = FrameState.EXPANDING;
    }

    /** 光标离开：从当前尺寸收缩到0。使用默认 animDuration。 */
    public void onHoverExit()
    {
        onHoverExit(this.animDuration);
    }

    /** 光标离开：从当前尺寸收缩到0。使用自定义 duration。 */
    public void onHoverExit(double duration)
    {
        if (this.state == FrameState.HIDDEN) return;

        float[] cur = this.frameModel.readCurrentEdgeScales();
        this.frameModel.syncFrame(this.minBounds, this.maxBounds);
        this.frameModel.triggerFrameAnimation(
            this.minBounds, this.maxBounds,
            cur[0], cur[1], cur[2], 0, 0, 0,
            duration, this.contractEasing
        );
        this.state = FrameState.CONTRACTING;
    }

    /** 立即隐藏（不播放收缩动画，用于强制清除场景）。 */
    public void forceHide()
    {
        this.frameModel.syncFrame(this.minBounds, this.maxBounds);
        this.state = FrameState.HIDDEN;
        this.visible = false;
    }

    public FrameState getFrameState() { return this.state; }

    // ---- 调试 ----

    public String getDebugStateLine()
    {
        float[] scales = this.frameModel.readCurrentEdgeScales();
        return String.format(Locale.ROOT,
            "SelectionFrame state=%s vis=%s min=(%.3f,%.3f,%.3f) max=(%.3f,%.3f,%.3f) scale=(%.3f,%.3f,%.3f)",
            this.state, this.visible,
            this.minBounds.x, this.minBounds.y, this.minBounds.z,
            this.maxBounds.x, this.maxBounds.y, this.maxBounds.z,
            scales[0], scales[1], scales[2]);
    }
}
