package kogasastudio.ashihara.world.biomes;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;

public class AshiharaBiomes
{
    public static Biome juniorCherryForest()
    {
        MobSpawnSettings.Builder spawnSetting = getStandardMobSpawnBuilder()
                .addSpawn(MobCategory.CREATURE,
                        new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        //BiomeDefaultFeatures.dripstoneCavesSpawns(spawnSetting);

        var spEffectBuilder = new BiomeSpecialEffects.Builder()
                .waterColor(0x48A7D6)
                .waterFogColor(0x282E84)
                .fogColor(0xC0D8FF)
                .grassColorOverride(0x59BA82)
                .skyColor(getSkyColorWithTemperatureModifier(0.7F))
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);

        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
        globalOverworldGeneration(builder);

        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                WorldGenEventRegistryHandler.FOREST_CHERRY_TREES.getHolder().get());

        BiomeDefaultFeatures.addPlainGrass(builder);
        BiomeDefaultFeatures.addDefaultOres(builder, true);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);
        BiomeDefaultFeatures.addPlainVegetation(builder);
        BiomeDefaultFeatures.addDefaultMushrooms(builder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(builder);

        // Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
        return new Biome.BiomeBuilder()
                .precipitation(Biome.Precipitation.RAIN)
                .biomeCategory(Biome.BiomeCategory.FOREST)
                //.depth(0.1F)
                //.scale(2.0F)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(spEffectBuilder.build())
                .mobSpawnSettings(spawnSetting.build())
                .generationSettings(builder.build())
                .build();
    }

    public static Biome redMapleForest()
    {
        BiomeGenerationSettings.Builder builder = makeDefaultBuilder();

        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_RED_MAPLE_TREES.getHolder().get());
        BiomeDefaultFeatures.addSavannaGrass(builder);
        BiomeDefaultFeatures.addSavannaExtraGrass(builder);
        BiomeDefaultFeatures.addDefaultOres(builder, true);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);
        BiomeDefaultFeatures.addDefaultMushrooms(builder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(builder);

        return
                (
                        new Biome.BiomeBuilder()
                                .precipitation(Biome.Precipitation.RAIN)
                                .biomeCategory(Biome.BiomeCategory.FOREST)
                                .temperature(0.6F)
                                .downfall(0.8F)
                                .specialEffects
                                        (
                                                new BiomeSpecialEffects.Builder()
                                                        .waterColor(0x00BBC6)
                                                        .waterFogColor(0x00AA93)
                                                        .fogColor(0xC7EA7A)
                                                        .grassColorOverride(0xA1E013)
                                                        .skyColor(getSkyColorWithTemperatureModifier(0.9F))
                                                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                                                        .build()
                                        )
                                .mobSpawnSettings
                                        (
                                                getStandardMobSpawnBuilder()
                                                        .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3))
                                                        .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 5, 1, 3))
                                                        .build()
                                        )
                                .generationSettings(builder.build())
                                .build()
                );
    }

    //public static Biome SnowyCherryForest() {
    //    BiomeGenerationSettings.Builder builder = makeDefaultBuilder();
    //    DefaultBiomeFeatures.addDefaultGrass(builder);
    //    DefaultBiomeFeatures.addSurfaceFreezing(builder);

    //    //添加樱花树
    //    builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, WorldGenEventRegistryHandler.FOREST_CHERRY_TREES);

    //    return
    //            (
    //                    new Biome.Builder()
    //                            .precipitation(Biome.RainType.SNOW)
    //                            .biomeCategory(Biome.Category.FOREST)
    //                            .depth(0.1F)
    //                            .scale(0.35F)
    //                            .temperature(0.1F)
    //                            .downfall(0.8F)
    //                            .specialEffects
    //                                    (
    //                                            new BiomeAmbience.Builder()
    //                                                    .waterColor(0x48A7D6)
    //                                                    .waterFogColor(0x282E84)
    //                                                    .fogColor(0xC0D8FF)
    //                                                    .grassColorOverride(0x59BA82)
    //                                                    .skyColor(getSkyColorWithTemperatureModifier(0.1F))
    //                                                    .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
    //                                                    .build()
    //                                    )
    //                            .mobSpawnSettings
    //                                    (
    //                                            getStandardMobSpawnBuilder()
    //                                                    .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3))
    //                                                    .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 2, 2, 5))
    //                                                    .addSpawn(MobCategory.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 3, 1, 4))
    //                                                    .build()
    //                                    )
    //                            .generationSettings(builder.build())
    //                            .build()
    //                            .setRegistryName("ashihara:snowy_cherry_forest")
    //            );
    //}

    //原版
    private static int getSkyColorWithTemperatureModifier(float temperature)
    {
        float lvt_1_1_ = temperature / 3.0F;
        lvt_1_1_ = Mth.clamp(lvt_1_1_, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    private static MobSpawnSettings.Builder getStandardMobSpawnBuilder()
    {
        MobSpawnSettings.Builder mobspawninfo$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(mobspawninfo$builder);
        BiomeDefaultFeatures.commonSpawns(mobspawninfo$builder);
        return mobspawninfo$builder;
    }

    private static BiomeGenerationSettings.Builder makeDefaultBuilder()
    {
        //抄原版用来装饰群系
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
        globalOverworldGeneration(builder);
        return builder;
    }

    /**
     * @see net.minecraft.data.worldgen.biome.OverworldBiomes
     */
    private static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder)
    {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

    private static Biome biome(Biome.Precipitation precipitation, Biome.BiomeCategory category, float temperature, float downfall, MobSpawnSettings.Builder spawnBuilder, BiomeGenerationSettings.Builder biomeBuilder, Music music)
    {
        return biome(precipitation, category, temperature, downfall, 4159204, 329011, spawnBuilder, biomeBuilder, music);
    }

    private static Biome biome(Biome.Precipitation precipitation,
                               Biome.BiomeCategory category,
                               float temperature, float downfall,
                               int waterColor, int waterFogColor,
                               MobSpawnSettings.Builder spawnBuilder,
                               BiomeGenerationSettings.Builder biomeBuilder,
                               Music music)
    {
        return new Biome.BiomeBuilder()
                .precipitation(precipitation)
                .biomeCategory(category)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(waterColor).waterFogColor(waterFogColor)
                        .fogColor(12638463).skyColor(getSkyColorWithTemperatureModifier(temperature))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(music).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build()).build();
    }
}
