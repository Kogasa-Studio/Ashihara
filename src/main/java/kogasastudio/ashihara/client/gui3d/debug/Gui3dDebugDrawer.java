package kogasastudio.ashihara.client.gui3d.debug;

import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector4f;

public final class Gui3dDebugDrawer
{
    private static final int[][] BOX_EDGES = {
        {0, 1}, {1, 3}, {3, 2}, {2, 0},
        {4, 5}, {5, 7}, {7, 6}, {6, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    private Gui3dDebugDrawer()
    {
    }

    public static DrawResult drawObb(GuiGraphicsExtractor guiGraphics, Screen3D screen, OBB obb, int color)
    {
        Vector4f[] corners = Gui3dDebugProjector.projectCorners(obb, screen);
        if (corners == null)
        {
            return new DrawResult(false, false);
        }

        boolean inside = false;
        for (Vector4f corner : corners)
        {
            if (corner.x >= screen.getPickingPipX0() && corner.x <= screen.getPickingPipX1()
                && corner.y >= screen.getPickingPipY0() && corner.y <= screen.getPickingPipY1())
            {
                inside = true;
                break;
            }
        }

        for (int[] edge : BOX_EDGES)
        {
            drawLine(guiGraphics,
                Math.round(corners[edge[0]].x), Math.round(corners[edge[0]].y),
                Math.round(corners[edge[1]].x), Math.round(corners[edge[1]].y),
                color);
        }

        for (Vector4f corner : corners)
        {
            int x = Math.round(corner.x);
            int y = Math.round(corner.y);
            guiGraphics.fill(x - 1, y - 1, x + 1, y + 1, color);
        }

        return new DrawResult(true, inside);
    }

    private static void drawLine(GuiGraphicsExtractor guiGraphics, int x0, int y0, int x1, int y1, int color)
    {
        int dx = Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;

        while (true)
        {
            guiGraphics.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1)
            {
                break;
            }
            int e2 = err * 2;
            if (e2 >= dy)
            {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx)
            {
                err += dx;
                y0 += sy;
            }
        }
    }

    public record DrawResult(boolean drawn, boolean inside)
    {
    }
}

