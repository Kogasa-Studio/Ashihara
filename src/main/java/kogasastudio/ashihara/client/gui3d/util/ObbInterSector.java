package kogasastudio.ashihara.client.gui3d.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ObbInterSector
{
    public static float rayOBBIntersect(Ray ray, OBB obb)
    {
        Matrix4f invPose = new Matrix4f(obb.pose()).invert();

        Vector4f originWorld = new Vector4f(ray.origin(), 1.0f);  // Homogeneous coordinates (w==1)
        Vector4f originLocal = new Vector4f(originWorld).mul(invPose);  // local homogeneous coordinates
        Vector3f rayOriginLocal = new Vector3f(originLocal.x, originLocal.y, originLocal.z).div(originLocal.w);  // Dehomogenize (make sure w!=0)

        Vector4f dirWorld = new Vector4f(ray.direction(), 0.0f);  // Homogeneous coordinates (w==0)
        Vector4f dirLocal = new Vector4f(dirWorld).mul(invPose);  // local direction
        Vector3f rayDirLocal = new Vector3f(dirLocal.x, dirLocal.y, dirLocal.z).normalize();

        return 0f;
    }
}
