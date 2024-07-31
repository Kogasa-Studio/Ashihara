package kogasastudio.ashihara.fluid;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class FluidRegistryHandler
{
    private static final ResourceLocation UNDERWATER_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png"),
            WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still"),
            WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow"),
            WATER_OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

    public static final ResourceLocation MILK_STILL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/fluid/milky_liquid_still.png");
    public static final ResourceLocation MILK_FLOW = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/fluid/milky_liquid_flow.png");

    private static BaseFlowingFluid.Properties getBasicFluidProp(Supplier<FluidType> type, Supplier<FlowingFluid> source, Supplier<FlowingFluid> flowing, Supplier<? extends LiquidBlock> block, Supplier<? extends Item> bucket)
    {
        return new BaseFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket).slopeFindDistance(3).explosionResistance(100F);
    }

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Ashihara.MODID);

    public static final Supplier<FlowingFluid> SOY_MILK = FLUIDS.register("soy_milk", () -> new BaseFlowingFluid.Source(FluidRegistryHandler.SOY_MILK_PROP));

    public static final Supplier<FlowingFluid> SOY_MILK_FLOWING = FLUIDS.register("soy_milk_flowing", () -> new BaseFlowingFluid.Flowing(FluidRegistryHandler.SOY_MILK_PROP));

    public static final Supplier<FlowingFluid> OIL = FLUIDS.register("oil", () -> new BaseFlowingFluid.Source(FluidRegistryHandler.OIL_PROP));
    public static final Supplier<FlowingFluid> OIL_FLOWING = FLUIDS.register("oil_flowing", () -> new BaseFlowingFluid.Flowing(FluidRegistryHandler.OIL_PROP));

    public static BaseFlowingFluid.Properties SOY_MILK_PROP = getBasicFluidProp(AshiharaFluidTypes.TYPE_SOY_MILK, SOY_MILK, SOY_MILK_FLOWING, BlockRegistryHandler.SOY_MILK_BLOCK, ItemRegistryHandler.SOY_MILK_BUCKET);
    public static BaseFlowingFluid.Properties OIL_PROP = getBasicFluidProp(AshiharaFluidTypes.TYPE_OIL, OIL, OIL_FLOWING, BlockRegistryHandler.OIL_BLOCK, ItemRegistryHandler.OIL_BUCKET);
    //oil 168 244 233 132

    public class AshiharaFluidTypes
    {
        public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Ashihara.MODID);

        public static final Supplier<FluidType> TYPE_SOY_MILK = TYPES.register
                (
                        "soy_milk", () -> createWaterLike("block.ashihara.soy_milk", WATER_STILL, WATER_FLOW, WATER_OVERLAY, UNDERWATER_LOCATION, FastColor.ARGB32.color(255, 255, 253, 225))
                );

        public static final Supplier<FluidType> TYPE_OIL = TYPES.register
                (
                        "oil", () -> createStandard("block.ashihara.oil", 970, 512, false, WATER_STILL, WATER_FLOW, WATER_OVERLAY, UNDERWATER_LOCATION, FastColor.ARGB32.color(255, 246, 223, 12))
                );

        private static FluidType createWaterLike(String description, @Nullable ResourceLocation still, @Nullable ResourceLocation flowing, @Nullable ResourceLocation overlay, @Nullable ResourceLocation renderOverlay, int color)
        {
            return createStandard(description, 1024, 1024, true, still, flowing, overlay, renderOverlay, color);
        }

        private static FluidType createStandard(String description, int density, int viscosity, boolean canExtinguish, @Nullable ResourceLocation still, @Nullable ResourceLocation flowing, @Nullable ResourceLocation overlay, @Nullable ResourceLocation renderOverlay, int color)
        {
            return new FluidType
                (
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
                                .supportsBoating(true)
                )
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept
                    (
                        new IClientFluidTypeExtensions()
                        {
                            @Override
                            public int getTintColor() {return color;}

                            @Override
                            public ResourceLocation getStillTexture() {return still == null ? IClientFluidTypeExtensions.super.getStillTexture() : still;}

                            @Override
                            public ResourceLocation getFlowingTexture() {return flowing == null ? IClientFluidTypeExtensions.super.getFlowingTexture() : flowing;}

                            @Override
                            public @Nullable ResourceLocation getOverlayTexture() {return overlay == null ? IClientFluidTypeExtensions.super.getOverlayTexture() : overlay;}

                            @Override
                            public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc)
                            {
                                return renderOverlay == null ? IClientFluidTypeExtensions.super.getRenderOverlayTexture(mc) : renderOverlay;
                            }
                        }
                    );
                }
            };
        }
    }
}
