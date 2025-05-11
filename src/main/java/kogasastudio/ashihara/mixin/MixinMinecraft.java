package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import kogasastudio.ashihara.item.IHasPreSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V"))
    private void cancelPreSwingItemSwing(CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack, @Local InputEvent.InteractionKeyMappingTriggered event)
    {
        if (itemStack.getItem() instanceof IHasPreSwing) event.setSwingHand(false);
    }
}
