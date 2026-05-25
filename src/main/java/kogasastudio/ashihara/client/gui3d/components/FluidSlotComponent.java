package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.ContainerScreen3D;
import kogasastudio.ashihara.client.gui3d.interaction.HitPolicy;
import kogasastudio.ashihara.client.gui3d.interaction.HitResult;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.container.PotMenu;
import kogasastudio.ashihara.network.FluidSlotClickPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 流体槽位组件。
 *
 * <p>绑定一个骨骼，在骨骼的 OBB 范围六面渲染流体贴图。
 * 当光标抓取的物品具有流体交互能力时才可选中，左键触发流体交互。
 */
public class FluidSlotComponent extends ModelComponent implements ISelectable
{
    protected final String boneName;
    protected final BlockPos blockEntityPos;
    protected final ContainerScreen3D<?> containerScreen3D;
    protected final SelectionFrameComponent selectionFrame;
    protected final SelectionFrameModel frameModel = new SelectionFrameModel("assistance/cubic_selection_frame", "textures/geo/highlight_outline.png");

    public FluidSlotComponent(SimpleInternalControlGeoModel model, String boneName, BlockPos pos, ContainerScreen3D<?> screen)
    {
        super(model);
        this.boneName = boneName;
        this.blockEntityPos = pos;
        this.containerScreen3D = screen;
        this.interactionPriority = 50;

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

    @Override
    @Nullable
    public HitResult findTopHit(Ray ray)
    {
        if (!hasFluidCapability(getCarriedItem())) return null;
        return super.findTopHit(ray);
    }

    @Override
    protected boolean isPenetrating()
    {
        return !hasFluidCapability(getCarriedItem());
    }

    private boolean hasFluidCapability(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        return ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM) != null;
    }

    @Override
    public HitPolicy getHitPolicy()
    {
        return HitPolicy.MIXED;
    }

    // ── 渲染 ─────────────────────────────────────────────────────────────────

    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        var tracer = this.boneTracers.get(this.boneName);
        if (tracer == null || tracer.collisionBoxes().isEmpty()) return;

        FluidStack fluidStack = getFluidStack();
        if (fluidStack.isEmpty()) return;

        OBB obb = tracer.collisionBoxes().getFirst();
        output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
        {
            RenderHelper.renderFluidOnOBB(poseStack, obb, fluidStack, submitNodeCollector, 15728880);
        }, true));
    }

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        Component fluid_slot = Component.translatable("tooltip.ashihara.fluid_slot");
        MutableComponent component = MutableComponent.create(Component.empty().getContents());
        component.append(Component.translatable("tooltip.ashihara.fluid_type"));
        component.append(getFluidStack().getHoverName());
        if (getTank() != null && !getFluidStack().isEmpty())
        {
            component.append(" ");
            component.append(String.valueOf(getFluidStack().amount()));
            component.append("mB / ");
            component.append(String.valueOf(getTank().getCapacity()));
            component.append("mB");
        }
        component.append(".");
        component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(RenderHelper.getFluidTintColor(getFluidStack()))));

        List<Component> list = List.of(fluid_slot, component);
        graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, (int) mouseX, (int) mouseY);
        super.extractTooltip(graphics, mouseX, mouseY);
    }
// ── 交互 ─────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(MouseButtonEvent event)
    {
        if (event.button() == 0 && !isPenetrating())
        {
            ClientPacketDistributor.sendToServer(new FluidSlotClickPayload(blockEntityPos));
            return true;
        }
        return super.mouseClicked(event);
    }

    // ── 悬停钩子 ─────────────────────────────────────────────────────────────

    @Override
    protected void onHoverEnter()
    {
        this.selectionFrame.onHoverEnter();
    }

    @Override
    protected void onHoverExit()
    {
        this.selectionFrame.onHoverExit();
    }

    // ── ISelectable ───────────────────────────────────────────────────────────

    @Override
    public SelectionFrameComponent getSelectionFrame()
    {
        return this.selectionFrame;
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    private ItemStack getCarriedItem()
    {
        return this.containerScreen3D.getContainerScreen().getMenu().getCarried();
    }

    private FluidStack getFluidStack()
    {
        return getTank() == null ? FluidStack.EMPTY : getTank().getFluidStack();
    }

    private BEFluidStackHandler<?> getTank()
    {
        if (this.containerScreen3D.getContainerScreen().getMenu() instanceof PotMenu potMenu)
        {
            return potMenu.blockEntity.fluidTank;
        }
        return null;
    }
}
