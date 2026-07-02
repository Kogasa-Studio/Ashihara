package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.component.DataComponentType;
import com.mojang.serialization.Codec;
import kogasastudio.ashihara.datacomponent.ChopsticksFood;
import kogasastudio.ashihara.datacomponent.PickleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public class DataComponentTypes
{
    public static final DeferredRegister<DataComponentType<?>> DC_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ashihara.MODID);

    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DC_TYPES.register("fluid_content", () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<Integer>> CHOP_LEFT = DC_TYPES.register("chop_left", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<ChopsticksFood>> CHOPSTICKS_FOOD = DC_TYPES.register("chopsticks_food", () -> DataComponentType.<ChopsticksFood>builder().persistent(ChopsticksFood.CODEC).networkSynchronized(ChopsticksFood.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<Integer>> MAX_BITES = DC_TYPES.register("max_bites", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<PickleType>> PICKLE_TYPE = DC_TYPES.register("pickle_type", () -> DataComponentType.<PickleType>builder().persistent(PickleType.CODEC).networkSynchronized(PickleType.STREAM_CODEC).build());
}
