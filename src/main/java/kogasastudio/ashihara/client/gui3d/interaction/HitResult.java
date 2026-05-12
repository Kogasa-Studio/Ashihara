package kogasastudio.ashihara.client.gui3d.interaction;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/**
 * 虚拟3D拾取结果（选项1.C）。
 *
 * @param component  被命中的组件，{@code null} 表示未命中任何组件
 * @param t          射线参数（原点到命中点的距离），{@code -1} 表示未命中
 * @param hitPoint   世界坐标系中的命中点
 * @param policy     命中时采用的策略
 */
public record HitResult(
    @Nullable AbstractComponent component,
    float t,
    Vector3f hitPoint,
    HitPolicy policy
)
{
    /** 未命中单例，避免频繁构造。 */
    public static final HitResult MISS = new HitResult(null, -1f, new Vector3f(), HitPolicy.BLOCK);

    /** 是否有效命中（命中了某个组件且 t >= 0）。 */
    public boolean isHit()
    {
        return component != null && t >= 0f;
    }

    /**
     * 构造命中结果，自动计算命中点。
     *
     * @param component 被命中的组件
     * @param t         射线参数
     * @param rayOrigin 射线起点
     * @param rayDir    射线方向
     * @param policy    命中策略
     */
    public static HitResult of(
        AbstractComponent component,
        float t,
        Vector3f rayOrigin,
        Vector3f rayDir,
        HitPolicy policy
    )
    {
        Vector3f hitPoint = new Vector3f(rayDir).mul(t).add(rayOrigin);
        return new HitResult(component, t, hitPoint, policy);
    }
}
