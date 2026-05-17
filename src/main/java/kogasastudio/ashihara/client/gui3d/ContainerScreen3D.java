package kogasastudio.ashihara.client.gui3d;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;

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
 * @param <S> 对应的 AbstractContainerScreen 子类型
 */
public abstract class ContainerScreen3D<S extends AbstractContainerScreen<? extends AbstractContainerMenu>> extends Screen3D
{
    protected final S containerScreen;

    protected ContainerScreen3D(S containerScreen, Component title)
    {
        super(title);
        this.containerScreen = containerScreen;
    }

    public S getContainerScreen()
    {
        return this.containerScreen;
    }

    public void sendSlotClick(int slotId, int button, ClickAction clickType)
    {
    }

    /**
     * 渲染跟随鼠标的拾取物品（2D 平面，与原版槽位 UI 一致）。
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}

