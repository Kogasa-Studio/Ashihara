package kogasastudio.ashihara.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;


public class GenericParticleData implements ParticleOptions {
    public static final Deserializer<GenericParticleData> DESERIALIZER = new Deserializer<GenericParticleData>() {
        @Override
        public GenericParticleData fromCommand(ParticleType<GenericParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double speedX = reader.readDouble();
            reader.expect(' ');
            double speedY = reader.readDouble();
            reader.expect(' ');
            double speedZ = reader.readDouble();
            reader.expect(' ');
            float diameter = reader.readFloat();
            return new GenericParticleData(new Vec3(speedX, speedY, speedZ), diameter, particleTypeIn);
        }

        @Override
        public GenericParticleData fromNetwork(ParticleType<GenericParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            double speedX = buffer.readDouble();
            double speedY = buffer.readDouble();
            double speedZ = buffer.readDouble();
            float diameter = buffer.readFloat();
            return new GenericParticleData(new Vec3(speedX, speedY, speedZ), diameter, particleTypeIn);
        }
    };
    private final Vec3 speed;
    private final float diameter;
    private final ParticleType<?> type;

    public GenericParticleData(Vec3 speed, float diameter, ParticleType<?> type) {
        this.speed = speed;
        this.diameter = diameter;
        this.type = type;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.speed.x);
        buffer.writeDouble(this.speed.y);
        buffer.writeDouble(this.speed.z);
        buffer.writeFloat(this.diameter);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f",
                this.getType().getRegistryName(), diameter, speed.x(), speed.y(), speed.z());
    }
}
