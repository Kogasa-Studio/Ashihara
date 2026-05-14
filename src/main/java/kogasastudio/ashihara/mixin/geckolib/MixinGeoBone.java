package kogasastudio.ashihara.mixin.geckolib;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.utils.mixin.GeoRendererPoseSyncProvider;
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
 * 此时 poseStack 已由 {@code RenderUtil.prepMatrixForBoneAndUpdateListeners}
 * 完整累积（pivot + BoneSnapshot 旋转/缩放/位移 + 父链变换），
 * 可直接用于 OBB 构建。
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
        for (BoneTracer tracer : tracers)
        {
            if (tracer.testBone(bone))
            {
                // poseStack.last().pose() 是此骨骼从模型根到自身的完整累积 Matrix4f。
                // BoneTracer.syncFromBone 内部会拷贝，无需在此处额外 new Matrix4f。
                tracer.syncFromBone(bone, poseStack.last().pose());
            }
        }
    }
}

