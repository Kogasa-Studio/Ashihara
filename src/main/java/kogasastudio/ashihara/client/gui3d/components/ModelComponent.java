package kogasastudio.ashihara.client.gui3d.components;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
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

    public List<OBB> getBoneCollisionBoxes(Collection<String> boneNames)
    {
        List<OBB> boxes = new ArrayList<>();
        for (String boneName : boneNames)
        {
            boxes.addAll(this.bindBone(boneName).collisionBoxes());
        }
        return boxes;
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
}
