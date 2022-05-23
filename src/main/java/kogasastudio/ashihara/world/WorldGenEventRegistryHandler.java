package kogasastudio.ashihara.world;

public class WorldGenEventRegistryHandler {
    // todo 需要 DR，重写
    // //Trees
    // public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_CHERRY =
    //         register
    //                 (
    //                         "fancy_cherry",
    //                         Feature.TREE.configured
    //                                 (
    //                                         new BaseTreeFeatureConfig.Builder
    //                                                 (
    //                                                         new SimpleBlockStateProvider(BlockRegistryHandler.CHERRY_LOG.get().defaultBlockState()),
    //                                                         new SimpleBlockStateProvider(BlockRegistryHandler.CHERRY_BLOSSOM.get().defaultBlockState()),
    //                                                         new FancyFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(4), 4),
    //                                                         new FancyTrunkPlacer(3, 11, 0),
    //                                                         new TwoLayerFeature(0, 0, 0, OptionalInt.of(4))
    //                                                 )
    //                                                 .ignoreVines()
    //                                                 .heightmap(Heightmap.Type.MOTION_BLOCKING)
    //                                                 .build()
    //                                 )
    //                 );

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

    // //ConfiguredFeatures
    // public static final ConfiguredFeature<?, ?> FOREST_CHERRY_TREES = register("forest_cherry_trees", FANCY_CHERRY.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
    // public static final ConfiguredFeature<?, ?> PLAIN_CHERRY_TREES = register("plain_cherry_trees", FANCY_CHERRY.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
    // public static final ConfiguredFeature<?, ?> PLAIN_RED_MAPLE_TREES = register("plain_red_maple_trees", RED_MAPLE.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
    // public static final ConfiguredFeature<?, ?> FOREST_RED_MAPLE_TREES = register("forest_red_maple_trees", RED_MAPLE.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));

    // private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature) {
    //     return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Ashihara.MODID, key), configuredFeature);
    // }
}
