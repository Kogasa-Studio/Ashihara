package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.gui3d.debug.Gui3dDebugCollector;
import kogasastudio.ashihara.client.gui3d.debug.Gui3dDebugDrawer;
import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public final class Gui3dDebugOverlay
{
    private Gui3dDebugOverlay()
    {
    }

    public static void render(GuiGraphicsExtractor guiGraphics, Screen3D screen, List<AbstractComponent> roots, int mouseX, int mouseY)
    {
        guiGraphics.pose().translate(0,0);
        float localGuiX = mouseX - screen.getPickingPipX0();
        float localGuiY = mouseY - screen.getPickingPipY0();
        float pipPixelX = localGuiX * screen.getPickingGuiScale() * screen.getPickingPipScale();
        float pipPixelY = localGuiY * screen.getPickingGuiScale() * screen.getPickingPipScale();

        Ray ray = screen.createMouseRay(mouseX, mouseY);
        Gui3dDebugCollector.DebugSnapshot snapshot = Gui3dDebugCollector.collect(roots, ray);

        int drawn = 0;
        int fullyOffscreen = 0;
        for (Gui3dDebugCollector.ObbDrawEntry drawEntry : snapshot.drawEntries())
        {
            Gui3dDebugDrawer.DrawResult result = Gui3dDebugDrawer.drawObb(guiGraphics, screen, drawEntry.obb(), drawEntry.color());
            if (!result.drawn())
            {
                continue;
            }

            drawn++;
            if (!result.inside())
            {
                fullyOffscreen++;
            }
        }

        List<Gui3dDebugCollector.HitInfo> hits = snapshot.hits();
        hits.sort(Comparator.comparingDouble(Gui3dDebugCollector.HitInfo::distance));

        int y = 8;
        guiGraphics.text(Minecraft.getInstance().font, Component.literal("3D UI Debug [F9]"), 8, y, 0xFF7CFFB2, false);
        y += 10;
        guiGraphics.text(Minecraft.getInstance().font, Component.literal(String.format("mouse=(%d,%d) ray=(%.1f,%.1f,%.1f)->(%.1f,%.1f,%.1f)",
            mouseX, mouseY,
            ray.origin().x, ray.origin().y, ray.origin().z,
            ray.direction().x, ray.direction().y, ray.direction().z)), 8, y, 0xFFD7E3F4, false);
        y += 10;
        guiGraphics.text(Minecraft.getInstance().font, Component.literal(String.format("pip=[%d,%d -> %d,%d] scale(gui=%.3f pip=%.3f)",
            screen.getPickingPipX0(), screen.getPickingPipY0(),
            screen.getPickingPipX1(), screen.getPickingPipY1(),
            screen.getPickingGuiScale(), screen.getPickingPipScale())), 8, y, 0xFFB4C6FF, false);
        y += 10;
        guiGraphics.text(Minecraft.getInstance().font, Component.literal(String.format("local=(%.1f,%.1f) pipPixel=(%.1f,%.1f)",
            localGuiX, localGuiY, pipPixelX, pipPixelY)), 8, y, 0xFFB4C6FF, false);
        y += 10;
        guiGraphics.text(Minecraft.getInstance().font, Component.literal(String.format("obbDrawn=%d offscreen=%d", drawn, fullyOffscreen)), 8, y, 0xFFB4C6FF, false);
        y += 10;

        if (hits.isEmpty())
        {
            guiGraphics.text(Minecraft.getInstance().font, Component.literal("hit: none"), 8, y, 0xFFFF9090, false);
            return;
        }

        int limit = Math.min(6, hits.size());
        for (int i = 0; i < limit; i++)
        {
            Gui3dDebugCollector.HitInfo hit = hits.get(i);
            int color = i == 0 ? 0xFF9DFF57 : 0xFFFFD36B;
            guiGraphics.text(Minecraft.getInstance().font,
                Component.literal(String.format("hit[%d] %s t=%.2f", i, hit.name(), hit.distance())),
                8, y, color, false);
            y += 10;
        }

        List<String> frameDebugLines = snapshot.frameDebugLines();
        int frameLimit = Math.min(3, frameDebugLines.size());
        for (int i = 0; i < frameLimit; i++)
        {
            guiGraphics.text(Minecraft.getInstance().font, Component.literal(frameDebugLines.get(i)), 8, y, 0xFFB4C6FF, false);
            y += 10;
        }
    }
}

