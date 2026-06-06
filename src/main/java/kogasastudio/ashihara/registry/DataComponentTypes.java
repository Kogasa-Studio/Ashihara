package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponentTypes
{
    public static final DeferredRegister<DataComponentType<?>> DC_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ashihara.MODID);

    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DC_TYPES.register("fluid_content", () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());
}
