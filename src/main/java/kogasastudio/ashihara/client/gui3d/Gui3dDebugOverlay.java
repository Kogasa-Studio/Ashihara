package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.components.SelectionFrameComponent;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.gui3d.util.ObbInterSector;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Gui3dDebugOverlay
{
    private static final int[][] BOX_EDGES = {
        {0, 1}, {1, 3}, {3, 2}, {2, 0},
        {4, 5}, {5, 7}, {7, 6}, {6, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    private Gui3dDebugOverlay()
    {
    }

    public static void render(GuiGraphics guiGraphics, Screen3D screen, List<AbstractComponent> roots, int mouseX, int mouseY)
    {
        guiGraphics.pose().translate(0,0,3000);
        Ray ray = screen.createMouseRay(mouseX, mouseY);
        List<HitInfo> hits = new ArrayList<>();
        List<String> frameDebugLines = new ArrayList<>();

        for (AbstractComponent root : roots)
        {
            collectAndRender(guiGraphics, ray, root, hits, frameDebugLines);
        }

        hits.sort(Comparator.comparingDouble(HitInfo::distance));
        int y = 8;
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("3D UI Debug [F9]"), 8, y, 0xFF7CFFB2, false);
        y += 10;
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.format("mouse=(%d,%d) ray=(%.1f,%.1f,%.1f)->(%.1f,%.1f,%.1f)",
            mouseX, mouseY,
            ray.origin().x, ray.origin().y, ray.origin().z,
            ray.direction().x, ray.direction().y, ray.direction().z)), 8, y, 0xFFD7E3F4, false);
        y += 10;

        if (hits.isEmpty())
        {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("hit: none"), 8, y, 0xFFFF9090, false);
            return;
        }

        int limit = Math.min(6, hits.size());
        for (int i = 0; i < limit; i++)
        {
            HitInfo hit = hits.get(i);
            int color = i == 0 ? 0xFF9DFF57 : 0xFFFFD36B;
            guiGraphics.drawString(Minecraft.getInstance().font,
                Component.literal(String.format("hit[%d] %s t=%.2f", i, hit.name(), hit.distance())),
                8, y, color, false);
            y += 10;
        }

        int frameLimit = Math.min(4, frameDebugLines.size());
        for (int i = 0; i < frameLimit; i++)
        {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(frameDebugLines.get(i)), 8, y, 0xFFB4C6FF, false);
            y += 10;
        }
    }

    private static void collectAndRender(GuiGraphics guiGraphics, Ray ray, AbstractComponent component, List<HitInfo> hits, List<String> frameDebugLines)
    {
        if (!component.isVisible())
        {
            return;
        }

        if (component instanceof SelectionFrameComponent selectionFrame)
        {
            frameDebugLines.add(selectionFrame.getDebugStateLine());
        }

        List<OBB> boxes = component.getCollisionBoxes();
        for (int i = 0; i < boxes.size(); i++)
        {
            OBB obb = boxes.get(i);
            float t = ObbInterSector.rayOBBIntersect(ray, obb);
            int color = component.isHovered() ? 0xFFFFFF6B : (t >= 0 ? 0xFF72FF72 : 0x66AAB7C4);
            drawObb(guiGraphics, obb, color);

            if (t >= 0)
            {
                hits.add(new HitInfo(component.getClass().getSimpleName() + "#" + i, t));
            }
        }

        for (AbstractComponent child : component.getChildren())
        {
            collectAndRender(guiGraphics, ray, child, hits, frameDebugLines);
        }
    }

    private static void drawObb(GuiGraphics guiGraphics, OBB obb, int color)
    {
        Vector4f[] corners = projectCorners(obb);
        if (corners == null)
        {
            return;
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
    }

    private static Vector4f[] projectCorners(OBB obb)
    {
        Matrix4f pose = obb.pose();
        Vector3f min = obb.minXYZ();
        Vector3f max = obb.maxXYZ();
        Vector4f[] result = new Vector4f[8];
        int index = 0;

        for (int ix = 0; ix < 2; ix++)
        {
            for (int iy = 0; iy < 2; iy++)
            {
                for (int iz = 0; iz < 2; iz++)
                {
                    float px = ix == 0 ? min.x : max.x;
                    float py = iy == 0 ? min.y : max.y;
                    float pz = iz == 0 ? min.z : max.z;
                    Vector4f transformed = new Vector4f(px, py, pz, 1.0f).mul(pose);
                    if (!Float.isFinite(transformed.w) || transformed.w == 0.0f)
                    {
                        return null;
                    }
                    result[index++] = new Vector4f(transformed.x / transformed.w, transformed.y / transformed.w, transformed.z / transformed.w, 1.0f);
                }
            }
        }

        return result;
    }

    private static void drawLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color)
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

    private record HitInfo(String name, float distance)
    {
    }
}

