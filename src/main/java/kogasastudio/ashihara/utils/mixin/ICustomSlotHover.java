package kogasastudio.ashihara.utils.mixin;

import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

/**
 * Optional bridge for replacing container hover slot detection.
 *
 * <p>Return null to keep vanilla hover resolution for the current query.
 */
public interface ICustomSlotHover
{
    @Nullable Slot ashihara$findCustomHoveredSlot(double mouseX, double mouseY);
}

