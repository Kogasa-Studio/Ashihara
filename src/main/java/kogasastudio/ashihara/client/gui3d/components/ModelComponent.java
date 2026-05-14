package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.render.geo.GUI3DObjectRenderer;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;

import java.util.*;

public class ModelComponent extends AbstractComponent
{
    protected final SimpleInternalControlGeoModel model;
    public final Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();
    public final Set<BoneTracer> attachedTracers = new HashSet<>();
    public boolean tracerAttached = false;
    public boolean renderModel = false;
    public Matrix4f presetTransform = new Matrix4f();

    public ModelComponent(SimpleInternalControlGeoModel model)
    {
        this.model = model;
    }

    public ModelComponent(SimpleInternalControlGeoModel model, boolean renderModel)
    {
        this(model);
        this.renderModel = renderModel;
    }

    public ModelComponent(SimpleInternalControlGeoModel model, boolean renderModel, Matrix4f presetTransform)
    {
        this(model, renderModel);
        this.presetTransform = presetTransform;
    }

    public SimpleInternalControlGeoModel getModel()
    {
        return model;
    }

    public Matrix4f getPresetTransform()
    {
        return presetTransform;
    }

    public void applyPreset(Matrix4f transform)
    {
        this.presetTransform = transform;
    }

    @Override
    public void init()
    {
        if (!this.tracerAttached)
        {
            this.boneTracers.values().forEach(tracer -> this.model.getRendererPoseSync().ashihara_1_21$addTracer(tracer));
            this.attachedTracers.addAll(this.boneTracers.values());
            this.tracerAttached = true;
        }
        super.init();
    }

    @Override
    protected void renderSelf(GUI3DComponentRenderState guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderSelf(guiGraphics, mouseX, mouseY, partialTick);
        //if (renderModel) renderModel(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        if (!this.renderModel)
        {
            return;
        }

        output.add(
            GUI3DComponentRenderState.of
            (
                this.model,
                this.model.RENDERER,
                null,
                new CameraRenderState(),
                0xF000F0,
                partialTick
            )
        );
    }

    public List<OBB> getBoneCollisionBoxes(String boneName)
    {
        return new ArrayList<>(this.bindBone(boneName).collisionBoxes());
    }

    public BoneTracer bindBone(String boneName)
    {
        BoneTracer tracer = this.boneTracers.computeIfAbsent(boneName, name -> new BoneTracer(bone -> bone.name().equals(name)));
        if (this.tracerAttached && this.attachedTracers.add(tracer))
        {
            this.model.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
        }
        return tracer;
    }

    // ── 骨骼修改（写）──────────────────────────────────────────────────────────

    /**
     * 注册一个骨骼修改回调，每帧动画计算完毕后调用。
     *
     * <p>可在 Screen 关闭时通过 {@link #clearBoneModifiers()} 批量清除，
     * 或通过 {@link #removeBoneModifier(GUI3DObjectRenderer.BoneModifier)} 单独移除。
     *
     * <p>示例：
     * <pre>{@code
     * comp.addBoneModifier((renderPassInfo, snapshots) ->
     *     snapshots.ifPresent("my_bone", snap -> snap.setRotX(angle))
     * );
     * }</pre>
     */
    public void addBoneModifier(GUI3DObjectRenderer.BoneModifier modifier)
    {
        this.model.addBoneModifier(modifier);
    }

    /** 移除单个骨骼修改回调。 */
    public boolean removeBoneModifier(GUI3DObjectRenderer.BoneModifier modifier)
    {
        return this.model.removeBoneModifier(modifier);
    }

    /** 清除所有骨骼修改回调（Screen 关闭时调用）。 */
    public void clearBoneModifiers()
    {
        this.model.clearBoneModifiers();
    }
}
