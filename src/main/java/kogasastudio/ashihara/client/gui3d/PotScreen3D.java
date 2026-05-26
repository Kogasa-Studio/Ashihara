package kogasastudio.ashihara.client.gui3d;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui3d.components.*;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.BubbleModel;
import kogasastudio.ashihara.client.models.geo.PotModel;
import kogasastudio.ashihara.inventory.container.PotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * 土锅 3D 容器屏幕。
 *
 * <p>职责：
 * <ul>
 *   <li>组装 {@link PotModelComponent}、{@link PotLidComponent}、4 个 {@link ItemSlotComponent}</li>
 *   <li>物品交互、carried-item 渲染、C↔S 同步全部由父类 {@link ContainerScreen3D} 处理</li>
 * </ul>
 */
public class PotScreen3D extends ContainerScreen3D<PotScreen>
{
    public final PotModel potModel = new PotModel("block/pot", "textures/block/pot.png", "gui/pot");
    public final BubbleModel bubbleModel = new BubbleModel("bubble", "textures/gui/bubble.png", "gui/bubble");
    public static final Identifier INV_BG = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/player_inventory.png");
    protected PotModelComponent potModelComponent;
    protected BubbleComponent bubbleComponent;
    protected PotMenu menu;
    protected Inventory playerInventory;
    private static final int INV_BG_WIDTH = 176;
    private static final int INV_BG_HEIGHT = 90;
    private static final int BG_OFFSET_X = 0;
    private static final int BG_OFFSET_Y = 39;
    private static final float u1 = INV_BG_WIDTH / 256f, v1 = INV_BG_HEIGHT / 256f;
    private int x0, y0, x1, y1;

    /**
     * 由 MenuScreens 工厂调用（MenuType 绑定时传入 Inventory 和 title，此处忽略两者）。
     */
    public PotScreen3D(PotScreen screen, PotMenu menu, Inventory playerInventory, Component title)
    {
        super(screen, title);
        this.menu = menu;
        this.playerInventory = playerInventory;
    }

    @Override
    public void init()
    {
        this.clearComponents();
        x0 = this.width / 2 - INV_BG_WIDTH / 2 + BG_OFFSET_X;
        x1 = this.width / 2 + INV_BG_WIDTH / 2 + BG_OFFSET_X;
        y0 = this.height / 2 - INV_BG_HEIGHT / 2 + BG_OFFSET_Y;
        y1 = this.height / 2 + INV_BG_HEIGHT / 2 + BG_OFFSET_Y;

        // 主模型（土锅 + 锅盖）
        this.potModelComponent = new PotModelComponent(
        potModel,
        64.0f,
        new Matrix4f().scale(64f, -64f, 64f).translate(0f, 0, 0f).rotateXYZ(-45, -45, 0)
        );
        PotLidComponent lidComponent = new PotLidComponent(this.potModelComponent);
        this.potModelComponent.addChild(lidComponent);

        // 4 个食材槽位，绑定到 PotMenu 的 Slot 0-3 及对应骨骼 item_slot_0..3
        for (int i = 0; i < PotMenu.INGREDIENT_SLOTS; i++)
        {
            ItemSlotComponent slot = new ItemSlotComponent(this.potModelComponent.getModel(), "item_slot_" + i, this.menu.getSlot(i));
            this.potModelComponent.addChild(slot);
        }
        ItemSlotComponent output = new ItemSlotComponent(this.potModelComponent.getModel(), "item_slot_4", this.menu.getSlot(4));
        this.potModelComponent.addChild(output);

        // 流体槽位，绑定到 fluid_display 骨骼
        FluidSlotComponent fluidSlot = new FluidSlotComponent(this.potModelComponent.getModel(), "fluid_display", this.menu.blockEntity.getBlockPos(), this);
        this.potModelComponent.addChild(fluidSlot);

        this.bubbleComponent = new BubbleComponent(this.bubbleModel, new Matrix4f().scale(16f, -16f, 16f).translate(1.5f, 1.0f, 0).rotateXYZ(0, (float) Math.toRadians(180), 0));

        this.addComponent(this.bubbleComponent);
        this.addComponent(this.potModelComponent);
        this.bubbleComponent.model().init(this.minecraft.player, this.menu.blockEntity.getAvailableRecipe() != null);
        ItemDisplayComponent output_display = new ItemDisplayComponent(() -> this.menu.blockEntity.getAvailableOutput(), () ->
        {
            List<OBB> obb = this.bubbleComponent.getBoneCollisionBoxes("item_slot_0");
            if (obb.isEmpty()) return new Matrix4f();
            OBB o = obb.getFirst();
            Matrix4f mat = new Matrix4f(o.pose());
            Vector3f t = new Vector3f(o.maxXYZ()).min(o.minXYZ());
            mat.translate((float) (t.x()+0.5), (float) (t.y()+0.5), (float) (t.z()+0.5));
            mat.translate(0, 0, 0.5f);
            return mat;
        });
        this.bubbleComponent.addChild(output_display);

        super.init();
    }

    public void setChanged()
    {
        if (this.menu.blockEntity.getAvailableRecipe() != null)
        {
            this.bubbleComponent.appear();
        }
        else
        {
            this.bubbleComponent.hide();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(INV_BG, x0, y0, x1, y1, 0, u1, 0, v1);
        /*// PotScreen3D 作为统一空间测试对象：输出 GUI->PiP 转换后的射线数据与命中距离。
        Ray mouseRay = this.createMouseRay(mouseX, mouseY);
        float nearestHit = this.potModelComponent == null ? -1.0f : this.potModelComponent.rayHitDistance(mouseRay);

        graphics.text(Minecraft.getInstance().font,
                      String.format("gui=(%d,%d) ray=(%.1f,%.1f,%.1f)", mouseX, mouseY, mouseRay.origin().x, mouseRay.origin().y, mouseRay.origin().z),
                      6, 6, 0xFF196884);
        graphics.text(Minecraft.getInstance().font,
                      String.format("pickScale gui=%.2f hit=%.3f", this.getPickingGuiScale(), nearestHit),
                      6, 18, 0xFF88CCAA);*/
    }
}
