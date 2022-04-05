package kogasastudio.ashihara.event;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.gui.MillScreen;
import kogasastudio.ashihara.client.gui.MortarScreen;
import kogasastudio.ashihara.client.models.baked.PailModel;
import kogasastudio.ashihara.client.particles.MapleLeafParticle;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.client.particles.RiceParticle;
import kogasastudio.ashihara.client.particles.SakuraParticle;
import kogasastudio.ashihara.client.render.ter.CandleTER;
import kogasastudio.ashihara.client.render.ter.MarkableLanternTER;
import kogasastudio.ashihara.client.render.ter.MillTER;
import kogasastudio.ashihara.client.render.ter.PailTER;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
//import net.minecraft.world.biome.BiomeColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscribeHandler
{
    private static void setRenderType(Block block, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(block, type));
    }

    private static void setRenderType(Fluid fluid, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(fluid, type));
    }

    //设置渲染方式
    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event)
    {
        setRenderType(BlockRegistryHandler.BLOCK_RICE_CROP.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_IMMATURE_RICE.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_BLOSSOM.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.MAPLE_LEAVES_RED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_JINJA_LANTERN.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.STONE_LANTERN.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_SAKURA.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_MAPLE_LEAVES_RED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_CHERRY_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_RED_MAPLE_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_RED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHRYSANTHEMUM.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_REED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_SHORTER_REED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HYDRANGEA_BUSH.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.TEA_TREE.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.GOLD_FENCE_DECORATION.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_FENCE_EXPANSION.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SOY_BEANS.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SWEET_POTATOES.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CUCUMBERS.get(), RenderType.getCutoutMipped(), event);

        setRenderType(FluidRegistryHandler.SOY_MILK.get(), RenderType.getTranslucent(), event);
        setRenderType(FluidRegistryHandler.SOY_MILK_FLOWING.get(), RenderType.getTranslucent(), event);
        setRenderType(FluidRegistryHandler.OIL.get(), RenderType.getTranslucent(), event);
        setRenderType(FluidRegistryHandler.OIL_FLOWING.get(), RenderType.getTranslucent(), event);
    }

    //注册粒子
    @SubscribeEvent
    public static void onParticleFactoryRegister(ParticleFactoryRegisterEvent event)
    {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleFactory::new);
        manager.registerFactory(ParticleRegistryHandler.SAKURA.get(), SakuraParticle.SakuraParticleFactory::new);
        manager.registerFactory(ParticleRegistryHandler.MAPLE_LEAF.get(), MapleLeafParticle.MapleLeafParticleFactory::new);
    }

    //绑定TER
    @SubscribeEvent
    public static void onTERBind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.MARKABLE_LANTERN_TE.get(), MarkableLanternTER::new));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.MILL_TE.get(), MillTER::new));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.PAIL_TE.get(), PailTER::new));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.CANDLE_TE.get(), CandleTER::new));
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event)
    {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        ModelResourceLocation location = new ModelResourceLocation
        (Objects.requireNonNull(ItemRegistryHandler.PAIL.get().getRegistryName()), "inventory");
        IBakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null)
        {
            throw new RuntimeException("Did not find Obsidian Hidden in registry");
        } else if (existingModel instanceof PailModel)
        {
            throw new RuntimeException("Tried to replaceObsidian Hidden twice");
        } else
        {
            PailModel model = new PailModel(existingModel);
            event.getModelRegistry().put(location, model);
        }
    }

    //绑定GUI
    @SubscribeEvent
    public static void onScreenBind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ScreenManager.registerFactory(ContainerRegistryHandler.MILL_CONTAINER.get(), MillScreen::new));
        event.enqueueWork(() -> ScreenManager.registerFactory(ContainerRegistryHandler.MORTAR_CONTAINER.get(), MortarScreen::new));
    }

//    @SubscribeEvent
//    public static void onColorSetup(ColorHandlerEvent.Block event)
//    {
//        event.getBlockColors().register
//                (
//                    (state, reader, pos, tintIndex) ->
//                    pos != null && reader != null ? BiomeColors.getWaterColor(reader, pos) : -1, BlockExampleContainer.BLOCK_WATER_FIELD
//                );
//    }
}
