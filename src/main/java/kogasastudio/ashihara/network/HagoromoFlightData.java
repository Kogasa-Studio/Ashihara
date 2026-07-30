package kogasastudio.ashihara.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HagoromoFlightData(boolean hasFlight, boolean hagoromoActive, int airborneTicks, int cooldownTicks)
{
    public static final MapCodec<HagoromoFlightData> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.BOOL.fieldOf("hasFlight").forGetter(HagoromoFlightData::hasFlight),
            Codec.BOOL.fieldOf("hagoromoActive").forGetter(HagoromoFlightData::hagoromoActive),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("airborneTicks").forGetter(HagoromoFlightData::airborneTicks),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("cooldownTicks").forGetter(HagoromoFlightData::cooldownTicks)
        ).apply(instance, HagoromoFlightData::new)
    );
}
