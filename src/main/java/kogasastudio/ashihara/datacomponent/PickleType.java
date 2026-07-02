package kogasastudio.ashihara.datacomponent;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.function.IntFunction;

public enum PickleType implements StringRepresentable
{
    SALT(0, "salt", 0x9EEACC, new MobEffectInstance(MobEffects.WATER_BREATHING, 2400, 0)),
    BRAN(1, "bran", 0xFFC780, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400, 0));

    private static final IntFunction<PickleType> BY_ID = ByIdMap.continuous(PickleType::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<PickleType> CODEC = StringRepresentable.fromEnum(PickleType::values);
    public static final StreamCodec<ByteBuf, PickleType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PickleType::id);

    private final int id;
    private final String name;
    private final int color;
    private final MobEffectInstance effect;

    PickleType(int id, String name, int color, MobEffectInstance effect)
    {
        this.id = id;
        this.name = name;
        this.color = color;
        this.effect = effect;
    }

    public int id() { return id; }
    public int getColor() { return color; }
    public MobEffectInstance getEffect() { return effect; }

    @Override public String getSerializedName() { return name; }
}
