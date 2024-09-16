package kogasastudio.ashihara.helper;

import net.minecraft.core.Direction;

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

    public static boolean coordsInRangeFixedX(Direction clickDir, double coords, double min, double max)
    {
        if (clickDir == Direction.EAST) return (coords >= min && coords < max);
        else return (coords > min && coords <= max);
    }

    public static boolean coordsInRangeFixedY(Direction clickDir, double coords, double min, double max)
    {
        if (clickDir == Direction.UP) return (coords >= min && coords < max);
        else return (coords > min && coords <= max);
    }

    public static boolean coordsInRangeFixedZ(Direction clickDir, double coords, double min, double max)
    {
        if (clickDir == Direction.SOUTH) return (coords >= min && coords < max);
        else return (coords > min && coords <= max);
    }

    /**
     * 将斜向的旋转角度化整为45的倍数
     */
    public static float transformObliqueDegree(float degrees)
    {
        do {degrees += 360;} while (degrees < 0);
        do {degrees -= 360;} while (degrees > 360);

        if (degrees >= 0 && degrees < 90) return 45;
        else if (degrees >= 90 && degrees < 180) return 135;
        else if (degrees >= 180 && degrees < 270) return 225;
        else return 315;
    }
}
