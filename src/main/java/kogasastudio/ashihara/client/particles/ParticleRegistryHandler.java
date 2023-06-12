package kogasastudio.ashihara.client.particles;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistryHandler
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Ashihara.MODID);

    public static final RegistryObject<GenericParticleType> RICE = PARTICLE_TYPES.register("rice", GenericParticleType::new);
    public static final RegistryObject<GenericParticleType> SAKURA = PARTICLE_TYPES.register("sakura", GenericParticleType::new);
    public static final RegistryObject<GenericParticleType> MAPLE_LEAF = PARTICLE_TYPES.register("maple_leaf", GenericParticleType::new);
}
