package kogasastudio.ashihara.helper;

public class MathHelper
{
    public static double simplifyDouble(Double d, int bit)
    {
        return Math.round(d * Math.pow(10d, bit)) / Math.pow(10d, bit);
    }

    /**
     * 将平面上某给定点绕某一枢轴旋转一定角度
     * @param x 给定点的x坐标
     * @param z 给定点的z坐标
     * @param pivotX 枢轴x坐标
     * @param pivotZ 枢轴z坐标
     * @param rotation 旋转的角度，需为弧度制
     * @return 格式为{x, z}的旋转后点坐标
     */
    public static double[] rotatePoint(double x, double z, double pivotX, double pivotZ, double rotation)
    {
        double nX = (x - pivotX) * Math.cos(rotation) - (z - pivotZ) * Math.sin(rotation) + pivotX;
        double nZ = (z - pivotZ) * Math.cos(rotation) + (x - pivotX) * Math.sin(rotation) + pivotZ;
        return new double[]{nX, nZ};
    }
}
