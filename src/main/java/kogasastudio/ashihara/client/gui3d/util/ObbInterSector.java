package kogasastudio.ashihara.client.gui3d.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ObbInterSector
{
    public static float rayOBBIntersect(Ray ray, OBB obb)
    {
        Matrix4f invPose = new Matrix4f(obb.pose()).invert();

        if (!invPose.isFinite())
        {
            return -1;
        }

        Vector4f originWorld = new Vector4f(ray.origin(), 1.0f);  // Homogeneous coordinates (w==1)
        Vector4f originLocal = new Vector4f(originWorld).mul(invPose);  // local homogeneous coordinates
        if (!Float.isFinite(originLocal.w) || Math.abs(originLocal.w) < 1e-6f)
        {
            return -1;
        }
        Vector3f rayOriginLocal = new Vector3f(originLocal.x, originLocal.y, originLocal.z).div(originLocal.w);  // Dehomogenize (make sure w!=0)

        Vector4f dirWorld = new Vector4f(ray.direction(), 0.0f);  // Homogeneous coordinates (w==0)
        Vector4f dirLocal = new Vector4f(dirWorld).mul(invPose);  // local direction
        // Normalise for numerical stability in the slab test, then divide result back to world-space.
        // t_world = t_normalised / |dirUnscaled|
        Vector3f dirUnscaled  = new Vector3f(dirLocal.x, dirLocal.y, dirLocal.z);
        float    dirScale     = dirUnscaled.length();
        if (dirScale < 1e-12f) return -1;
        Vector3f rayDirLocal  = dirUnscaled.div(dirScale);
        Vector3f aabbMin = obb.minXYZ();
        Vector3f aabbMax = obb.maxXYZ();

        // 5. 对局部AABB应用Slabs算法计算相交
        float tMin = -Float.MAX_VALUE;
        float tMax = Float.MAX_VALUE;

        // 检查X轴
        if (Math.abs(rayDirLocal.x) < 1e-6f) {  // 射线平行于X轴
            if (rayOriginLocal.x < aabbMin.x || rayOriginLocal.x > aabbMax.x) {
                return -1;  // 原点不在X范围内，无交集
            }
        } else {
            float t1 = (aabbMin.x - rayOriginLocal.x) / rayDirLocal.x;
            float t2 = (aabbMax.x - rayOriginLocal.x) / rayDirLocal.x;
            if (t1 > t2) {  // 确保t1是较小值
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);  // 进入AABB的最大t
            tMax = Math.min(tMax, t2);  // 离开AABB的最小t
        }

        // 检查Y轴（同X轴逻辑）
        if (Math.abs(rayDirLocal.y) < 1e-6f) {
            if (rayOriginLocal.y < aabbMin.y || rayOriginLocal.y > aabbMax.y) {
                return -1;
            }
        } else {
            float t1 = (aabbMin.y - rayOriginLocal.y) / rayDirLocal.y;
            float t2 = (aabbMax.y - rayOriginLocal.y) / rayDirLocal.y;
            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
        }

        // 检查Z轴（同X轴逻辑）
        if (Math.abs(rayDirLocal.z) < 1e-6f) {
            if (rayOriginLocal.z < aabbMin.z || rayOriginLocal.z > aabbMax.z) {
                return -1;
            }
        } else {
            float t1 = (aabbMin.z - rayOriginLocal.z) / rayDirLocal.z;
            float t2 = (aabbMax.z - rayOriginLocal.z) / rayDirLocal.z;
            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
        }

        // 6. 判断有效交集（t>0表示在射线前方）
        if (tMin < tMax && tMax > 0) {
            return Math.max(tMin, 0f) / dirScale;  // Convert local t back to world-space t
        }
        return -1;
    }
}
