package kogasastudio.ashihara.client.gui3d.interaction;

/**
 * 定义虚拟3D交互层中，组件对射线命中事件的响应策略（选项1.C）。
 *
 * <p>排序规则：对同一帧内的所有命中结果按 t 升序（最近优先）处理：
 * <ol>
 *   <li>遇到 {@link #BLOCK} → 停止，该组件消耗事件。</li>
 *   <li>遇到 {@link #PENETRATE} → 记录悬停状态，事件继续向后传递。</li>
 *   <li>遇到 {@link #MIXED} → 由组件自行通过 {@code isPenetrating()} 决定。</li>
 * </ol>
 */
public enum HitPolicy
{
    /**
     * 阻挡：命中后吸收事件，不再向后传递。
     * 默认值，适用于按钮、槽位、实体交互区域等。
     */
    BLOCK,

    /**
     * 穿透：命中会更新悬停状态，但点击/拖拽事件继续向后传递。
     * 适用于半透明背景、装饰层、遮罩区域等。
     */
    PENETRATE,

    /**
     * 混合：组件需覆写 {@code isPenetrating()} 方法动态决定行为。
     * 适用于根据状态切换穿透/阻挡的复杂交互组件。
     */
    MIXED
}
