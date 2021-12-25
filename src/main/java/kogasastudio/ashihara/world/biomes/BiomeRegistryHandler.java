package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static kogasastudio.ashihara.world.biomes.AshiharaBiomes.JuniorCherryForest;

@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeRegistryHandler
{
    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Biome> event)
    {
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation("ashihara:junior_cherry_forest")), 5));

        event.getRegistry().register(JuniorCherryForest());
    }
}
