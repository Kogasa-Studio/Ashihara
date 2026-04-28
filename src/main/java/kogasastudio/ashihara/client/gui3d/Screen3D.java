package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Screen3D extends Screen
{
    public final List<AbstractComponent> components = new ArrayList<>();
    public AbstractComponent draggingComponent;
    public boolean componentsInitialized = false;
    public boolean debugOverlayEnabled = false;

    public Screen3D(Component title)
    {
        super(title);
    }

    @Override
    public void init()
    {
        super.init();
        this.componentsInitialized = true;
        for (AbstractComponent component : this.components)
        {
            component.attach(this);
            component.init();
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        for (AbstractComponent component : this.components)
        {
            component.tick();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.updateHoverState(mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.components.forEach(component -> component.render(guiGraphics, mouseX, mouseY, partialTick));

        if (this.debugOverlayEnabled)
        {
            Gui3dDebugOverlay.render(guiGraphics, this, this.components, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_F9)
        {
            this.debugOverlayEnabled = !this.debugOverlayEnabled;
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void addComponent(AbstractComponent component)
    {
        this.components.add(component);
        component.attach(this);
        if (this.componentsInitialized)
        {
            component.init();
        }
    }

    public void removeComponent(AbstractComponent component)
    {
        this.components.remove(component);
    }

    public void clearComponents()
    {
        this.components.clear();
        this.draggingComponent = null;
    }

    public Vector3f screenToGuiSpace(double screenX, double screenY, float guiZ)
    {
        return new Vector3f((float) screenX, (float) screenY, guiZ);
    }

    public Vector2f guiSpaceToScreen(float guiX, float guiY, float guiZ)
    {
        return new Vector2f(guiX, guiY);
    }

    public Ray createMouseRay(double mouseX, double mouseY)
    {
        return new Ray
        (
            new Vector3f((float) mouseX, (float) mouseY, -2000.0f),
            new Vector3f(0.0f, 0.0f, 1.0f)
        );
    }

    public boolean isDebugOverlayEnabled()
    {
        return this.debugOverlayEnabled;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        AbstractComponent target = this.findTopComponent(mouseX, mouseY);
        if (target != null && target.mouseClicked(mouseX, mouseY, button))
        {
            if (button == 0)
            {
                this.draggingComponent = target;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.draggingComponent != null)
        {
            AbstractComponent target = this.draggingComponent;
            this.draggingComponent = null;
            if (target.mouseReleased(mouseX, mouseY, button))
            {
                return true;
            }
        }

        AbstractComponent hovered = this.findTopComponent(mouseX, mouseY);
        if (hovered != null && hovered.mouseReleased(mouseX, mouseY, button))
        {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.draggingComponent != null && this.draggingComponent.mouseDragged(mouseX, mouseY, button, dragX, dragY))
        {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        AbstractComponent target = this.findTopComponent(mouseX, mouseY);
        if (target != null && target.mouseScrolled(mouseX, mouseY, scrollY))
        {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        this.updateHoverState(mouseX, mouseY);
        AbstractComponent target = this.findTopComponent(mouseX, mouseY);
        if (target != null)
        {
            target.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    protected void updateHoverState(double mouseX, double mouseY)
    {
        for (AbstractComponent component : this.components)
        {
            component.clearHoverState();
        }

        AbstractComponent target = this.findTopComponent(mouseX, mouseY);
        if (target != null)
        {
            target.setHoveredChain();
        }

        for (AbstractComponent component : this.components)
        {
            component.fireHoverTransitions();
        }
    }

    protected AbstractComponent findTopComponent(double mouseX, double mouseY)
    {
        List<AbstractComponent> ordered = new ArrayList<>(this.components);
        ordered.sort(Comparator.comparingDouble(AbstractComponent::getAbsoluteInteractionDepth).reversed());
        for (AbstractComponent component : ordered)
        {
            AbstractComponent hit = component.findHitComponent(mouseX, mouseY);
            if (hit != null)
            {
                return hit;
            }
        }
        return null;
    }

    public static double mouseX()
    {
        return Minecraft.getInstance().mouseHandler.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth();
    }

    public static double mouseY()
    {
        return Minecraft.getInstance().mouseHandler.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight();
    }
}
