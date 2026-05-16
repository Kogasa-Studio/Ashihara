package kogasastudio.ashihara.mixin.geckolib;

import com.geckolib.animation.object.EasingType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

/**
 * GeckoLib's EasingType lookup is case-sensitive, while many Bedrock-style
 * animation files use camelCase values such as "easeInOutBack".
 *
 * <p>Normalize easing names to lower-case before map lookup so legacy
 * animations keep their intended curves instead of silently falling back to LINEAR.
 */
@Mixin(value = EasingType.class, remap = false)
public interface MixinEasingType
{
    @Inject(
        method = "fromString",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void ashihara$normalizeEasingName(String name, CallbackInfoReturnable<EasingType> cir)
    {
        if (name == null || name.isEmpty())
        {
            cir.setReturnValue(EasingType.LINEAR);
            return;
        }

        String normalizedName = name.toLowerCase(Locale.ROOT);
        cir.setReturnValue(EasingType.EASING_TYPES.getOrDefault(normalizedName, EasingType.LINEAR));
    }
}

