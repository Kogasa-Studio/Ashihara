package kogasastudio.ashihara.sounds;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SoundEvents
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Ashihara.MODID);

    public static final Supplier<SoundEvent> UNTHRESH = register("unthresh");

    private static Supplier<SoundEvent> register(String key)
    {
        return SOUNDS.register(key, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, key)));
    }
}
