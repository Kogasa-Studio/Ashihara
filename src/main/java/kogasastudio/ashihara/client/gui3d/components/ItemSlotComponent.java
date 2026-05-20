package kogasastudio.ashihara.client.gui3d.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.gui3d.ContainerScreen3D;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 物品槽位组件。
 *
 * <p>每个实例绑定：
 * <ul>
 *   <li>一个来自父级 Model 的骨骼（{@code boneName}），用于定位渲染位置和碰撞盒</li>
 *   <li>一个 {@link Slot}（来自 Menu），用于读取物品和发送交互</li>
 * </ul>
 *
 * <p>交互逻辑：
 * <ul>
 *   <li>左键（无 Shift）→ {@link net.minecraft.world.inventory.ClickAction#PRIMARY}，button=0（拾取/放置/交换）</li>
 *   <li>右键           → {@link net.minecraft.world.inventory.ClickAction#SECONDARY}，button=1（分半放置）</li>
 *   <li>Shift+左键     → {@link net.minecraft.world.inventory.ClickAction#PRIMARY}，button=0（快速转移）</li>
 * </ul>
 */
public class ItemSlotComponent extends ModelComponent implements ISelectable
{
    /** 渲染 3D 物品时的缩放系数（在骨骼局部空间中）。可在实例上直接赋值覆盖。 */
    public float itemRenderScale = 0.45f;

    protected final String boneName;
    protected final Slot menuSlot;
    protected final SelectionFrameComponent selectionFrame;
    protected final SelectionFrameModel frameModel = new SelectionFrameModel("assistance/cubic_selection_frame", "textures/geo/highlight_outline.png", Minecraft.getInstance().player);

    /**
     * @param model     父级 ModelComponent 持有的 GeoModel 实例（共享引用，用于骨骼追踪）
     * @param boneName  要绑定的骨骼名称，例如 {@code "item_slot_0"}
     * @param menuSlot  对应 Menu 中的 {@link Slot}，用于读写物品和提供 slotId
     */
    public ItemSlotComponent(SimpleInternalControlGeoModel model, String boneName, Slot menuSlot)
    {
        super(model);
        this.boneName = boneName;
        this.menuSlot = menuSlot;
        this.interactionPriority = 50;

        // 预先绑定骨骼，使 ModelComponent.init() 能在首次渲染前注册到 rendererPoseSync
        this.bindBone(boneName);

        this.selectionFrame = new SelectionFrameComponent(this.frameModel);
    }

    // ── 生命周期 ─────────────────────────────────────────────────────────────

    @Override
    public void init()
    {
        super.init();
        this.addChild(selectionFrame);
    }

    // ── 碰撞盒 ───────────────────────────────────────────────────────────────

    @Override
    public List<OBB> getCollisionBoxes()
    {
        List<OBB> boxes = this.getBoneCollisionBoxes(this.boneName);
        return boxes.isEmpty() ? Collections.emptyList() : boxes;
    }

    public BiConsumer<PoseStack, OBB> getItemTranslate()
    {
        return (poseStack, obb) ->
        {
            Vector3f t = new Vector3f(obb.maxXYZ()).min(obb.minXYZ()).mul(1f);
            poseStack.translate(t.x()+2.5/16, t.y()+2.5/16, t.z()+2.5/16);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90f));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            poseStack.scale(-this.itemRenderScale, this.itemRenderScale, this.itemRenderScale);
        };
    }

    // ── 渲染 ─────────────────────────────────────────────────────────────────


    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        ItemStack stack = this.menuSlot.getItem();
        if (stack.isEmpty())
        {
            return;
        }

        BoneTracer tracer = this.boneTracers.get(this.boneName);
        if (tracer == null || tracer.collisionBoxes().isEmpty())
        {
            return;
        }

        OBB obb = tracer.collisionBoxes().getFirst();
        output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
        {
            poseStack.pushPose();
            poseStack.last().pose().set(obb.pose());
            this.getItemTranslate().accept(poseStack, obb);
            poseStack.last().normal().identity();
            RenderHelper.renderItem
            (
                poseStack,
                submitNodeCollector,
                stack,
                ItemDisplayContext.GUI,
                Minecraft.getInstance().level,
                Minecraft.getInstance().player,
                this.menuSlot.index,
                15728880,
                OverlayTexture.NO_OVERLAY,
                0
            );
            poseStack.popPose();
        }));
    }

    // ── 交互 ─────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(MouseButtonEvent event)
    {
        return super.mouseClicked(event);
    }

    // ── 悬停钩子 ─────────────────────────────────────────────────────────────

    @Override
    protected void onHoverEnter()
    {
        List<OBB> boxes = this.getCollisionBoxes();
        if (!boxes.isEmpty()) this.selectionFrame.onHoverEnter(boxes);
        if (this.screen instanceof ContainerScreen3D<?> cs)
        {
            cs.getContainerScreen().setHoveredSlot(this.menuSlot);
        }
    }

    @Override
    protected void onHoverExit()
    {
        this.selectionFrame.onHoverExit();
        if (this.screen instanceof ContainerScreen3D<?> cs && cs.getContainerScreen().getHoveredSlot() == this.menuSlot)
        {
            cs.getContainerScreen().setHoveredSlot(null);
        }
    }

    // ── ISelectable ───────────────────────────────────────────────────────────

    @Override
    public SelectionFrameComponent getSelectionFrame()
    {
        return this.selectionFrame;
    }

    // ── 访问器 ────────────────────────────────────────────────────────────────

    public Slot     getMenuSlot()  { return this.menuSlot; }
    public int      getSlotIndex() { return this.menuSlot.index; }
    public ItemStack getStack()    { return this.menuSlot.getItem(); }
}
