package kogasastudio.ashihara.client.particles;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ParticleRegistryHandler
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Ashihara.MODID);

    public static final Supplier<SimpleParticleType> RICE = PARTICLE_TYPES.register("rice", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> SAKURA = PARTICLE_TYPES.register("sakura", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> MAPLE_LEAF = PARTICLE_TYPES.register("maple_leaf", () -> new SimpleParticleType(false));
}
