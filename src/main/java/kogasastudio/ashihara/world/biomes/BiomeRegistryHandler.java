package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import terrablender.api.Regions;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeRegistryHandler
{
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Ashihara.MODID);

    public static final RegistryObject<Biome> JUNIOR_CHERRY_FOREST =
            BIOMES.register("junior_cherry_forest", AshiharaBiomes::juniorCherryForest);
    public static final RegistryObject<Biome> RED_MAPLE_FOREST =
            BIOMES.register("red_maple_forest", AshiharaBiomes::redMapleForest);

    public static void onRegister(RegistryEvent.Register<Biome> event)
    {
        /// addBiome(event, BiomeManager.BiomeType.WARM, "junior_cherry_forest", 5, JuniorCherryForest());
        /// addBiome(event, BiomeManager.BiomeType.COOL, "red_maple_forest", 12, RedMapleForest());
        /// addBiome(event, BiomeManager.BiomeType.ICY, "snowy_cherry_forest", 15, SnowyCherryForest());
    }

    private static final String OVERWORD = "overworld";

    @SubscribeEvent
    public static void onEvent(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> Regions.register(new AshiharaBiomeRegion(new ResourceLocation(Ashihara.MODID, OVERWORD), 2)));
    }

    private static RegistryObject<Biome> addBiome(RegistryObject<Biome> biome, BiomeManager.BiomeType type, int weight)
    {
        BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(biome.getKey(), weight));
        return biome;
    }
}