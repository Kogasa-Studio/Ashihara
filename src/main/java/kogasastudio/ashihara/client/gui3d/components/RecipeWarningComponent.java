package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.models.geo.ToastModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RecipeWarningComponent extends ToastComponent
{
    public RecipeWarningComponent(Matrix4f presetTransform, Supplier<List<Component>> tooltipSupplier)
    {
        super(new ToastModel("warning_sign", "textures/gui/warning.png", "gui/warning_sign"), true, presetTransform);
        this.tooltip = tooltipSupplier;
    }

    @Override
    public List<OBB> getCollisionBoxes()
    {
        List<OBB> boxes = this.getBoneCollisionBoxes("bounding_box");
        return boxes.isEmpty() ? Collections.emptyList() : boxes;
    }
}
