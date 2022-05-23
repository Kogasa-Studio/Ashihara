package kogasastudio.ashihara.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Locale;

import net.minecraft.particles.IParticleData.IDeserializer;

public class GenericParticleData implements IParticleData
{
    private final Vector3d speed;
    private final float diameter;
    private final ParticleType<?> type;

    public GenericParticleData(Vector3d speed, float diameter, ParticleType<?> type)
    {
        this.speed = speed;
        this.diameter = diameter;
        this.type = type;
    }

    public static final IDeserializer<GenericParticleData> DESERIALIZER = new IDeserializer<GenericParticleData>()
    {
        @Override
        public GenericParticleData fromCommand(ParticleType<GenericParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            double speedX = reader.readDouble();
            reader.expect(' ');
            double speedY = reader.readDouble();
            reader.expect(' ');
            double speedZ = reader.readDouble();
            reader.expect(' ');
            float diameter = reader.readFloat();
            return new GenericParticleData(new Vector3d(speedX, speedY, speedZ), diameter, particleTypeIn);
        }

        @Override
        public GenericParticleData fromNetwork(ParticleType<GenericParticleData> particleTypeIn, PacketBuffer buffer)
        {
            double speedX = buffer.readDouble();
            double speedY = buffer.readDouble();
            double speedZ = buffer.readDouble();
            float diameter = buffer.readFloat();
            return new GenericParticleData(new Vector3d(speedX, speedY, speedZ), diameter, particleTypeIn);
        }
    };

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }

    @Override
    public void writeToNetwork(PacketBuffer buffer)
    {
        buffer.writeDouble(this.speed.x);
        buffer.writeDouble(this.speed.y);
        buffer.writeDouble(this.speed.z);
        buffer.writeFloat(this.diameter);
    }

    @Override
    public String writeToString()
    {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f",
        this.getType().getRegistryName(), diameter, speed.x(), speed.y(), speed.z());
    }
}
