package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.ProgressBarModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class ProgressBarComponent extends ModelComponent
{
    protected final ProgressBarModel model;
    public Supplier<Float> progress;
    @Nullable
    public Supplier<Matrix4f> additionalTransform;

    public ProgressBarComponent(ProgressBarModel model)
    {
        super(model, true);
        this.model = model;
    }

    public ProgressBarComponent withProgress(Supplier<Float> progress)
    {
        this.progress = progress;
        return this;
    }

    public ProgressBarComponent withTransform(Supplier<Matrix4f> transform)
    {
        this.additionalTransform = transform;
        return this;
    }

    @Override
    public void init()
    {
        super.init();
        this.model.init(Minecraft.getInstance().player);
    }

    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        if (!this.renderModel) return;
        if (this.progress != null) this.model.setProgress(this.progress.get());

        output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
        {
            poseStack.pushPose();
            if (this.additionalTransform != null && this.additionalTransform.get() != null) poseStack.last().pose().set(this.additionalTransform.get());
            this.model.RENDERER.performRenderPass(
                this.model, null,
                poseStack, submitNodeCollector,
                new CameraRenderState(),
                15728880,
                partialTick
            );
            poseStack.popPose();
        }, true));

        super.collectSelfRenderStates(output, mouseX, mouseY, partialTick);
    }
}
