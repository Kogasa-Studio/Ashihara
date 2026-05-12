package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import com.geckolib.animation.AnimationController;

public class SelectionFrameModel extends SimpleInternalControlGeoModel
{
    public SelectionFrameModel(String modelPrefix, String texturePrefix, Player player)
    {
        super(modelPrefix, texturePrefix, player);
    }

    /**
     * 将12根棱骨骼的位置和缩放全部同步到指定边界（不触发动画，直接赋值）。
     * 用于零初始化（min=max=zero）或立即跳变到目标状态。
     */
    public void syncFrame(Vector3f min, Vector3f max)
    {
        syncPositionsOnly(min, max);
        float xs = max.x - min.x;
        float ys = max.y - min.y;
        float zs = max.z - min.z;
        /*for (String b : X_BONES) getBone(b).ifPresent(bone -> { bone.setScaleX(xs); bone.setScaleY(0); bone.setScaleZ(0); });
        for (String b : Y_BONES) getBone(b).ifPresent(bone -> { bone.setScaleX(0); bone.setScaleY(ys); bone.setScaleZ(0); });
        for (String b : Z_BONES) getBone(b).ifPresent(bone -> { bone.setScaleX(0); bone.setScaleY(0); bone.setScaleZ(zs); });*/
    }

    /**
     * 仅同步12根棱骨骼的位置到各自边的中点，不碰scale。
     * GeckoLib 单轴缩放以 pivot 为中心向两侧对称扩展，因此 pivot 必须位于棱的中点：
     *   X骨骼：PosX = x + xs/2，YZ锚定角点
     *   Y骨骼：PosY = y + ys/2，XZ锚定角点
     *   Z骨骼：PosZ = z + zs/2，XY锚定角点
     * 配合 triggerFrameAnimation 使用时，位置会在动画中以常量keyframe固定。
     */
    public void syncPositionsOnly(Vector3f min, Vector3f max)
    {
        float x = min.x, y = min.y, z = min.z;
        float xs = max.x - x, ys = max.y - y, zs = max.z - z;

        // x-parallel bones：沿X轴延伸，PosX置于X中点，YZ锚定角点
        /*getBone("x_pp").ifPresent(b -> { b.setPosX(x + xs/2); b.setPosY(y + ys); b.setPosZ(z + zs); });
        getBone("x_pn").ifPresent(b -> { b.setPosX(x + xs/2); b.setPosY(y + ys); b.setPosZ(z);      });
        getBone("x_np").ifPresent(b -> { b.setPosX(x + xs/2); b.setPosY(y);      b.setPosZ(z + zs); });
        getBone("x_nn").ifPresent(b -> { b.setPosX(x + xs/2); b.setPosY(y);      b.setPosZ(z);      });

        // y-parallel bones：沿Y轴延伸，PosY置于Y中点，XZ锚定角点
        getBone("y_pp").ifPresent(b -> { b.setPosX(x + xs); b.setPosY(y + ys/2); b.setPosZ(z + zs); });
        getBone("y_pn").ifPresent(b -> { b.setPosX(x + xs); b.setPosY(y + ys/2); b.setPosZ(z);      });
        getBone("y_np").ifPresent(b -> { b.setPosX(x);      b.setPosY(y + ys/2); b.setPosZ(z + zs); });
        getBone("y_nn").ifPresent(b -> { b.setPosX(x);      b.setPosY(y + ys/2); b.setPosZ(z);      });

        // z-parallel bones：沿Z轴延伸，PosZ置于Z中点，XY锚定角点
        getBone("z_pp").ifPresent(b -> { b.setPosX(x + xs); b.setPosY(y + ys); b.setPosZ(z + zs/2); });
        getBone("z_pn").ifPresent(b -> { b.setPosX(x + xs); b.setPosY(y);      b.setPosZ(z + zs/2); });
        getBone("z_np").ifPresent(b -> { b.setPosX(x);      b.setPosY(y + ys); b.setPosZ(z + zs/2); });
        getBone("z_nn").ifPresent(b -> { b.setPosX(x);      b.setPosY(y);      b.setPosZ(z + zs/2); });*/
    }

    /**
     * 读取12根棱当前的实时缩放值，用于动画打断时的起始尺寸。
     * 返回 float[3]：{xScale, yScale, zScale}
     */
    public float[] readCurrentEdgeScales()
    {
        float xs = 0;//getBone("x_nn").map(b -> b.getScaleY()).orElse(0f);
        float ys = 0;//getBone("y_nn").map(b -> b.getScaleZ()).orElse(0f);
        float zs = 0;//getBone("z_nn").map(b -> b.getScaleX()).orElse(0f);
        return new float[]{ xs, ys, zs };
    }

    /**
     * 触发骨骼位置+缩放动画。每次触发均从 fromScale 插值到 toScale，
     * 位置以常量keyframe锁定OBB角点，防止GeckoLib帧间reset到初始快照。
     * 使用 HOLD_ON_LAST_FRAME 确保动画完成后骨骼保持在目标值。
     */
    public void triggerFrameAnimation(
        Vector3f min, Vector3f max,
        float xFrom, float yFrom, float zFrom,
        float xTo,   float yTo,   float zTo,
        double duration, EasingType easing)
    {
        float x = min.x, y = min.y, z = min.z;
        float xs = max.x - x, ys = max.y - y, zs = max.z - z;
        float xs_fixed = xs + 2 * yTo, ys_fixed = ys + 2 * zTo, zs_fixed = zs + 2 * zTo;

        var SCALE = InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE;
        var POS   = InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION;

        var builder = new InternalControlGeoModel.InternalAnimationBuilder("frame_anim", LoopType.HOLD_ON_LAST_FRAME);

        // x-parallel bones：PosX锁定X中点，YZ锁定角点
        builder.startBone("x_pp").lerpSingle(POS, duration, x+xs/2, x+xs/2, y+ys, y+ys, z+zs, z+zs, EasingType.STEP).lerpSingle(SCALE, duration, xs_fixed, xs_fixed, yFrom, yTo, zFrom, zTo, easing).endBone();
        builder.startBone("x_pn").lerpSingle(POS, duration, x+xs/2, x+xs/2, y+ys, y+ys, z,    z,    EasingType.STEP).lerpSingle(SCALE, duration, xs_fixed, xs_fixed, yFrom, yTo, zFrom, zTo, easing).endBone();
        builder.startBone("x_np").lerpSingle(POS, duration, x+xs/2, x+xs/2, y,    y,    z+zs, z+zs, EasingType.STEP).lerpSingle(SCALE, duration, xs_fixed, xs_fixed, yFrom, yTo, zFrom, zTo, easing).endBone();
        builder.startBone("x_nn").lerpSingle(POS, duration, x+xs/2, x+xs/2, y,    y,    z,    z,    EasingType.STEP).lerpSingle(SCALE, duration, xs_fixed, xs_fixed, yFrom, yTo, zFrom, zTo, easing).endBone();

        // y-parallel bones：PosY锁定Y中点，XZ锁定角点
        builder.startBone("y_pp").lerpSingle(POS, duration, x+xs, x+xs, y+ys/2, y+ys/2, z+zs, z+zs, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, ys_fixed, ys_fixed, zFrom, zTo, easing).endBone();
        builder.startBone("y_pn").lerpSingle(POS, duration, x+xs, x+xs, y+ys/2, y+ys/2, z,    z,    EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, ys_fixed, ys_fixed, zFrom, zTo, easing).endBone();
        builder.startBone("y_np").lerpSingle(POS, duration, x,    x,    y+ys/2, y+ys/2, z+zs, z+zs, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, ys_fixed, ys_fixed, zFrom, zTo, easing).endBone();
        builder.startBone("y_nn").lerpSingle(POS, duration, x,    x,    y+ys/2, y+ys/2, z,    z,    EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, ys_fixed, ys_fixed, zFrom, zTo, easing).endBone();

        // z-parallel bones：PosZ锁定Z中点，XY锁定角点
        builder.startBone("z_pp").lerpSingle(POS, duration, x+xs, x+xs, y+ys, y+ys, z+zs/2, z+zs/2, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, yFrom, yTo, zs_fixed, zs_fixed, easing).endBone();
        builder.startBone("z_pn").lerpSingle(POS, duration, x+xs, x+xs, y,    y,    z+zs/2, z+zs/2, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, yFrom, yTo, zs_fixed, zs_fixed, easing).endBone();
        builder.startBone("z_np").lerpSingle(POS, duration, x,    x,    y+ys, y+ys, z+zs/2, z+zs/2, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, yFrom, yTo, zs_fixed, zs_fixed, easing).endBone();
        builder.startBone("z_nn").lerpSingle(POS, duration, x,    x,    y,    y,    z+zs/2, z+zs/2, EasingType.STEP).lerpSingle(SCALE, duration, xFrom, xTo, yFrom, yTo, zs_fixed, zs_fixed, easing).endBone();

        this.triggerInternal(this.player, this.hashCode(), builder.build());
    }

    /**
     * 检查 "internal" 控制器的动画是否已播放完毕（用于检测收缩结束）。
     */
    public boolean isInternalAnimFinished()
    {
        var cache = getAnimatableInstanceCache();
        if (cache == null) return false;
        var manager = cache.getManagerForId(this.hashCode());
        if (manager == null) return false;
        AnimationController<?> controller = manager.getAnimationControllers().get("internal");
        if (controller == null) return false;
        return controller.hasAnimationFinished();
    }

    // ---- constants ----
    private static final String[] X_BONES = {"x_pp", "x_pn", "x_np", "x_nn"};
    private static final String[] Y_BONES = {"y_pp", "y_pn", "y_np", "y_nn"};
    private static final String[] Z_BONES = {"z_pp", "z_pn", "z_np", "z_nn"};
}
