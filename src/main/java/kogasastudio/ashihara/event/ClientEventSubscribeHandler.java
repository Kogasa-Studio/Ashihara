package kogasastudio.ashihara.event;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.gui.MillScreen;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.client.particles.RiceParticle;
import kogasastudio.ashihara.client.particles.SakuraParticle;
import kogasastudio.ashihara.client.render.ter.MarkableLanternTER;
import kogasastudio.ashihara.client.render.ter.MillTER;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
//import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscribeHandler
{
    private static void setRenderType(Block block, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(block, type));
    }

    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event)
    {
        setRenderType(BlockRegistryHandler.BLOCK_RICE_CROP.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_IMMATURE_RICE.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_BLOSSOM.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_JINJA_LANTERN.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_SAKURA.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_CHERRY_SAPLING.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_RED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHRYSANTHEMUM.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_REED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.BLOCK_SHORTER_REED.get(), RenderType.getCutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HYDRANGEA_BUSH.get(), RenderType.getCutoutMipped(), event);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(ParticleFactoryRegisterEvent event)
    {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleFactory::new);
        manager.registerFactory(ParticleRegistryHandler.SAKURA.get(), SakuraParticle.SakuraParticleFactory::new);
    }

    @SubscribeEvent
    public static void onTERbind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.MARKABLE_LANTERN_TE.get(), MarkableLanternTER::new));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(TERegistryHandler.MILL_TE.get(), MillTER::new));
    }

    @SubscribeEvent
    public static void onScreenBind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ScreenManager.registerFactory(ContainerRegistryHandler.MILL_CONTAINER.get(), MillScreen::new));
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
