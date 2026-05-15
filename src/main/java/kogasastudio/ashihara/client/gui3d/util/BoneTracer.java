package kogasastudio.ashihara.client.gui3d.util;

import com.geckolib.animation.state.BoneSnapshot;
import org.joml.Matrix4f;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.cache.model.cuboid.GeoCube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BoneTracer
{
    protected Matrix4f matrix = new Matrix4f();
    protected final Predicate<GeoBone> bone;
    protected final List<OBB> collisionBoxes = new ArrayList<>();
    protected BoneSnapshot boneSnapshot;

    public BoneTracer(Predicate<GeoBone> bone) {this.bone = bone;}

    public boolean testBone(GeoBone bone) {return this.bone.test(bone);}

    public void syncMatrix(Matrix4f matrix)
    {
        this.matrix = matrix;
    }

    public void syncFromBone(GeoBone bone, Matrix4f matrix)
    {
        this.matrix = new Matrix4f(matrix);
        this.boneSnapshot = bone.frameSnapshot == null ? BoneSnapshot.create(bone) : bone.frameSnapshot;
        this.collisionBoxes.clear();

        if (bone instanceof CuboidGeoBone cuboidBone)
        {
            for (GeoCube cube : cuboidBone.cubes)
            {
                this.collisionBoxes.add(GeoCubeObbExtractor.extract(this.matrix, cube));
            }
        }
    }

    public BoneSnapshot snapshot() {return this.boneSnapshot;}

    public Matrix4f matrix() {return this.matrix;}

    public List<OBB> collisionBoxes()
    {
        return Collections.unmodifiableList(this.collisionBoxes);
    }
}
