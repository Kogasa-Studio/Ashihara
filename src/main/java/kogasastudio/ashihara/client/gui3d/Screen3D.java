package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.client.gui3d.components.AbstractComponent;
import kogasastudio.ashihara.client.gui3d.interaction.HitPolicy;
import kogasastudio.ashihara.client.gui3d.interaction.HitResult;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.client.render.state.Screen3DPiPRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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

    // Active PiP context used for picking (same space as OBB.pose generated during PiP render).
    protected int pickingPipX0 = 0;
    protected int pickingPipY0 = 0;
    protected int pickingPipX1 = 0;
    protected int pickingPipY1 = 0;
    protected float pickingPipScale = 1.0f;
    protected float pickingGuiScale = 1.0f;

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        this.updateHoverState(mouseX, mouseY);
        super.extractRenderState(graphics, mouseX, mouseY, a);

        List<GUI3DComponentRenderState> renderStates = new ArrayList<>();
        this.components.forEach(component -> component.collectRenderStates(renderStates, mouseX, mouseY, a));

        Screen3DPiPRenderState state = new Screen3DPiPRenderState
        (
            0, 0, this.width, this.height, 1.0f,
            graphics.peekScissorStack(),
            renderStates,
            0xF000F0,
            a
        );

        // Picking must use the same coordinate space as PiP texture rendering.
        this.pickingPipX0 = state.x0();
        this.pickingPipY0 = state.y0();
        this.pickingPipX1 = state.x1();
        this.pickingPipY1 = state.y1();
        this.pickingPipScale = state.scale();
        this.pickingGuiScale = computeCurrentGuiScale();

        graphics.submitPictureInPictureRenderState(state);
        if (this.debugOverlayEnabled)
        {
            Gui3dDebugOverlay.render(graphics, this, this.components, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (event.key() == GLFW.GLFW_KEY_F9)
        {
            this.debugOverlayEnabled = !this.debugOverlayEnabled;
            return true;
        }

        return super.keyPressed(event);
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
        component.dispose();
    }

    public void clearComponents()
    {
        this.components.forEach(AbstractComponent::dispose);
        this.components.clear();
        this.draggingComponent = null;
    }

    @Override
    public void onClose()
    {
        this.clearComponents();
        super.onClose();
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
        // Convert from GUI space into PiP texture pixel space to match OBB.pose space.
        float localGuiX = (float) mouseX - this.pickingPipX0;
        float localGuiY = (float) mouseY - this.pickingPipY0;
        float pipPixelX = localGuiX * this.pickingGuiScale * this.pickingPipScale;
        float pipPixelY = localGuiY * this.pickingGuiScale * this.pickingPipScale;

        return new Ray
        (
            new Vector3f(pipPixelX, pipPixelY, -2000.0f),
            new Vector3f(0.0f, 0.0f, 1.0f)
        );
    }

    protected float computeCurrentGuiScale()
    {
        if (this.width <= 0)
        {
            return 1.0f;
        }

        return (float) Minecraft.getInstance().getWindow().getWidth() / (float) this.width;
    }

    public float getPickingGuiScale()
    {
        return this.pickingGuiScale;
    }

    public int getPickingPipX0()
    {
        return this.pickingPipX0;
    }

    public int getPickingPipY0()
    {
        return this.pickingPipY0;
    }

    public int getPickingPipX1()
    {
        return this.pickingPipX1;
    }

    public int getPickingPipY1()
    {
        return this.pickingPipY1;
    }

    public float getPickingPipScale()
    {
        return this.pickingPipScale;
    }

    public boolean isDebugOverlayEnabled()
    {
        return this.debugOverlayEnabled;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked)
    {
        double mouseX = event.x(), mouseY = event.y();
        int button = event.button();
        AbstractComponent target = this.findTopComponent(mouseX, mouseY);
        if (target != null && target.mouseClicked(event))
        {
            if (button == 0)
            {
                this.draggingComponent = target;
            }
            return true;
        }
        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        double mouseX = event.x(), mouseY = event.y();
        if (this.draggingComponent != null)
        {
            AbstractComponent target = this.draggingComponent;
            this.draggingComponent = null;
            if (target.mouseReleased(event))
            {
                return true;
            }
        }

        AbstractComponent hovered = this.findTopComponent(mouseX, mouseY);
        if (hovered != null && hovered.mouseReleased(event))
        {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
    {
        if (this.draggingComponent != null && this.draggingComponent.mouseDragged(event, dragX, dragY))
        {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
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
        AbstractComponent target = this.findTopHoverComponent(mouseX, mouseY);
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

        AbstractComponent target = this.findTopHoverComponent(mouseX, mouseY);
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
        HitResult hitResult = this.findTopHit(mouseX, mouseY);
        if (hitResult == null || hitResult.policy() != HitPolicy.BLOCK)
        {
            return null;
        }

        return hitResult.component();
    }

    protected AbstractComponent findTopHoverComponent(double mouseX, double mouseY)
    {
        HitResult hitResult = this.findTopHit(mouseX, mouseY);
        return hitResult == null ? null : hitResult.component();
    }

    protected HitResult findTopHit(double mouseX, double mouseY)
    {
        Ray ray = this.createMouseRay(mouseX, mouseY);
        List<AbstractComponent> ordered = new ArrayList<>(this.components);
        ordered.sort(Comparator.comparingDouble(AbstractComponent::getAbsoluteInteractionDepth).reversed());

        HitResult nearestPenetrate = null;
        for (AbstractComponent component : ordered)
        {
            HitResult hit = component.findTopHit(ray);
            if (hit == null)
            {
                continue;
            }

            if (hit.policy() == HitPolicy.BLOCK)
            {
                return hit;
            }

            if (nearestPenetrate == null || hit.t() < nearestPenetrate.t())
            {
                nearestPenetrate = hit;
            }
        }

        return nearestPenetrate;
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
