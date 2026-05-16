package kogasastudio.ashihara.client.gui3d.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

public class Transformation
{
    public float[] translate = {0, 0, 0};
    public float[] rotate = {0, 0, 0};
    public float[] scale = {0, 0, 0};

    public Transformation translate(float x, float y, float z)
    {
        translate[0] = x;
        translate[1] = y;
        translate[2] = z;
        return this;
    }

    public Transformation rotate(float x, float y, float z)
    {
        rotate[0] = x;
        rotate[1] = y;
        rotate[2] = z;
        return this;
    }

    public Transformation scale(float x, float y, float z)
    {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
        return this;
    }

    public Matrix4f getTransform()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(translate[0], translate[1], translate[2]);
        matrix4f.scale(scale[0], scale[1], scale[2]);
        matrix4f.rotateXYZ(rotate[0], rotate[1], rotate[2]);
        return matrix4f;
    }

    public void applyTo(PoseStack poseStack)
    {
        poseStack.translate(translate[0], translate[1], translate[2]);
        poseStack.scale(scale[0], scale[1], scale[2]);
        poseStack.mulPose(Axis.XP.rotation(rotate[0]));
        poseStack.mulPose(Axis.YP.rotation(rotate[1]));
        poseStack.mulPose(Axis.ZP.rotation(rotate[2]));
    }
}
