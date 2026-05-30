package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.IToast;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Supplier;

public class ToastComponent extends ModelComponent
{
    protected static final Player p = Minecraft.getInstance().player;
    public boolean isActive = false;

    @Nullable
    public Supplier<Boolean> appearanceCondition;
    @Nullable
    public Supplier<Boolean> disappearanceCondition;
    @Nullable
    public Supplier<List<Component>> tooltip;
    @Nullable
    public Supplier<List<OBB>> boundingBox;

    public <M extends SimpleInternalControlGeoModel & IToast> ToastComponent(M model)
    {
        super(model);
    }

    public <M extends SimpleInternalControlGeoModel & IToast> ToastComponent(M model, boolean renderModel)
    {
        super(model, renderModel);
    }

    public <M extends SimpleInternalControlGeoModel & IToast> ToastComponent(M model, boolean renderModel, Matrix4f presetTransform)
    {
        super(model, renderModel, presetTransform);
    }

    public ToastComponent withBoundingBox(Supplier<List<OBB>> boundingBox)
    {
        this.boundingBox = boundingBox;
        return this;
    }

    public ToastComponent withTooltip(Supplier<List<Component>> tooltip)
    {
        this.tooltip = tooltip;
        return this;
    }

    public ToastComponent withAppearanceCondition(Supplier<Boolean> appearanceCondition)
    {
        this.appearanceCondition = appearanceCondition;
        return this;
    }

    public ToastComponent withDisappearanceCondition(Supplier<Boolean> disappearanceCondition)
    {
        this.disappearanceCondition = disappearanceCondition;
        return this;
    }

    public ToastComponent withBiCondition(Supplier<Boolean> biCondition)
    {
        this.appearanceCondition = biCondition;
        this.disappearanceCondition = () -> !biCondition.get();
        return this;
    }

    @Override
    public void init()
    {
        super.init();
        if (this.model() != null && this.appearanceCondition != null) this.model().init(p, this.appearanceCondition.get());
    }

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.tooltip != null && this.tooltip.get() != null && !this.tooltip.get().isEmpty()) graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, this.tooltip.get(), (int) mouseX, (int) mouseY);
    }

    @Override
    public List<OBB> getCollisionBoxes()
    {
        if (this.boundingBox != null && !this.boundingBox.get().isEmpty()) return this.boundingBox.get();
        return super.getCollisionBoxes();
    }

    public void appear()
    {
        if (this.isActive) return;
        if (this.model() != null) this.model().intro(p);
        this.isActive = true;
    }

    public void hide()
    {
        if (!this.isActive) return;
        if (this.model() != null) this.model().outro(p);
        this.isActive = false;
    }

    public <M extends SimpleInternalControlGeoModel & IToast> M model()
    {
        return (M) this.model;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.appearanceCondition != null && this.appearanceCondition.get()) appear();
        if (this.disappearanceCondition != null && this.disappearanceCondition.get()) hide();
    }
}
