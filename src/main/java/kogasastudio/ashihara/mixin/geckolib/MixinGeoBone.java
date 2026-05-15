package kogasastudio.ashihara.mixin.geckolib;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.utils.mixin.GeoRendererPoseSyncProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 为每一根骨骼在其变换矩阵完整建立后，将 PoseStack 快照同步给
 * 附加到该渲染器的所有 BoneTracer。
 *
 * <p>注入点：{@code GeoBone.updateBonePositionListeners} 方法头部。
 * 该时机位于 {@code RenderUtil.prepMatrixForBoneAndUpdateListeners} 的
 * {@code bone.translateAwayFromPivotPoint(...)} 之前，因此这里需要手动补偿
 * 一次 pivot-away，才能得到与实际 cube 渲染一致的骨骼基矩阵。
 *
 * <p>对普通渲染对象（未附加 BoneTracer），tracer 列表为空，本注入体
 * 在检查后立即返回，实际开销为零。
 */
@Mixin(value = GeoBone.class, remap = false)
public abstract class MixinGeoBone
{
    @Inject(
        method = "updateBonePositionListeners",
        at = @At("HEAD"),
        remap = false
    )
    private void syncToTracers(PoseStack poseStack, RenderPassInfo<?> renderPassInfo, CallbackInfo ci)
    {
        GeoRenderer<?, ?, ?> renderer = renderPassInfo.renderer();
        if (!(renderer instanceof GeoRendererPoseSyncProvider syncProvider)) return;

        List<BoneTracer> tracers = syncProvider.ashihara_1_21$getTracers();
        if (tracers.isEmpty()) return;

        GeoBone bone = (GeoBone) (Object) this;
        Matrix4f correctedBonePose = new Matrix4f(poseStack.last().pose())
            .translate(-bone.pivotX() / 16.0f, -bone.pivotY() / 16.0f, -bone.pivotZ() / 16.0f);

        for (BoneTracer tracer : tracers)
        {
            if (tracer.testBone(bone))
            {
                tracer.syncFromBone(bone, correctedBonePose);
            }
        }
    }
}

