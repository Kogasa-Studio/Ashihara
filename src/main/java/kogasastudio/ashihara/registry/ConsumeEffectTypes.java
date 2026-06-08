package kogasastudio.ashihara.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ConsumeEffectTypes
{
    private ConsumeEffectTypes() {}

    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS =
        DeferredRegister.create(BuiltInRegistries.CONSUME_EFFECT_TYPE, Ashihara.MODID);

    public static final Supplier<ConsumeEffect.Type<SetFire>> SET_FIRE =
        CONSUME_EFFECTS.register("set_fire", () -> new ConsumeEffect.Type<>(SetFire.CODEC, SetFire.STREAM_CODEC));

    public record SetFire(int seconds) implements ConsumeEffect
    {
        public static final MapCodec<SetFire> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(Codec.INT.fieldOf("seconds").forGetter(SetFire::seconds))
                .apply(i, SetFire::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SetFire> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SetFire::seconds, SetFire::new);

        @Override
        public ConsumeEffect.Type<SetFire> getType()
        {
            return SET_FIRE.get();
        }

        @Override
        public boolean apply(Level level, ItemStack stack, LivingEntity user)
        {
            user.setRemainingFireTicks(seconds * 20);
            return true;
        }
    }
}