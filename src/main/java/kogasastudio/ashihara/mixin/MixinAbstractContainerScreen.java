package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import kogasastudio.ashihara.utils.mixin.ICustomSlotHover;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen
{
    @ModifyReturnValue(
        method = "getHoveredSlot(DD)Lnet/minecraft/world/inventory/Slot;",
        at = @At("RETURN")
    )
    private Slot ashihara$mergeCustomHover(Slot vanillaResult, double mouseX, double mouseY)
    {
        Object screen = this;
        if (screen instanceof ICustomSlotHover customSlotHover)
        {
            Slot customResult = customSlotHover.ashihara$findCustomHoveredSlot(mouseX, mouseY);
            if (customResult != null)
            {
                return customResult;
            }
        }

        return vanillaResult;
    }
}


