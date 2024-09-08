package kogasastudio.ashihara.helper;

public class PositionHelper
{
    /**
     * 将像素坐标转换为方块内坐标
     *
     * @param pixels 像素坐标数值, 如5, 13
     * @return 方块内坐标数值, 如0.625,  0.25
     */
    public static float XTP(float pixels)
    {
        return pixels / 16f;
    }

    /**
     * 将方块内坐标转换为像素坐标
     */
    public static double PTX(double pos)
    {
        return pos * 16d;
    }

    /**
     * 将世界内坐标转换为方块内坐标
     */
    public static double ATP(double absolutePos)
    {
        return absolutePos - Math.abs(absolutePos);
    }

    /**
     * 将世界内坐标转换为像素坐标
     */
    public static double ATX(double absolutePos)
    {
        return PTX(ATP(absolutePos));
    }
}
