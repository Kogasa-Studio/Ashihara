package kogasastudio.ashihara.client.gui3d.components;

import com.geckolib.animation.object.EasingType;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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
public class SelectionFrameComponent extends ModelComponent
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
    public double animDuration = 1d;
    public double thickness = 0.25d;
    public EasingType expandEasing  = EasingType.EASE_OUT_CUBIC;
    public EasingType contractEasing = EasingType.EASE_IN_CUBIC;

    public final Vector3f minBounds = new Vector3f();
    public final Vector3f maxBounds = new Vector3f();

    protected FrameState state = FrameState.HIDDEN;
    protected double animProgress = 0.0;

    private OBB targetOBB;

    public SelectionFrameComponent(SelectionFrameModel model)
    {
        super(model, true);
        ((SelectionFrameModel) this.model).setThickness(this.thickness);
        this.interactionPriority = -1000;
        this.doTick = true;
        this.enabled = false;
        this.visible = false;
        this.enableTick();
    }

    @Override
    public void tick()
    {
        super.tick();
        updateOBB();
        if (!this.visible) return;

        double delta = (1.0 / 20.0) / this.animDuration;

        if (this.state == FrameState.EXPANDING)
        {
            this.animProgress += delta;
            if (this.animProgress >= 1.0)
            {
                this.animProgress = 1.0;
                this.state = FrameState.EXPANDED;
            }
        }
        else if (this.state == FrameState.CONTRACTING)
        {
            this.animProgress -= delta;
            if (this.animProgress <= 0.0)
            {
                this.animProgress = 0.0;
                this.state = FrameState.HIDDEN;
                this.visible = false;
            }
        }
    }

    @Override
    public Object getRelatedObject()
    {
        return this.model;
    }
// ---- 外部驱动接口 ----

    /** 光标进入：从当前尺寸膨胀到OBB目标尺寸。使用默认 animDuration。 */
    public void onHoverEnter()
    {
        onHoverEnter(this.animDuration);
    }

    private void updateOBB()
    {
        List<OBB> obbs = this.parent.getCollisionBoxes();
        if (obbs == null || obbs.isEmpty())
        {
            this.targetOBB = null;
            return;
        }

        // 合并所有OBB边界（以第一个OBB的矩阵为参考系）
        OBB ref = obbs.getFirst();
        Vector3f min = new Vector3f(ref.minXYZ());
        Vector3f max = new Vector3f(ref.maxXYZ());
        for (int i = 1; i < obbs.size(); i++)
        {
            min.min(new Vector3f(obbs.get(i).minXYZ()));
            max.max(new Vector3f(obbs.get(i).maxXYZ()));
        }
        this.targetOBB = new OBB(ref.center(), min, max, ref.pose());
    }

    /** 光标进入：从当前尺寸膨胀到OBB目标尺寸。使用自定义 duration。 */
    public void onHoverEnter(double duration)
    {
        updateOBB();
        this.minBounds.set(this.targetOBB.minXYZ());
        this.maxBounds.set(this.targetOBB.maxXYZ());

        ((SelectionFrameModel) this.model).setTarget(this.targetOBB);

        if (this.state == FrameState.HIDDEN || this.state == FrameState.EXPANDED)
        {
            this.animProgress = 0.0;
            if (Minecraft.getInstance().player != null) this.model.triggerAnim(Minecraft.getInstance().player, this.model.hashCode(), SelectionFrameModel.EXPAND, SelectionFrameModel.EXPAND);
        }

        this.model.setAnimSpeed(SelectionFrameModel.EXPAND, 2.0 / duration);
        this.animDuration = duration;
        this.visible = true;
        this.state = FrameState.EXPANDING;
    }

    /** 光标离开：从当前尺寸收缩到0。使用默认 animDuration。 */
    public void onHoverExit()
    {
        onHoverExit(this.animDuration);
    }

    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        // presetTransform is derived from obb.pose() which already contains the PiP base matrix.
        // Use set() to avoid doubling the PiP transform (ModelComponent's mul-based path would apply it twice).
        if (this.renderModel && this.visible)
        {
            output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
            {
                poseStack.pushPose();
                poseStack.last().pose().set(this.targetOBB.pose());
                poseStack.translate(-0.5f, -0.5f, -0.5f);
                poseStack.last().normal().identity();
                this.model.RENDERER.performRenderPass
                (
                    this.model,
                    this.getRelatedObject(),
                    poseStack,
                    submitNodeCollector,
                    new CameraRenderState(),
                    15728880,
                    partialTick
                );
                poseStack.popPose();
            }));
        }
    }

    /** 光标离开：从当前尺寸收缩到0。使用自定义 duration。 */
    public void onHoverExit(double duration)
    {
        if (this.state == FrameState.HIDDEN) return;
        this.model.setAnimSpeed(SelectionFrameModel.EXPAND, -2.0 / duration);
        this.animDuration = duration;
        this.state = FrameState.CONTRACTING;
    }

    /** 立即隐藏（不播放收缩动画，用于强制清除场景）。 */
    public void forceHide()
    {
        this.state = FrameState.HIDDEN;
        this.visible = false;
        this.targetOBB = null;
        this.animProgress = 0.0;
    }

    // ---- 调试 ----

    public String getDebugStateLine()
    {
        return String.format(Locale.ROOT,
            "SelectionFrame state=%s vis=%s min=(%.3f,%.3f,%.3f) max=(%.3f,%.3f,%.3f)",
            this.state, this.visible,
            this.minBounds.x, this.minBounds.y, this.minBounds.z,
            this.maxBounds.x, this.maxBounds.y, this.maxBounds.z);
    }
}
