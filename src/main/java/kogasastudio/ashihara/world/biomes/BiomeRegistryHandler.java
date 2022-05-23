package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeRegistryHandler {
    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Biome> event) {
        // todo 大概需要 DR？
        /// addBiome(event, BiomeManager.BiomeType.WARM, "junior_cherry_forest", 5, JuniorCherryForest());
        /// addBiome(event, BiomeManager.BiomeType.COOL, "red_maple_forest", 12, RedMapleForest());
        /// addBiome(event, BiomeManager.BiomeType.ICY, "snowy_cherry_forest", 15, SnowyCherryForest());
    }

    private static void addBiome(RegistryEvent.Register<Biome> event, BiomeManager.BiomeType type, String id, int weight, Biome biome) {
        BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Ashihara.MODID, id)), weight));
        event.getRegistry().register(biome);
    }
}