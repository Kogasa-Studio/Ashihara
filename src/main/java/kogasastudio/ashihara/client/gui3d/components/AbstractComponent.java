package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.interaction.HitPolicy;
import kogasastudio.ashihara.client.gui3d.interaction.HitResult;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.gui3d.util.ObbInterSector;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbstractComponent
{
    public Screen3D screen;
    public AbstractComponent parent;
    protected List<AbstractComponent> children = new ArrayList<>();

    public float x;
    public float y;
    public float z;
    protected float scaleX;
    protected float scaleY;
    protected float scaleZ;

    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    protected boolean wasHovered = false;
    protected boolean dragging = false;
    protected boolean doTick = false;
    protected int interactionPriority = 0;
    protected HitPolicy hitPolicy = HitPolicy.BLOCK;

    public AbstractComponent()
    {
        this(0, 0, 0, 0, 0, 0);
    }

    public AbstractComponent(float x, float y, float z, float scaleX, float scaleY, float scaleZ)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void attach(Screen3D screen)
    {
        this.screen = screen;
        for (AbstractComponent child : this.children)
        {
            child.parent = this;
            child.attach(screen);
        }
    }

    public List<AbstractComponent> getChildren()
    {
        return children;
    }

    public void addChild(AbstractComponent child)
    {
        child.parent = this;
        this.children.add(child);
        if (this.screen != null)
        {
            child.attach(this.screen);
            child.init();
        }
    }

    public void removeChild(AbstractComponent child)
    {
        this.children.remove(child);
        child.dispose();
        child.parent = null;
        child.screen = null;
    }

    public void dispose()
    {
        for (AbstractComponent child : this.children)
        {
            child.dispose();
        }
    }

    public void init()
    {
        for (AbstractComponent child : this.children)
        {
            child.init();
        }
    }

    public boolean ticks()
    {
        return this.doTick;
    }

    public void enableTick()
    {
        this.doTick = true;
    }

    public void disableTick()
    {
        this.doTick = false;
    }

    public void tick()
    {
        for (AbstractComponent child : this.children)
        {
            child.tick();
        }
    }

    public void render(GUI3DComponentRenderState renderState, int mouseX, int mouseY, float partialTick)
    {
        if (!this.visible)
        {
            return;
        }

        this.renderSelf(renderState, mouseX, mouseY, partialTick);
        this.children.forEach(child -> child.render(renderState, mouseX, mouseY, partialTick));
    }

    /**
     * Collect PiP render-state nodes from this component tree.
     */
    public void collectRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        if (!this.visible)
        {
            return;
        }

        this.collectSelfRenderStates(output, mouseX, mouseY, partialTick);
        this.children.forEach(child -> child.collectRenderStates(output, mouseX, mouseY, partialTick));
    }

    /** Hook for subclasses to append their own render-state submissions. */
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
    }

    protected void renderSelf(GUI3DComponentRenderState renderState, int mouseX, int mouseY, float partialTick)
    {
    }

    public boolean draggable() {return false;}

    public boolean mouseClicked(MouseButtonEvent event)
    {
        if (this.draggable()) this.dragging = event.button() == 0;
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event)
    {
        if (this.draggable())
        {
            boolean wasDragging = this.dragging;
            this.dragging = false;
            return wasDragging;
        }
        return false;
    }

    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
    {
        return this.draggable() && this.dragging;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return false;
    }

    public void mouseMoved(double mouseX, double mouseY)
    {
    }

    public void setHovered(boolean hovered)
    {
        this.hovered = hovered;
    }

    public boolean isHovered()
    {
        return this.hovered;
    }

    public boolean isDragging()
    {
        return this.dragging;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public int getInteractionPriority()
    {
        return this.interactionPriority;
    }

    public HitPolicy getHitPolicy()
    {
        return this.hitPolicy;
    }

    public void setHitPolicy(HitPolicy hitPolicy)
    {
        this.hitPolicy = hitPolicy;
    }

    protected boolean isPenetrating()
    {
        return false;
    }

    protected HitPolicy resolveHitPolicy()
    {
        return this.getHitPolicy() == HitPolicy.MIXED
            ? (this.isPenetrating() ? HitPolicy.PENETRATE : HitPolicy.BLOCK)
            : this.getHitPolicy();
    }

    public void setInteractionPriority(int interactionPriority)
    {
        this.interactionPriority = interactionPriority;
    }

    public float getGlobalX()
    {
        return this.parent == null ? this.x : this.parent.getGlobalX() + this.x;
    }

    public float getGlobalY()
    {
        return this.parent == null ? this.y : this.parent.getGlobalY() + this.y;
    }

    public float getGlobalZ()
    {
        return this.parent == null ? this.z : this.parent.getGlobalZ() + this.z;
    }

    public float getAbsoluteDepth()
    {
        return this.getGlobalZ() + this.scaleZ;
    }

    public float getAbsoluteInteractionDepth()
    {
        return this.getAbsoluteDepth() + this.interactionPriority;
    }

    @Nullable
    public OBB getCollisionBox()
    {
        Matrix4f pose = new Matrix4f().translation(this.getGlobalX(), this.getGlobalY(), this.getGlobalZ());
        return new OBB(new Vector3f(this.scaleX * 0.5f, this.scaleY * 0.5f, this.scaleZ * 0.5f), new Vector3f(), new Vector3f(this.scaleX, this.scaleY, this.scaleZ), pose);
    }

    public List<OBB> getCollisionBoxes()
    {
        OBB box = this.getCollisionBox();
        if (box == null)
        {
            return Collections.emptyList();
        }
        return List.of(box);
    }

    public float rayHitDistance(Ray ray)
    {
        float nearest = Float.POSITIVE_INFINITY;
        for (OBB box : this.getCollisionBoxes())
        {
            float hit = ObbInterSector.rayOBBIntersect(ray, box);
            if (hit >= 0.0f)
            {
                nearest = Math.min(nearest, hit);
            }
        }
        return nearest == Float.POSITIVE_INFINITY ? -1.0f : nearest;
    }

    @Nullable
    public HitResult findTopHit(Ray ray)
    {
        if (!this.visible)
        {
            return null;
        }

        HitResult nearestBlock     = null;
        HitResult nearestPenetrate = null;

        List<AbstractComponent> orderedChildren = new ArrayList<>(this.children);
        orderedChildren.sort(Comparator.comparingDouble(AbstractComponent::getAbsoluteInteractionDepth).reversed());
        for (AbstractComponent child : orderedChildren)
        {
            HitResult childHit = child.findTopHit(ray);
            if (childHit == null) continue;

            if (childHit.policy() == HitPolicy.BLOCK)
            {
                if (nearestBlock == null || childHit.t() < nearestBlock.t())
                {
                    nearestBlock = childHit;
                }
                continue;
            }

            if (nearestPenetrate == null || childHit.t() < nearestPenetrate.t())
            {
                nearestPenetrate = childHit;
            }
        }

        if (this.enabled)
        {
            float t = this.rayHitDistance(ray);
            if (t >= 0.0f)
            {
                HitResult selfHit = HitResult.of(this, t, ray.origin(), ray.direction(), this.resolveHitPolicy());
                if (selfHit.policy() == HitPolicy.BLOCK)
                {
                    if (nearestBlock == null || t < nearestBlock.t())
                    {
                        nearestBlock = selfHit;
                    }
                }
                else if (nearestPenetrate == null || t < nearestPenetrate.t())
                {
                    nearestPenetrate = selfHit;
                }
            }
        }

        return nearestBlock != null ? nearestBlock : nearestPenetrate;
    }

    public void clearHoverState()
    {
        this.wasHovered = this.hovered;
        this.hovered = false;
        for (AbstractComponent child : this.children)
        {
            child.clearHoverState();
        }
    }

    public void setHoveredChain()
    {
        this.hovered = true;
        if (this.parent != null)
        {
            this.parent.setHoveredChain();
        }
    }

    public float[] getScreenBounds()
    {        List<OBB> boxes = this.getCollisionBoxes();
        if (boxes.isEmpty())
        {
            return null;
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (OBB box : boxes)
        {
            float[] projected = projectBounds(box);
            if (projected == null)
            {
                continue;
            }
            minX = Math.min(minX, projected[0]);
            minY = Math.min(minY, projected[1]);
            maxX = Math.max(maxX, projected[2]);
            maxY = Math.max(maxY, projected[3]);
        }

        if (!Float.isFinite(minX) || !Float.isFinite(minY) || !Float.isFinite(maxX) || !Float.isFinite(maxY))
        {
            return null;
        }

        return new float[]{minX, minY, maxX, maxY};
    }

    public Screen3D getScreen()
    {
        return this.screen;
    }

    // ---- 悬停过渡生命周期钩子（空实现，子类覆写以响应过渡事件）----

    /** 当组件从"未悬停"变为"悬停"时调用一次。 */
    protected void onHoverEnter() {}

    /** 当组件从"悬停"变为"未悬停"时调用一次。 */
    protected void onHoverExit() {}

    /**
     * 比较 wasHovered 与当前 hovered，按需调用 onHoverEnter / onHoverExit，再递归 children。
     * 由 Screen3D.updateHoverState() 在每帧 setHoveredChain 之后调用。
     */
    public void fireHoverTransitions()
    {
        if (!this.wasHovered && this.hovered)
        {
            this.onHoverEnter();
        }
        else if (this.wasHovered && !this.hovered)
        {
            this.onHoverExit();
        }
        for (AbstractComponent child : this.children)
        {
            child.fireHoverTransitions();
        }
    }

    private float[] projectBounds(OBB box)
    {
        Matrix4f pose = box.pose();
        Vector3f min = box.minXYZ();
        Vector3f max = box.maxXYZ();
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

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
                    float screenX = transformed.x / transformed.w;
                    float screenY = transformed.y / transformed.w;
                    minX = Math.min(minX, screenX);
                    minY = Math.min(minY, screenY);
                    maxX = Math.max(maxX, screenX);
                    maxY = Math.max(maxY, screenY);
                }
            }
        }

        if (!Float.isFinite(minX) || !Float.isFinite(minY) || !Float.isFinite(maxX) || !Float.isFinite(maxY))
        {
            return null;
        }

        return new float[]{minX, minY, maxX, maxY};
    }
}
