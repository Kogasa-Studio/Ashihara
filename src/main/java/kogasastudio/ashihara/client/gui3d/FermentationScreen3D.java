package kogasastudio.ashihara.client.gui3d;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui3d.components.FluidSlotComponent;
import kogasastudio.ashihara.client.gui3d.components.ItemSlotComponent;
import kogasastudio.ashihara.client.gui3d.components.ModelComponent;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.inventory.container.FermentationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public class FermentationScreen3D extends ContainerScreen3D<FermentationScreen>
{
    protected FermentationMenu menu;
    protected Inventory playerInventory;
    public final SimpleInternalControlGeoModel BG_MODEL = new SimpleInternalControlGeoModel("fermentation_background", "textures/gui/fermentation_background.png");
    public final SimpleInternalControlGeoModel VAT_MODEL = new SimpleInternalControlGeoModel("fermentation_vat_gui", "textures/block/fermentation_vat.png");
    public static final Identifier INV_BG = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/player_inventory.png");
    protected ModelComponent bg;
    protected ModelComponent vat;

    private static final BiConsumer<PoseStack, OBB> itemTranslate = (poseStack, obb) ->
    {
        Vector3f t = new Vector3f(obb.maxXYZ()).min(obb.minXYZ()).mul(1f);
        poseStack.translate(t.x()+1.5/16, t.y()+1.5/16, t.z()+1.5/16);
        poseStack.mulPose(Axis.YP.rotationDegrees(0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(0f));
        poseStack.scale(0.25f, 0.25f, 0.25f);
    };

    public FermentationScreen3D(FermentationScreen containerScreen, FermentationMenu menu, Inventory playerInventory, Component title)
    {
        super(containerScreen, title);
        this.menu = menu;
        this.playerInventory = playerInventory;
    }

    @Override
    public void init()
    {
        this.bg = new ModelComponent(this.BG_MODEL, true, new Matrix4f().translate(32f, 0f, 64f).scale(-64f, -64f, 64f));
        this.vat = new ModelComponent(this.VAT_MODEL, true, new Matrix4f().scale(64f, -64f, 64f).translate(-0.75f, 0f, 0f));
        for (int i = 0; i < this.menu.blockEntity.inventory.size(); i++)
        {
            ItemSlotComponent slot = new ItemSlotComponent(this.vat.getModel(), "item_slot_" + i, this.menu.getSlot(i));
            slot.withItemTranslate(itemTranslate);
            this.vat.addChild(slot);
        }

        FluidSlotComponent fluidSlot = new FluidSlotComponent(this.vat.getModel(), "fluid_slot", this.menu.blockEntity.getBlockPos(), this).withTank(() -> this.menu.blockEntity.getTank());
        this.vat.addChild(fluidSlot);

        this.addComponent(this.bg);
        this.addComponent(this.vat);

        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(INV_BG, x0, y0, x1, y1, 0, u1, 0, v1);
    }
}
