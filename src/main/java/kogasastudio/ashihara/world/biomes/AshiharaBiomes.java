package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public class AshiharaBiomes
{
    private static BiomeGenerationSettings.Builder makeDefaultBuilder()
    {
        //抄原版用来装饰群系
        BiomeGenerationSettings.Builder BiomegenerationSettings
        = (new BiomeGenerationSettings.Builder()
        .withSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
        .withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.CAVE));
        DefaultBiomeFeatures.withStrongholdAndMineshaft(BiomegenerationSettings);
        DefaultBiomeFeatures.withCavesAndCanyons(BiomegenerationSettings);
        DefaultBiomeFeatures.withLavaAndWaterLakes(BiomegenerationSettings);
        DefaultBiomeFeatures.withMonsterRoom(BiomegenerationSettings);
        DefaultBiomeFeatures.withCommonOverworldBlocks(BiomegenerationSettings);
        DefaultBiomeFeatures.withOverworldOres(BiomegenerationSettings);
        DefaultBiomeFeatures.withDisks(BiomegenerationSettings);
//        DefaultBiomeFeatures.withForestGrass(BiomegenerationSettings);
        DefaultBiomeFeatures.withNormalMushroomGeneration(BiomegenerationSettings);
        DefaultBiomeFeatures.withLavaAndWaterSprings(BiomegenerationSettings);
        return BiomegenerationSettings;
    }

    public static Biome JuniorCherryForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
        //添加樱花树
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);
        DefaultBiomeFeatures.withForestGrass(builder);
        DefaultBiomeFeatures.withAllForestFlowerGeneration(builder);

        return
        (
            new Biome.Builder()
            .precipitation(Biome.RainType.RAIN)
            .category(Biome.Category.FOREST)
            .depth(0.1F)
            .scale(2.0F)
            .temperature(0.7F)
            .downfall(0.8F)
            .setEffects
            (
                new BiomeAmbience.Builder()
                .setWaterColor(0x48A7D6)
                .setWaterFogColor(0x282E84)
                .setFogColor(0xC0D8FF)
                .withGrassColor(0x59BA82)
                .withSkyColor(getSkyColorWithTemperatureModifier(0.7F))
                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                .build()
            )
            .withMobSpawnSettings
            (
                getStandardMobSpawnBuilder()
                .withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3))
                .build()
            )
            .withGenerationSettings(builder.build())
            .build()
            .setRegistryName("ashihara:junior_cherry_forest")
        );
    }

    public static Biome SnowyCherryForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
        DefaultBiomeFeatures.withBadlandsGrass(builder);
        DefaultBiomeFeatures.withFrozenTopLayer(builder);

        //添加樱花树
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);

        return
        (
            new Biome.Builder()
            .precipitation(Biome.RainType.SNOW)
            .category(Biome.Category.FOREST)
            .depth(0.1F)
            .scale(0.35F)
            .temperature(0.1F)
            .downfall(0.8F)
            .setEffects
            (
                new BiomeAmbience.Builder()
                .setWaterColor(0x48A7D6)
                .setWaterFogColor(0x282E84)
                .setFogColor(0xC0D8FF)
                .withGrassColor(0x59BA82)
                .withSkyColor(getSkyColorWithTemperatureModifier(0.1F))
                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                .build()
            )
            .withMobSpawnSettings
            (
                getStandardMobSpawnBuilder()
                .withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3))
                .withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 2, 2, 5))
                .withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 3, 1, 4))
                .build()
            )
            .withGenerationSettings(builder.build())
            .build()
            .setRegistryName("ashihara:snowy_cherry_forest")
        );
    }

    //原版
    private static int getSkyColorWithTemperatureModifier(float temperature)
    {
        float lvt_1_1_ = temperature / 3.0F;
        lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
        return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    private static MobSpawnInfo.Builder getStandardMobSpawnBuilder()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        return mobspawninfo$builder;
    }
}
