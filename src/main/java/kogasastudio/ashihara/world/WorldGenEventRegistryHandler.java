package kogasastudio.ashihara.world;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.FancyFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;

import java.util.OptionalInt;

public class WorldGenEventRegistryHandler
{
    //Trees
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_CHERRY =
    register
    (
        "fancy_cherry",
        Feature.TREE.withConfiguration
        (
            new BaseTreeFeatureConfig.Builder
            (
                new SimpleBlockStateProvider(BlockRegistryHandler.CHERRY_LOG.get().getDefaultState()),
                new SimpleBlockStateProvider(BlockRegistryHandler.CHERRY_BLOSSOM.get().getDefaultState()),
                new FancyFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(4), 4),
                new FancyTrunkPlacer(3, 11, 0),
                new TwoLayerFeature(0, 0, 0, OptionalInt.of(4))
            )
            .setIgnoreVines()
            .setHeightmap(Heightmap.Type.MOTION_BLOCKING)
            .build()
        )
    );

    //ConfiguredFeatures
    public static final ConfiguredFeature<?, ?> FOREST_CHERRY_TREES = register("forest_cherry_trees", FANCY_CHERRY.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
    public static final ConfiguredFeature<?, ?> PLAIN_CHERRY_TREES = register("plain_cherry_trees", FANCY_CHERRY.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));

    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature)
    {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Ashihara.MODID, key), configuredFeature);
    }
}
