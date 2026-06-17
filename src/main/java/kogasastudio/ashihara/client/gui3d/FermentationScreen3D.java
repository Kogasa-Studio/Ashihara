package kogasastudio.ashihara.client.gui3d;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui3d.components.*;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.ProgressBarModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.ToastModel;
import kogasastudio.ashihara.helper.RenderHelper;
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
    public final ToastModel bubbleModel = new ToastModel("sign", "textures/gui/sign.png", "gui/sign");
    public final ProgressBarModel progressBarModel = new ProgressBarModel("diamond_progress_bar", "textures/gui/indicator.png", "gui/diamond_progress_bar");
    public static final Identifier INV_BG = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/player_inventory.png");
    protected ModelComponent bg;
    protected ModelComponent vat;
    protected ToastComponent signComponent;
    protected RecipeWarningComponent warningComponent;

    private static final BiConsumer<PoseStack, OBB> itemTranslate = (poseStack, obb) ->
    {
        Vector3f t = new Vector3f(obb.maxXYZ()).min(obb.minXYZ());
        poseStack.translate(t.x()+1.5/16, t.y()+1.5/16, t.z()+1.5/16);
        poseStack.mulPose(Axis.YP.rotationDegrees(0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(0f));
        poseStack.scale(0.25f, 0.25f, -0.25f);
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

        this.signComponent = new ToastComponent(this.bubbleModel, true, new Matrix4f().scale(-64f, -64f, 64f).translate(-0.85f, 0.75f, 0));
        this.signComponent.withBiCondition(() -> this.menu.blockEntity.getAvailableRecipe() != null);
        this.signComponent.withTooltip(() -> this.menu.blockEntity.currentRecipe == null ? null : this.menu.blockEntity.getProductionTooltip());
        this.signComponent.withBoundingBox(() -> this.signComponent.getBoneCollisionBoxes("item_slot"));
        ItemDisplayComponent output_display = new ItemDisplayComponent(() -> this.menu.blockEntity.getAvailableOutput(), () ->
        {
            OBB obb = this.signComponent.getFirstBoneCollisionBox("item_slot");
            if (obb == null) return new Matrix4f();
            return RenderHelper.getOBBCenterTransform(obb, 2).scale(0.25f);
        });
        output_display.withCount(() -> this.menu.blockEntity.getAvailableOutput().getCount() * this.menu.blockEntity.parallel);
        this.signComponent.addChild(output_display);

        FluidDisplayComponent fluid_display = new FluidDisplayComponent(() -> this.menu.blockEntity.getAvailableFluidOutput(), () -> this.signComponent.getFirstBoneCollisionBox("fluid_slot"));
        this.signComponent.addChild(fluid_display);

        ProgressBarComponent progress = new ProgressBarComponent(this.progressBarModel)
        .withTransform(() ->
         {
             var boxes = this.signComponent.getBoneCollisionBoxes("item_slot");
             return boxes.isEmpty() ? null : RenderHelper.getOBBCenterTransform(boxes.getFirst(), 0).scale(0.25f);
         })
        .withProgress(() -> this.menu.blockEntity.getFermentProgress());
        this.signComponent.addChild(progress);

        this.warningComponent = new RecipeWarningComponent(new Matrix4f().scale(-16f, -16f, 16f).translate(3f, 1.0f, 0), () -> this.menu.blockEntity.getUnavailabilityMessages());
        this.warningComponent.withBiCondition(() -> !this.menu.blockEntity.getUnavailabilityMessages().isEmpty());

        this.addComponent(this.bg);
        this.addComponent(this.vat);
        this.addComponent(this.signComponent);
        this.addComponent(this.warningComponent);

        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(INV_BG, x0, y0, x1, y1, 0, u1, 0, v1);
    }
}
