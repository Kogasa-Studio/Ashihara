package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class WorldGenEventRegistryHandler
{
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = DeferredRegister.create(Registries.CONFIGURED_FEATURE.registry(), Ashihara.MODID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURE = DeferredRegister.create(Registries.PLACED_FEATURE.registry(), Ashihara.MODID);

    private static final PlacementModifier TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);

    public static final Supplier<ConfiguredFeature<?, ?>> FANCY_CHERRY =
            CONFIGURED_FEATURE.register
                    ("fancy_cherry", () -> new ConfiguredFeature<>
                            (
                                    Feature.TREE,
                                    new TreeConfiguration.TreeConfigurationBuilder
                                            (
                                                    BlockStateProvider.simple(BlockRegistryHandler.CHERRY_LOG.get().defaultBlockState()),
                                                    new FancyTrunkPlacer(3, 11, 0),
                                                    BlockStateProvider.simple(BlockRegistryHandler.CHERRY_BLOSSOM.get()),
                                                    new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                                                    new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                                            )
                                            .ignoreVines()
                                            .build()
                            )
                    );

    public static final Supplier<ConfiguredFeature<?, ?>> RED_MAPLE =
            CONFIGURED_FEATURE.register
                    (
                            "red_maple", () -> new ConfiguredFeature<>
                                    (
                                            Feature.TREE,
                                            new TreeConfiguration.TreeConfigurationBuilder
                                                    (
                                                            BlockStateProvider.simple(BlockRegistryHandler.MAPLE_LOG.get().defaultBlockState()),
                                                            new StraightTrunkPlacer(5, 2, 0),
                                                            BlockStateProvider.simple(BlockRegistryHandler.MAPLE_LEAVES_RED.get().defaultBlockState()),
                                                            new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                                                            new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                                                    )
                                                    .ignoreVines()
                                                    .build()
                                    )
                    );


    private static List<PlacementModifier> DefaultTreeModifiers(int baseAmount, float extraProbability, int extraAmount)
    {
        return
                List.of
                        (
                                PlacementUtils.countExtra(baseAmount, extraProbability, extraAmount),
                                InSquarePlacement.spread(),
                                TREE_THRESHOLD,
                                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                                PlacementUtils.filteredByBlockSurvival(BlockRegistryHandler.CHERRY_SAPLING.get()),
                                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(BlockRegistryHandler.CHERRY_SAPLING.get().defaultBlockState(), BlockPos.ZERO)),
                                BiomeFilter.biome()
                        );
    }

    public static final Supplier<PlacedFeature> FOREST_CHERRY_TREES =
            PLACED_FEATURE.register
                    (
                            "forest_cherry_trees",
                            () -> new PlacedFeature(Holder.direct(FANCY_CHERRY.get()), DefaultTreeModifiers(8, 0.1F, Ashihara.getRandomBounded(1, 3)))
                    );

    public static final Supplier<PlacedFeature> FOREST_RED_MAPLE_TREES =
            PLACED_FEATURE.register
                    (
                            "forest_red_maple_trees",
                            () -> new PlacedFeature(Holder.direct(RED_MAPLE.get()), DefaultTreeModifiers(10, 0.1F, 1))
                    );

    public static final Supplier<PlacedFeature> PLAIN_CHERRY_TREES =
            PLACED_FEATURE.register
                    (
                            "plain_cherry_trees",
                            () -> new PlacedFeature(Holder.direct(FANCY_CHERRY.get()), DefaultTreeModifiers(0, 0.05F, 1))
                    );

    public static final Supplier<PlacedFeature> PLAIN_RED_MAPLE_TREES =
            PLACED_FEATURE.register
                    (
                            "plain_red_maple_trees",
                            () -> new PlacedFeature(Holder.direct(RED_MAPLE.get()), DefaultTreeModifiers(0, 0.05F, 1))
                    );


    //Trees

    // public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> RED_MAPLE =
    //         register
    //                 (
    //                         "red_maple",
    //                         Feature.TREE.configured
    //                                 (
    //                                         new BaseTreeFeatureConfig.Builder
    //                                                 (
    //                                                         new SimpleBlockStateProvider(BlockRegistryHandler.MAPLE_LOG.get().defaultBlockState()),
    //                                                         new SimpleBlockStateProvider(BlockRegistryHandler.MAPLE_LEAVES_RED.get().defaultBlockState()),
    //                                                         new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3),
    //                                                         new StraightTrunkPlacer(5, 2, 0),
    //                                                         new TwoLayerFeature(0, 0, 0, OptionalInt.of(4))
    //                                                 )
    //                                                 .ignoreVines()
    //                                                 .heightmap(Heightmap.Type.MOTION_BLOCKING)
    //                                                 .build()
    //                                 )
    //                 );

    //ConfiguredFeatures
    // public static final ConfiguredFeature<?, ?> FOREST_CHERRY_TREES = register("forest_cherry_trees", FANCY_CHERRY.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
    // public static final ConfiguredFeature<?, ?> PLAIN_CHERRY_TREES = register("plain_cherry_trees", FANCY_CHERRY.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
    // public static final ConfiguredFeature<?, ?> PLAIN_RED_MAPLE_TREES = register("plain_red_maple_trees", RED_MAPLE.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
    // public static final ConfiguredFeature<?, ?> FOREST_RED_MAPLE_TREES = register("forest_red_maple_trees", RED_MAPLE.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
}
