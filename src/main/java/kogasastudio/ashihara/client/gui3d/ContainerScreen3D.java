package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.network.ContainerSlotClickPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 基于 Screen3D 的容器屏幕基类，持有 {@link AbstractContainerMenu} 并提供：
 * <ul>
 *   <li>跟随鼠标的 carried-item 2D 渲染</li>
 *   <li>{@link #sendSlotClick} 统一发送泛用槽位点击包</li>
 *   <li>关闭时向服务端发送 closeContainer 包（保证 carried-item 归还逻辑由服务端处理）</li>
 * </ul>
 *
 * <p>所有未来的 3D 容器屏幕均应继承此类，而非直接继承 {@link Screen3D}。
 *
 * @param <T> 对应的 AbstractContainerMenu 子类型
 */
public abstract class ContainerScreen3D<T extends AbstractContainerMenu>
        extends Screen3D
        implements MenuAccess<T>
{
    protected final T menu;

    protected ContainerScreen3D(T menu, Component title)
    {
        super(title);
        this.menu = menu;
    }

    @Override
    public T getMenu()
    {
        return this.menu;
    }

    /**
     * 发送槽位点击到服务端。
     * ItemSlotComponent 通过此方法触发交互，自身不直接操作 menu。
     *
     * @param slotId      在 menu.slots 列表中的索引
     * @param button      0=左键, 1=右键
     * @param clickType   原版 ClickType 枚举
     */
    public void sendSlotClick(int slotId, int button, ClickAction clickType)
    {
        ClientPacketDistributor.sendToServer(new ContainerSlotClickPacket(
                this.menu.containerId, slotId, button, clickType.ordinal()));
    }

    /**
     * 渲染跟随鼠标的拾取物品（2D 平面，与原版槽位 UI 一致）。
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        ItemStack carried = this.menu.getCarried();
        if (!carried.isEmpty())
        {
            graphics.item(carried, mouseX - 8, mouseY - 8);
            graphics.itemDecorations(Minecraft.getInstance().font, carried, mouseX - 8, mouseY - 8);
        }
    }

    /**
     * 关闭屏幕时先通知服务端关闭容器，保证 carried-item 归还逻辑正确执行。
     * 调用顺序与 AbstractContainerScreen.onClose() 保持一致（closeContainer 先于 super.onClose）。
     */
    @Override
    public void onClose()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null)
        {
            mc.player.closeContainer();
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}

