package kogasastudio.ashihara.fluid;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static net.minecraft.util.SoundEvents.ITEM_BUCKET_EMPTY;
import static net.minecraft.util.SoundEvents.ITEM_BUCKET_FILL;

public class FluidRegistryHandler
{
    public static final ResourceLocation TEXTURE_WATER_STILL = new ResourceLocation(Ashihara.MODID, "fluid/milky_liquid_still");
    public static final ResourceLocation TEXTURE_WATER_FLOW = new ResourceLocation(Ashihara.MODID, "fluid/milky_liquid_flow");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Ashihara.MODID);

    public static final RegistryObject<FlowingFluid> SOY_MILK = FLUIDS.register("soy_milk", () -> new ForgeFlowingFluid.Source(FluidRegistryHandler.SOY_MILK_PROP));
    public static final RegistryObject<FlowingFluid> SOY_MILK_FLOWING = FLUIDS.register("soy_milk_flowing", () -> new ForgeFlowingFluid.Flowing(FluidRegistryHandler.SOY_MILK_PROP));

    public static ForgeFlowingFluid.Properties SOY_MILK_PROP = getBasicFluidProp(SOY_MILK, SOY_MILK_FLOWING, ColorHelper.PackedColor.packColor(255, 255, 253, 225), BlockRegistryHandler.SOY_MILK_BLOCK, ItemRegistryHandler.SOY_MILK_BUCKET);

    private static ForgeFlowingFluid.Properties getBasicFluidProp(RegistryObject<FlowingFluid> source, RegistryObject<FlowingFluid> flowing, int color, Supplier<? extends FlowingFluidBlock> block, Supplier<? extends Item> bucket)
    {
        return new ForgeFlowingFluid.Properties(source, flowing,
            FluidAttributes.builder(TEXTURE_WATER_STILL, TEXTURE_WATER_FLOW).sound(ITEM_BUCKET_FILL, ITEM_BUCKET_EMPTY)
            .color(color).viscosity(1000))
            .block(block).bucket(bucket).slopeFindDistance(3).explosionResistance(100F);
    }

}
