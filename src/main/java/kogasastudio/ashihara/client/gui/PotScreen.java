package kogasastudio.ashihara.client.gui;

import kogasastudio.ashihara.client.gui3d.ContainerScreen3D;
import kogasastudio.ashihara.client.gui3d.components.ItemSlotComponent;
import kogasastudio.ashihara.client.gui3d.components.PotLidComponent;
import kogasastudio.ashihara.client.gui3d.components.PotModelComponent;
import kogasastudio.ashihara.inventory.container.PotMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * 土锅 3D 容器屏幕。
 *
 * <p>职责：
 * <ul>
 *   <li>组装 {@link PotModelComponent}、{@link PotLidComponent}、4 个 {@link ItemSlotComponent}</li>
 *   <li>物品交互、carried-item 渲染、C↔S 同步全部由父类 {@link ContainerScreen3D} 处理</li>
 * </ul>
 */
public class PotScreen extends ContainerScreen3D<PotMenu>
{
    protected PotModelComponent potModelComponent;

    /** 由 MenuScreens 工厂调用（MenuType 绑定时传入 Inventory 和 title，此处忽略两者）。 */
    public PotScreen(PotMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, Component.empty());
    }

    @Override
    public void init()
    {
        this.clearComponents();

        // 主模型（土锅 + 锅盖）
        this.potModelComponent = new PotModelComponent(
                this.width / 2.0f,
                this.height / 2.0f + 26.0f,
                64.0f
        );
        PotLidComponent lidComponent = new PotLidComponent(this.potModelComponent);
        this.potModelComponent.addChild(lidComponent);

        // 4 个食材槽位，绑定到 PotMenu 的 Slot 0-3 及对应骨骼 item_slot_0..3
        for (int i = 0; i < PotMenu.INGREDIENT_SLOTS; i++)
        {
            ItemSlotComponent slot = new ItemSlotComponent(
                    this.potModelComponent.getModel(),
                    "item_slot_" + i,
                    this.menu.getSlot(i)
            );
            this.potModelComponent.addChild(slot);
        }

        this.addComponent(this.potModelComponent);

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        // 背景
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC050505);
        // 组件 + carried-item（由父类处理）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
