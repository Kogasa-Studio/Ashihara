package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.models.geo.PotModel;
import org.joml.Matrix4f;

public class PotModelComponent extends ModelComponent
{
    protected boolean lidRemoved = false;
    protected final float modelScale;

    public PotModelComponent(PotModel model, float centerX, float centerY, float modelScale)
    {
        this(model, modelScale,
             new Matrix4f()
             .translation(centerX, centerY, 40)
             .scale(modelScale, modelScale, modelScale)
             .rotateX((float) Math.toRadians(-58.0f))
             .rotateY((float) Math.toRadians(-35.0f)));
    }

    public PotModelComponent(PotModel model, float modelScale, Matrix4f presetTransform)
    {
        super(model, true, presetTransform);
        this.modelScale = modelScale;
        // 初始化关键骨骼追踪
        this.bindBone("main");
    }

    public boolean isLidRemoved()
    {
        return this.lidRemoved;
    }

    public boolean toggleLid()
    {
        this.lidRemoved = !this.lidRemoved;
        String anim = this.lidRemoved ? PotModel.LID_OPEN : PotModel.LID_CLOSE;
        this.model.triggerAnim(this.model.player, this.model.hashCode(), PotModel.LID_STATE_CONTROLLER, anim);
        return this.lidRemoved;
    }
}

