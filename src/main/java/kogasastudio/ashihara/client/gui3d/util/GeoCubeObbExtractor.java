package kogasastudio.ashihara.client.gui3d.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtil;

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
        RenderUtil.translateToPivotPoint(poseStack, cube);
        RenderUtil.rotateMatrixAroundCube(poseStack, cube);
        RenderUtil.translateAwayFromPivotPoint(poseStack, cube);
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
                Vector3f p = vertex.position();
                min.x = Math.min(min.x, p.x);
                min.y = Math.min(min.y, p.y);
                min.z = Math.min(min.z, p.z);
                max.x = Math.max(max.x, p.x);
                max.y = Math.max(max.y, p.y);
                max.z = Math.max(max.z, p.z);
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

