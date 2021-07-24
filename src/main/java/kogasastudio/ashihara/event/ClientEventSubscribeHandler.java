package kogasastudio.ashihara.event;

import kogasastudio.ashihara.block.BlockExampleContainer;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.client.particles.RiceParticle;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        setRenderType(BlockExampleContainer.BLOCK_WATER_FIELD, RenderType.getTranslucent(), event);
        setRenderType(BlockExampleContainer.BLOCK_RICE_CROP, RenderType.getCutoutMipped(), event);
        setRenderType(BlockExampleContainer.BLOCK_CHERRY_BLOSSOM, RenderType.getCutoutMipped(), event);
        setRenderType(BlockExampleContainer.BLOCK_CHERRY_SAPLING, RenderType.getCutoutMipped(), event);
        setRenderType(BlockExampleContainer.BLOCK_JINJA_LANTERN, RenderType.getCutoutMipped(), event);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(ParticleFactoryRegisterEvent event)
    {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleFactory::new);
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
