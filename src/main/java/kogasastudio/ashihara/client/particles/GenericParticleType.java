package kogasastudio.ashihara.client.particles;

import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;

import static kogasastudio.ashihara.client.particles.GenericParticleData.DESERIALIZER;

public class GenericParticleType extends ParticleType<GenericParticleData>
{
    public GenericParticleType() {super(false, DESERIALIZER);}

    @Override
    public Codec<GenericParticleData> func_230522_e_()
    {
        return Codec.unit(new GenericParticleData(new Vector3d(0, 0, 0), 0, null));
    }
}
