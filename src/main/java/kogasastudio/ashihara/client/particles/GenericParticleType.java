package kogasastudio.ashihara.client.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;

import static kogasastudio.ashihara.client.particles.GenericParticleData.DESERIALIZER;

public class GenericParticleType extends ParticleType<GenericParticleData> {
    public GenericParticleType() {
        super(false, DESERIALIZER);
    }

    @Override
    public Codec<GenericParticleData> codec() {
        return Codec.unit(new GenericParticleData(new Vec3(0, 0, 0), 0, null));
    }
}
