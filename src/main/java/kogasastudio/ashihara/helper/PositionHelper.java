package kogasastudio.ashihara.helper;

import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector2fc;

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

    /*
     * @Author zomb_676
     */
    public static Matrix3f computeHomography
    (
        Vector2fc leftUp,
        Vector2fc leftDown,
        Vector2fc rightDown,
        Vector2fc rightUp,
        float width, float height
    )
    {
        Vector2f dstLU = new Vector2f(0f, 0f);
        Vector2f dstLD = new Vector2f(0f, height);
        Vector2f dstRD = new Vector2f(width, height);
        Vector2f dstRU = new Vector2f(width, 0f);

        float[][] a = new float[8][8];
        float[] bVec = new float[8];

        fillEquationRow(a, bVec, 0, leftUp, dstLU);
        fillEquationRow(a, bVec, 2, leftDown, dstLD);
        fillEquationRow(a, bVec, 4, rightDown, dstRD);
        fillEquationRow(a, bVec, 6, rightUp, dstRU);

        float[] x = solveLinearSystem(a, bVec);

        return new Matrix3f
        (
            x[0], x[1], x[2],
            x[3], x[4], x[5],
            x[6], x[7], 1f
        );
    }

    /*
     * @Author zomb_676
     */
    private static void fillEquationRow(float[][] a, float[] b, int row, Vector2fc src, Vector2f dst)
    {
        float x = src.x();
        float y = src.y();
        float u = dst.x();
        float v = dst.y();

        a[row][0] = x;
        a[row][1] = y;
        a[row][2] = 1f;
        a[row][3] = 0f;
        a[row][4] = 0f;
        a[row][5] = 0f;
        a[row][6] = -u * x;
        a[row][7] = -u * y;
        b[row] = u;

        a[row + 1][0] = 0f;
        a[row + 1][1] = 0f;
        a[row + 1][2] = 0f;
        a[row + 1][3] = x;
        a[row + 1][4] = y;
        a[row + 1][5] = 1f;
        a[row + 1][6] = -v * x;
        a[row + 1][7] = -v * y;
        b[row + 1] = v;
    }

    /*
     * @Author zomb_676
     */
    private static float[] solveLinearSystem(float[][] a, float[] b)
    {
        int n = a.length;
        float[][] aug = createAugmentedMatrix(a, b);

        for (int i = 0; i < n; i++)
        {
            int maxRow = i;
            for (int k = i; k < n; k++)
            {
                if (Math.abs(aug[k][i]) > Math.abs(aug[maxRow][i])) maxRow = k;
            }

            float[] temp = aug[i];
            aug[i] = aug[maxRow];
            aug[maxRow] = temp;

            float pivot = aug[i][i];
            if (Math.abs(pivot) <= 1e-6f)
            {
                //throw new IllegalArgumentException("Matrix is singular, check if points are collinear");
                return b;
            }

            for (int j = i; j <= n; j++)
            {
                aug[i][j] /= pivot;
            }

            for (int k = i + 1; k < n; k++)
            {
                float factor = aug[k][i];
                for (int j = i; j <= n; j++) {aug[k][j] -= factor * aug[i][j];}
            }
        }

        float[] x = new float[n];

        for (int i = n - 1; i >= 0; i--)
        {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++)
            {
                x[i] -= aug[i][j] * x[j];
            }
        }

        return x;
    }

    /*
     * @Author zomb_676
     */
    private static float[][] createAugmentedMatrix(float[][] a, float[] b)
    {
        int n = a.length;
        float[][] aug = new float[n][n + 1];
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n + 1; j++)
            {
                if (j < n) aug[i][j] = a[i][j];
                else aug[i][j] = b[i];
            }
        }
        return aug;
    }

    public static float[] solvePosition(float mouseX, float mouseY, float width, float height, Vector2fc ul, Vector2fc dl, Vector2fc dr, Vector2fc ur)
    {
        Matrix3f matrix = computeHomography(ul, dl, dr, ur, width, height);
        Vector2f p = new Vector2f(mouseX, mouseY);
        float w = matrix.m20 * p.x + matrix.m21 * p.y + matrix.m22;
        float x = (matrix.m00 * p.x + matrix.m01 * p.y + matrix.m02) / w;
        float y = (matrix.m10 * p.x + matrix.m11 * p.y + matrix.m12) / w;
        return new float[]{x, y};
    }
}
