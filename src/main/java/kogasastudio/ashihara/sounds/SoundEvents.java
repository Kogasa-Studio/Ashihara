package kogasastudio.ashihara.sounds;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ashihara.MODID);

    public static final RegistryObject<SoundEvent> UNTHRESH = register("unthresh");

    private static RegistryObject<SoundEvent> register(String key)
    {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(Ashihara.MODID, key)));
    }
}
