package kogasastudio.ashihara.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ChopsticksFood(ItemStack food, int bitesPerItem)
{
    public static final ChopsticksFood EMPTY = new ChopsticksFood(ItemStack.EMPTY, 0);
    public static final Codec<ChopsticksFood> CODEC = RecordCodecBuilder.create(instance -> instance.group
    (
       ItemStack.CODEC.fieldOf("food").forGetter(ChopsticksFood::food),
        Codec.INT.fieldOf("bites_per_item").forGetter(ChopsticksFood::bitesPerItem)
    ).apply(instance, ChopsticksFood::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChopsticksFood> STREAM_CODEC = StreamCodec.composite
    (
        ItemStack.STREAM_CODEC, ChopsticksFood::food,
        ByteBufCodecs.VAR_INT, ChopsticksFood::bitesPerItem,
        ChopsticksFood::new
    );
}
