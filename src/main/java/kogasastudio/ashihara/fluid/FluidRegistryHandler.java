package kogasastudio.ashihara.fluid;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class FluidRegistryHandler
{
    public static final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("misc/underwater"),
            WATER_STILL = Identifier.withDefaultNamespace("block/water_still"),
            WATER_FLOW = Identifier.withDefaultNamespace("block/water_flow"),
            WATER_OVERLAY = Identifier.withDefaultNamespace("block/water_overlay");

    public static final Identifier PORRIDGE_STILL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "fluid/porridge");
    public static final Identifier PORRIDGE_FLOW = Identifier.fromNamespaceAndPath(Ashihara.MODID, "fluid/porridge_flow");

    public static final Identifier MILK_STILL = Identifier.fromNamespaceAndPath(Ashihara.MODID, "fluid/milky_liquid_still");
    public static final Identifier MILK_FLOW = Identifier.fromNamespaceAndPath(Ashihara.MODID, "fluid/milky_liquid_flow");

    private static BaseFlowingFluid.Properties getBasicFluidProp(Supplier<FluidType> type, Supplier<FlowingFluid> source, Supplier<FlowingFluid> flowing, Supplier<? extends LiquidBlock> block, Supplier<? extends Item> bucket)
    {
        return new BaseFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket).slopeFindDistance(3).explosionResistance(100F);
    }

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Ashihara.MODID);

    public static final Supplier<FlowingFluid> SOY_MILK = FLUIDS.register("soy_milk", () -> new BaseFlowingFluid.Source(FluidRegistryHandler.SOY_MILK_PROP));

    public static final Supplier<FlowingFluid> SOY_MILK_FLOWING = FLUIDS.register("soy_milk_flowing", () -> new BaseFlowingFluid.Flowing(FluidRegistryHandler.SOY_MILK_PROP));

    public static final Supplier<FlowingFluid> OIL = FLUIDS.register("oil", () -> new BaseFlowingFluid.Source(FluidRegistryHandler.OIL_PROP));
    public static final Supplier<FlowingFluid> OIL_FLOWING = FLUIDS.register("oil_flowing", () -> new BaseFlowingFluid.Flowing(FluidRegistryHandler.OIL_PROP));

    public static final Supplier<FlowingFluid> RICE_PORRIDGE = FLUIDS.register("rice_porridge", () -> new BaseFlowingFluid.Source(FluidRegistryHandler.RICE_PORRIDGE_PROP));
    public static final Supplier<FlowingFluid> RICE_PORRIDGE_FLOWING = FLUIDS.register("rice_porridge_flowing", () -> new BaseFlowingFluid.Flowing(FluidRegistryHandler.RICE_PORRIDGE_PROP));

    public static BaseFlowingFluid.Properties SOY_MILK_PROP = getBasicFluidProp(AshiharaFluidTypes.TYPE_SOY_MILK, SOY_MILK, SOY_MILK_FLOWING, Blocks.SOY_MILK_BLOCK, Items.SOY_MILK_BUCKET);
    public static BaseFlowingFluid.Properties OIL_PROP = getBasicFluidProp(AshiharaFluidTypes.TYPE_OIL, OIL, OIL_FLOWING, Blocks.OIL_BLOCK, Items.OIL_BUCKET);
    public static BaseFlowingFluid.Properties RICE_PORRIDGE_PROP = getBasicFluidProp(AshiharaFluidTypes.TYPE_RICE_PORRIDGE, RICE_PORRIDGE, RICE_PORRIDGE_FLOWING, Blocks.RICE_PORRIDGE_BLOCK, Items.RICE_PORRIDGE_BUCKET);
    //oil 168 244 233 132

    public static class AshiharaFluidTypes
    {
        public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Ashihara.MODID);

        public static final Supplier<FluidType> TYPE_SOY_MILK = TYPES.register("soy_milk", () -> createWaterLike("block.ashihara.soy_milk"));

        public static final Supplier<FluidType> TYPE_OIL = TYPES.register("oil", () -> createStandard("block.ashihara.oil", 970, 512, false));
        public static final Supplier<FluidType> TYPE_RICE_PORRIDGE = TYPES.register("rice_porridge", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("block.ashihara.rice_porridge")
                            .density(1100)
                            .viscosity(2048)
                            .canExtinguish(false)
                            .motionScale(0.007D)
                            .fallDistanceModifier(0f)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                            .canHydrate(false)
                            .supportsBoating(false)));

        private static FluidType createWaterLike(String description)
        {
            return createStandard(description, 1024, 1024, true);
        }

        private static FluidType createStandard(String description, int density, int viscosity, boolean canExtinguish)
        {
            return new FluidType(
                    FluidType.Properties.create()
                            .descriptionId(description)
                            .density(density)
                            .viscosity(viscosity)
                            .canExtinguish(canExtinguish)
                            .fallDistanceModifier(0f)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                            .canHydrate(false)
                            .supportsBoating(true));
        }
    }
}
