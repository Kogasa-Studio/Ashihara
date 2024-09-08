package kogasastudio.ashihara.event;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.models.SujikabutoModel;
import kogasastudio.ashihara.client.models.baked.PailModel;
import kogasastudio.ashihara.client.particles.MapleLeafParticle;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.client.particles.RiceParticle;
import kogasastudio.ashihara.client.particles.SakuraParticle;
import kogasastudio.ashihara.client.render.ister.PailISTER;
import kogasastudio.ashihara.client.render.ter.*;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscribeHandler
{
    private static void setRenderType(Block block, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(block, type));
    }

    private static void setRenderType(Fluid fluid, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(fluid, type));
    }

    //设置渲染方式
    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event)
    {
        setRenderType(BlockRegistryHandler.RICE_CROP.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.IMMATURE_RICE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_BLOSSOM.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.MAPLE_LEAVES_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.JINJA_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.STONE_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BONBURI_LAMP.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CANDLESTICK.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.OIL_PLATE_STICK.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_VINES.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_SAKURA.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_MAPLE_LEAVES_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_CHERRY_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_RED_MAPLE_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HOUSE_LIKE_HANGING_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HEXAGONAL_HANGING_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHRYSANTHEMUM.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.WILD_RICE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.REED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SHORTER_REED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HYDRANGEA_BUSH.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.TEA_TREE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.GOLD_FENCE_DECORATION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_FENCE_EXPANSION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SPRUCE_FENCE_EXPANSION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SOY_BEANS.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SWEET_POTATOES.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CUCUMBERS.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.MEAL_TABLE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHARLOTTE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RICE_DRYING_STICKS.get(), RenderType.cutoutMipped(), event);

        setRenderType(FluidRegistryHandler.SOY_MILK.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.SOY_MILK_FLOWING.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.OIL.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.OIL_FLOWING.get(), RenderType.translucent(), event);
    }

    //注册粒子
    @SubscribeEvent
    public static void onParticleFactoryRegister(RegisterParticleProvidersEvent event)
    {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleProvider::new);
        manager.register(ParticleRegistryHandler.SAKURA.get(), SakuraParticle.SakuraParticleProvider::new);
        manager.register(ParticleRegistryHandler.MAPLE_LEAF.get(), MapleLeafParticle.MapleLeafParticleProvider::new);
    }

    //绑定TER
    @SubscribeEvent
    public static void onTERBind(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(TERegistryHandler.MARKABLE_LANTERN_TE.get(), MarkableLanternTER::new);
        //event.registerBlockEntityRenderer(TERegistryHandler.MILL_TE.get(), MillTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.PAIL_TE.get(), PailTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.CANDLE_TE.get(), CandleTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.MULTI_BUILT_BLOCKENTITY.get(), MultiBuiltBlockRenderer::new);
        //event.registerBlockEntityRenderer(TERegistryHandler.MORTAR_TE.get(), MortarTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.CUTTING_BOARD_TE.get(), CuttingBoardTER::new);
    }

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event)
    {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelResourceLocation location = new ModelResourceLocation(ItemRegistryHandler.PAIL.getId(), "inventory");
        BakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null)
        {
            throw new RuntimeException("Did not find Obsidian Hidden in registry");
        } else if (existingModel instanceof PailModel)
        {
            throw new RuntimeException("Tried to replace Obsidian Hidden twice");
        } else
        {
            PailModel model = new PailModel(existingModel);
            modelRegistry.put(location, model);
        }
    }

    //绑定GUI
    @SubscribeEvent
    public static void onScreenBind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            //MenuScreens.register(ContainerRegistryHandler.MILL_CONTAINER.get(), MillScreen::new);
            //MenuScreens.register(ContainerRegistryHandler.MORTAR_CONTAINER.get(), MortarScreen::new);
        });
    }

    // Register client extensions, changed due to the deprecation of initializeClient in NeoForge 1.21.
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event)
    {
        event.registerItem
        (
            new IClientItemExtensions()
            {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer()
                {
                    return new PailISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
            }, ItemRegistryHandler.PAIL.get()
        );

        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity livingEntity,
                                                                   @NotNull ItemStack itemStack,
                                                                   @NotNull EquipmentSlot equipmentSlot,
                                                                   @NotNull HumanoidModel<?> original) {
                // Todo: SujikabotoModel here.
//                return new SujikabutoModel(0.8F);
                return IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
            }
        }, ItemRegistryHandler.SUJIKABUTO.get());

        event.registerFluidType(createClientFluidTypeExtension(FastColor.ARGB32.color(255, 255, 253, 225)), FluidRegistryHandler.AshiharaFluidTypes.TYPE_SOY_MILK.get());
        event.registerFluidType(createClientFluidTypeExtension(FastColor.ARGB32.color(255, 246, 223, 12)), FluidRegistryHandler.AshiharaFluidTypes.TYPE_OIL.get());
    }

    private static IClientFluidTypeExtensions createClientFluidTypeExtension(int color)
    {
        return createClientFluidTypeExtension(FluidRegistryHandler.WATER_STILL, FluidRegistryHandler.WATER_FLOW, FluidRegistryHandler.WATER_OVERLAY, FluidRegistryHandler.UNDERWATER_LOCATION, color);
    }

    private static IClientFluidTypeExtensions createClientFluidTypeExtension(@Nullable ResourceLocation still, @Nullable ResourceLocation flowing, @Nullable ResourceLocation overlay, @Nullable ResourceLocation renderOverlay, int color)
    {
        return new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                return color;
            }

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return still == null ? IClientFluidTypeExtensions.super.getStillTexture() : still;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return flowing == null ? IClientFluidTypeExtensions.super.getFlowingTexture() : flowing;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return overlay == null ? IClientFluidTypeExtensions.super.getOverlayTexture() : overlay;
            }

            @Override
            public @Nullable ResourceLocation getRenderOverlayTexture(@NotNull Minecraft mc) {
                return renderOverlay == null ? IClientFluidTypeExtensions.super.getRenderOverlayTexture(mc) : renderOverlay;
            }
        };
    }
}
