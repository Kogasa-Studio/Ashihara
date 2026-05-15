package kogasastudio.ashihara.client.gui3d.components;

import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public class PotModelComponent extends ModelComponent
{
    protected boolean lidRemoved = false;
    protected final float modelScale;

    public PotModelComponent(float centerX, float centerY, float modelScale)
    {
        super(
            new SimpleInternalControlGeoModel("block/pot", "textures/block/pot.png", Minecraft.getInstance().player),
            true,
            new Matrix4f()
                .translation(centerX, centerY, 40)
                .scale(modelScale, modelScale, modelScale)
                .rotateX((float) Math.toRadians(-58.0f))
                .rotateY((float) Math.toRadians(-35.0f))
        );
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
        float targetYOffset = this.lidRemoved ? 4.0f : 0.0f;
        float targetRotation = this.lidRemoved ? (float) Math.toRadians(-20.0f) : 0.0f;
        float currentYOffset = this.boneTracers.get("lid").snapshot().getTranslateY();//this.model.getBone("lid").map(GeoBone::getPosY).orElse(0.0f);
        float currentRotation = this.boneTracers.get("lid").snapshot().getRotZ();//this.model.getBone("lid").map(GeoBone::getRotZ).orElse(0.0f);

        this.model.triggerInternal
        (
            Minecraft.getInstance().player,
            this.model.hashCode(),
            new InternalControlGeoModel.InternalAnimationBuilder("pot_lid_toggle", LoopType.HOLD_ON_LAST_FRAME)
                .startBone("lid")
                .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 8 / 20f, currentYOffset, targetYOffset, EasingType.EASE_OUT_CUBIC)
                .lerpZ(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 8 / 20f, currentRotation, targetRotation, EasingType.EASE_OUT_CUBIC)
                .endBone()
                .build()
        );
        return this.lidRemoved;
    }
}

