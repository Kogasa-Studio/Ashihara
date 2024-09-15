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

    public static boolean coordsInRangeFixed(Direction clickDir, double coords, double min, double max)
    {
        Direction includesMin = switch (clickDir.getAxis())
        {
            case Y -> Direction.UP;
            case Z -> Direction.SOUTH;
            default -> Direction.EAST;
        };
        if (clickDir == includesMin) return (coords >= min && coords < max);
        else return (coords > min && coords <= max);
    }
}
