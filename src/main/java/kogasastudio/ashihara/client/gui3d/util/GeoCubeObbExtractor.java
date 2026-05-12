package kogasastudio.ashihara.client.gui3d.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;

public final class GeoCubeObbExtractor
{
    private GeoCubeObbExtractor()
    {
    }

    public static OBB extract(Matrix4f bonePose, GeoCube cube)
    {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.mulPose(new Matrix4f(bonePose));
        cube.translateToPivotPoint(poseStack);
        cube.rotate(poseStack);
        cube.translateAwayFromPivotPoint(poseStack);
        Matrix4f cubePose = new Matrix4f(poseStack.last().pose());
        poseStack.popPose();

        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        boolean hasVertices = false;

        for (GeoQuad quad : cube.quads())
        {
            if (quad == null)
            {
                continue;
            }

            for (GeoVertex vertex : quad.vertices())
            {
                float px = vertex.posX();
                float py = vertex.posY();
                float pz = vertex.posZ();
                min.x = Math.min(min.x, px);
                min.y = Math.min(min.y, py);
                min.z = Math.min(min.z, pz);
                max.x = Math.max(max.x, px);
                max.y = Math.max(max.y, py);
                max.z = Math.max(max.z, pz);
                hasVertices = true;
            }
        }

        if (!hasVertices)
        {
            Vector3f half = new Vector3f((float) cube.size().x() / 32.0f, (float) cube.size().y() / 32.0f, (float) cube.size().z() / 32.0f);
            min.set(-half.x, -half.y, -half.z);
            max.set(half.x, half.y, half.z);
        }

        return new OBB(new Vector3f(), min, max, cubePose);
    }
}

