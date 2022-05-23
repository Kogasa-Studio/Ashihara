package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
        .surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
        .addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.CAVE));
        DefaultBiomeFeatures.addDefaultOverworldLandStructures(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultCarvers(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultLakes(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultMonsterRoom(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultUndergroundVariety(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultOres(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultSoftDisks(BiomegenerationSettings);
//        DefaultBiomeFeatures.withForestGrass(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultMushrooms(BiomegenerationSettings);
        DefaultBiomeFeatures.addDefaultSprings(BiomegenerationSettings);
        return BiomegenerationSettings;
    }

    public static Biome JuniorCherryForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
        //添加樱花树
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);
        DefaultBiomeFeatures.addForestGrass(builder);
        DefaultBiomeFeatures.addForestFlowers(builder);

        return
        (
            new Biome.Builder()
            .precipitation(Biome.RainType.RAIN)
            .biomeCategory(Biome.Category.FOREST)
            .depth(0.1F)
            .scale(2.0F)
            .temperature(0.7F)
            .downfall(0.8F)
            .specialEffects
            (
                new BiomeAmbience.Builder()
                .waterColor(0x48A7D6)
                .waterFogColor(0x282E84)
                .fogColor(0xC0D8FF)
                .grassColorOverride(0x59BA82)
                .skyColor(getSkyColorWithTemperatureModifier(0.7F))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                .build()
            )
            .mobSpawnSettings
            (
                getStandardMobSpawnBuilder()
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3))
                .build()
            )
            .generationSettings(builder.build())
            .build()
            .setRegistryName("ashihara:junior_cherry_forest")
        );
    }

    public static Biome RedMapleForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_RED_MAPLE_TREES);
        DefaultBiomeFeatures.addForestGrass(builder);
        DefaultBiomeFeatures.addForestFlowers(builder);

        return
        (
            new Biome.Builder()
            .precipitation(Biome.RainType.RAIN)
            .biomeCategory(Biome.Category.FOREST)
            .depth(0.1F)
            .scale(0.2F)
            .temperature(0.6F)
            .downfall(0.8F)
            .specialEffects
            (
                new BiomeAmbience.Builder()
                .waterColor(0x00BBC6)
                .waterFogColor(0x00AA93)
                .fogColor(0xC7EA7A)
                .grassColorOverride(0xA1E013)
                .skyColor(getSkyColorWithTemperatureModifier(0.9F))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                .build()
            )
            .mobSpawnSettings
            (
                getStandardMobSpawnBuilder()
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 10, 2, 3))
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 5, 1, 3))
                .build()
            )
            .generationSettings(builder.build())
            .build()
            .setRegistryName("ashihara:red_maple_forest")
        );
    }

    public static Biome SnowyCherryForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
        DefaultBiomeFeatures.addDefaultGrass(builder);
        DefaultBiomeFeatures.addSurfaceFreezing(builder);

        //添加樱花树
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);

        return
        (
            new Biome.Builder()
            .precipitation(Biome.RainType.SNOW)
            .biomeCategory(Biome.Category.FOREST)
            .depth(0.1F)
            .scale(0.35F)
            .temperature(0.1F)
            .downfall(0.8F)
            .specialEffects
            (
                new BiomeAmbience.Builder()
                .waterColor(0x48A7D6)
                .waterFogColor(0x282E84)
                .fogColor(0xC0D8FF)
                .grassColorOverride(0x59BA82)
                .skyColor(getSkyColorWithTemperatureModifier(0.1F))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                .build()
            )
            .mobSpawnSettings
            (
                getStandardMobSpawnBuilder()
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3))
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 2, 2, 5))
                .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 3, 1, 4))
                .build()
            )
            .generationSettings(builder.build())
            .build()
            .setRegistryName("ashihara:snowy_cherry_forest")
        );
    }

    //原版
    private static int getSkyColorWithTemperatureModifier(float temperature)
    {
        float lvt_1_1_ = temperature / 3.0F;
        lvt_1_1_ = Mth.clamp(lvt_1_1_, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    private static MobSpawnInfo.Builder getStandardMobSpawnBuilder()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.farmAnimals(mobspawninfo$builder);
        DefaultBiomeFeatures.commonSpawns(mobspawninfo$builder);
        return mobspawninfo$builder;
    }
}
