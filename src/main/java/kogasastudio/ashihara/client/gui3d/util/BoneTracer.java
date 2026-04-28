package kogasastudio.ashihara.client.gui3d.util;

import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BoneTracer
{
    protected Matrix4f matrix = new Matrix4f();
    protected final Predicate<GeoBone> bone;
    protected final List<OBB> collisionBoxes = new ArrayList<>();

    public BoneTracer(Predicate<GeoBone> bone) {this.bone = bone;}

    public boolean testBone(GeoBone bone) {return this.bone.test(bone);}

    public void syncMatrix(Matrix4f matrix)
    {
        this.matrix = matrix;
    }

    public void syncFromBone(GeoBone bone, Matrix4f matrix)
    {
        this.matrix = new Matrix4f(matrix);
        this.collisionBoxes.clear();

        for (GeoCube cube : bone.getCubes())
        {
            this.collisionBoxes.add(GeoCubeObbExtractor.extract(this.matrix, cube));
        }
    }

    public Matrix4f matrix() {return this.matrix;}

    public List<OBB> collisionBoxes()
    {
        return Collections.unmodifiableList(this.collisionBoxes);
    }
}
