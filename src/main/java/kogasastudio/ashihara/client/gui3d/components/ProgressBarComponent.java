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
    public Supplier<OBB> obbSupplier;

    public ProgressBarComponent(ProgressBarModel model)
    {
        super(model, true);
        this.model = model;
    }

    public ProgressBarComponent withOBB(Supplier<OBB> obbSupplier)
    {
        this.obbSupplier = obbSupplier;
        return this;
    }

    public ProgressBarComponent withProgress(Supplier<Float> progress)
    {
        this.progress = progress;
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

        if (this.obbSupplier != null)
        {
            OBB obb = this.obbSupplier.get();
            if (obb != null)
            {
                output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
                {
                    poseStack.pushPose();
                    poseStack.last().pose().set(obb.pose());
                    Vector3f t = new Vector3f(obb.maxXYZ()).min(obb.minXYZ());
                    poseStack.translate(t.x(), t.y(), t.z());
                    this.model.RENDERER.performRenderPass(
                        this.model, null,
                        poseStack, submitNodeCollector,
                        new CameraRenderState(),
                        15728880,
                        partialTick
                    );
                    poseStack.popPose();
                }, true));
                return;
            }
        }

        super.collectSelfRenderStates(output, mouseX, mouseY, partialTick);
    }
}
