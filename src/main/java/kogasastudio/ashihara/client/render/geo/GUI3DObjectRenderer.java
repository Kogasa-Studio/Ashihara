package kogasastudio.ashihara.client.render.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link GeoObjectRenderer} 的 GUI 专属子类，提供骨骼修改中间层。
 *
 * <h3>骨骼修改（写）</h3>
 * <p>通过 {@link #addBoneModifier} 注册 {@link BoneModifier} 回调，
 * 这些回调在动画控制器应用完毕后（{@code adjustModelBonesForRender} 时机）
 * 被统一派发，可对任意骨骼的旋转、缩放、位移进行二次覆盖。
 *
 * <p>只在本类覆写 {@code adjustModelBonesForRender}；子类或外部代码永远不需要
 * 再碰该方法，只需注册/注销 {@code BoneModifier}。
 *
 * <h3>骨骼追踪（读，OBB）</h3>
 * <p>通过 {@code MixinGeoBone} 注入 + {@code MixinGeoObjectRenderer} 挂载
 * tracer 列表完成，与本类正交，无需在此处处理。
 *
 * @param <T> GeoAnimatable 类型
 * @param <O> 关联对象类型（GUI 场景通常为 Void）
 * @param <R> RenderState 类型
 */
public class GUI3DObjectRenderer<T extends GeoAnimatable, O, R extends GeoRenderState>
        extends GeoObjectRenderer<T, O, R>
{
    // ── BoneModifier 接口 ────────────────────────────────────────────────────

    /**
     * 骨骼修改回调。在动画计算完毕后、几何体渲染前调用。
     *
     * <p>通过 {@link BoneSnapshots#get(String)} 按名称获取
     * {@code BoneSnapshot}，然后调用 {@code setRotation / setScale / setTranslation}
     * 等 setter 修改骨骼变换。
     *
     * <p>示例（实时更新书页厚度）：
     * <pre>{@code
     * renderer.addBoneModifier((renderPassInfo, snapshots) ->
     *     snapshots.ifPresent("page_body", snap -> snap.setScaleZ(currentThickness))
     * );
     * }</pre>
     */
    @FunctionalInterface
    public interface BoneModifier
    {
        /**
         * @param renderPassInfo 当帧渲染信息（含模型、RenderState 等）
         * @param snapshots      按骨骼名称查询 BoneSnapshot 的函数接口
         */
        void modify(RenderPassInfo<?> renderPassInfo, BoneSnapshots snapshots);
    }

    // ── 内部状态 ─────────────────────────────────────────────────────────────

    private final List<BoneModifier> boneModifiers = new ArrayList<>();

    // ── 构造器 ───────────────────────────────────────────────────────────────

    public GUI3DObjectRenderer(GeoModel<T> model)
    {
        super(model);
    }

    // ── 公开 API ─────────────────────────────────────────────────────────────

    /** 注册一个骨骼修改回调，每帧渲染前调用。 */
    public void addBoneModifier(BoneModifier modifier)
    {
        boneModifiers.add(modifier);
    }

    /** 移除一个骨骼修改回调。 */
    public boolean removeBoneModifier(BoneModifier modifier)
    {
        return boneModifiers.remove(modifier);
    }

    /** 清除所有已注册的骨骼修改回调（关闭 GUI 时调用）。 */
    public void clearBoneModifiers()
    {
        boneModifiers.clear();
    }

    // ── GeoRenderer 覆写 ─────────────────────────────────────────────────────

    /**
     * 本类唯一覆写的 GeoRenderer 方法。将所有注册的 {@link BoneModifier}
     * 依次派发，外部无需再继承或覆写此方法。
     */
    @Override
    @SuppressWarnings("unchecked")  // BoneModifier 用 RenderPassInfo<?>，向上转型安全
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots)
    {
        for (BoneModifier modifier : boneModifiers)
        {
            modifier.modify(renderPassInfo, snapshots);
        }
    }
}


