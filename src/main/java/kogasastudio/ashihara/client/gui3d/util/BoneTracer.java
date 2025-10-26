package kogasastudio.ashihara.client.gui3d.util;

import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.function.Predicate;

public class BoneTracer
{
    protected Matrix4f matrix = new Matrix4f();
    protected final Predicate<GeoBone> bone;

    public BoneTracer(Predicate<GeoBone> bone) {this.bone = bone;}

    public boolean testBone(GeoBone bone) {return this.bone.test(bone);}

    public void syncMatrix(Matrix4f matrix) {this.matrix = matrix;}

    public Matrix4f matrix() {return this.matrix;}
}
