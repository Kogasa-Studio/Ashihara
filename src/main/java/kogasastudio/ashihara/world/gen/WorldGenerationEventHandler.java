/*
package kogasastudio.ashihara.world.gen;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.level.BiomeLoadingEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldGenerationEventHandler
{
    @SubscribeEvent
    public static void onTreesGenerate(LevelEvent event)
    {
        Biome.BiomeCategory category = event.getCategory();
        if (category.equals(Biome.BiomeCategory.PLAINS) || category.equals(Biome.BiomeCategory.EXTREME_HILLS))
        {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_CHERRY_TREES.getHolder().get());
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_RED_MAPLE_TREES.getHolder().get());
        }
        // todo 需要重写
        // if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS)) {
        //     settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_CHERRY_TREES);
        //     settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_RED_MAPLE_TREES);
        // }
        // if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS)) {
        //     settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_CHERRY_TREES);
        //     settings.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.PLAIN_RED_MAPLE_TREES);
        // }
    }
}
*/
