package kogasastudio.ashihara.world.gen;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;

@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldGenerationEventHandler
{
    @SubscribeEvent
    public static void onTreesGenerate(BiomeLoadingEvent event)
    {
        BiomeGenerationSettingsBuilder settings = event.getGeneration();
        ResourceKey<Biome> biome = ResourceKey.create(ForgeRegistries.Keys.BIOMES, event.getName());
//        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST))
//        {
//            settings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);
//        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS))
        {
            settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_CHERRY_TREES);
            settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_RED_MAPLE_TREES);
        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS))
        {
            settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_CHERRY_TREES);
            settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_RED_MAPLE_TREES);
        }
    }
}
