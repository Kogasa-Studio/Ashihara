package kogasastudio.ashihara.fluid;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class FluidRegistryHandler {
    public static final ResourceLocation TEXTURE_WATER_STILL = new ResourceLocation(Ashihara.MODID, "fluid/water_still");
    public static final ResourceLocation TEXTURE_WATER_FLOW = new ResourceLocation(Ashihara.MODID, "fluid/water_flow");

    public static final ResourceLocation TEXTURE_MILK_STILL = new ResourceLocation(Ashihara.MODID, "fluid/milky_liquid_still");
    public static final ResourceLocation TEXTURE_MILK_FLOW = new ResourceLocation(Ashihara.MODID, "fluid/milky_liquid_flow");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Ashihara.MODID);

    private static ForgeFlowingFluid.Properties getBasicFluidProp(RegistryObject<FlowingFluid> source, RegistryObject<FlowingFluid> flowing, int color, Supplier<? extends LiquidBlock> block, Supplier<? extends Item> bucket, ResourceLocation STILL, ResourceLocation FLOWING) {
        return new ForgeFlowingFluid.Properties
                (
                        source, flowing,
                        FluidAttributes.builder(STILL, FLOWING)
                                .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                                .color(color).viscosity(1000)
                )
                .block(block).bucket(bucket).slopeFindDistance(3).explosionResistance(100F);
    }    public static final RegistryObject<FlowingFluid> SOY_MILK = FLUIDS.register("soy_milk", () -> new ForgeFlowingFluid.Source(FluidRegistryHandler.SOY_MILK_PROP));

    private static ForgeFlowingFluid.Properties getBasicFluidProp(RegistryObject<FlowingFluid> source, RegistryObject<FlowingFluid> flowing, int color, Supplier<? extends LiquidBlock> block, Supplier<? extends Item> bucket) {
        return getBasicFluidProp(source, flowing, color, block, bucket, TEXTURE_WATER_STILL, TEXTURE_WATER_FLOW);
    }    public static final RegistryObject<FlowingFluid> SOY_MILK_FLOWING = FLUIDS.register("soy_milk_flowing", () -> new ForgeFlowingFluid.Flowing(FluidRegistryHandler.SOY_MILK_PROP));

    public static final RegistryObject<FlowingFluid> OIL = FLUIDS.register("oil", () -> new ForgeFlowingFluid.Source(FluidRegistryHandler.OIL_PROP));
    public static final RegistryObject<FlowingFluid> OIL_FLOWING = FLUIDS.register("oil_flowing", () -> new ForgeFlowingFluid.Flowing(FluidRegistryHandler.OIL_PROP));

    public static ForgeFlowingFluid.Properties SOY_MILK_PROP = getBasicFluidProp(SOY_MILK, SOY_MILK_FLOWING, FastColor.ARGB32.color(255, 255, 253, 225), BlockRegistryHandler.SOY_MILK_BLOCK, ItemRegistryHandler.SOY_MILK_BUCKET, TEXTURE_MILK_STILL, TEXTURE_MILK_FLOW);
    public static ForgeFlowingFluid.Properties OIL_PROP = getBasicFluidProp(OIL, OIL_FLOWING, FastColor.ARGB32.color(255, 246, 223, 12), BlockRegistryHandler.OIL_BLOCK, ItemRegistryHandler.OIL_BUCKET);
    //oil 168 244 233 132




}
