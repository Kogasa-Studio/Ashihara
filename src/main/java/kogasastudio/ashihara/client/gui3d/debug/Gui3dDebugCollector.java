package kogasastudio.ashihara.client.gui3d.debug;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.components.SelectionFrameComponent;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.gui3d.util.ObbInterSector;
import kogasastudio.ashihara.client.gui3d.util.Ray;

import java.util.ArrayList;
import java.util.List;

public final class Gui3dDebugCollector
{
    private Gui3dDebugCollector()
    {
    }

    public static DebugSnapshot collect(List<AbstractComponent> roots, Ray ray)
    {
        List<ObbDrawEntry> drawEntries = new ArrayList<>();
        List<HitInfo> hits = new ArrayList<>();
        List<String> frameLines = new ArrayList<>();

        for (AbstractComponent root : roots)
        {
            collectRecursively(ray, root, drawEntries, hits, frameLines);
        }

        return new DebugSnapshot(drawEntries, hits, frameLines);
    }

    private static void collectRecursively(
        Ray ray,
        AbstractComponent component,
        List<ObbDrawEntry> drawEntries,
        List<HitInfo> hits,
        List<String> frameLines
    )
    {
        if (!component.isVisible())
        {
            return;
        }

        if (component instanceof SelectionFrameComponent selectionFrame)
        {
            frameLines.add(selectionFrame.getDebugStateLine());
        }

        List<OBB> boxes = component.getCollisionBoxes();
        for (int i = 0; i < boxes.size(); i++)
        {
            OBB obb = boxes.get(i);
            float t = ObbInterSector.rayOBBIntersect(ray, obb);
            int color = component.isHovered() ? 0xFFFFFF6B : (t >= 0 ? 0xFF72FF72 : 0x66AAB7C4);
            drawEntries.add(new ObbDrawEntry(obb, color));

            if (t >= 0)
            {
                hits.add(new HitInfo(component.getClass().getSimpleName() + "#" + i, t));
            }
        }

        for (AbstractComponent child : component.getChildren())
        {
            collectRecursively(ray, child, drawEntries, hits, frameLines);
        }
    }

    public record ObbDrawEntry(OBB obb, int color)
    {
    }

    public record HitInfo(String name, float distance)
    {
    }

    public record DebugSnapshot(
        List<ObbDrawEntry> drawEntries,
        List<HitInfo> hits,
        List<String> frameDebugLines
    )
    {
    }
}

