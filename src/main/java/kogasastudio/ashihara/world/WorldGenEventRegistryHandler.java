package kogasastudio.ashihara.world;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.OptionalInt;

public class WorldGenEventRegistryHandler {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE =
            DeferredRegister.create(BuiltinRegistries.CONFIGURED_FEATURE.key(), Ashihara.MODID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURE =
            DeferredRegister.create(BuiltinRegistries.PLACED_FEATURE.key(), Ashihara.MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> FANCY_CHERRY =
            CONFIGURED_FEATURE.register("fancy_cherry", () -> new ConfiguredFeature<>(
                    Feature.TREE,
                    new TreeConfiguration.TreeConfigurationBuilder(
                            BlockStateProvider.simple(BlockRegistryHandler.CHERRY_LOG.get().defaultBlockState()),
                            new FancyTrunkPlacer(3, 11, 0),
                            BlockStateProvider.simple(BlockRegistryHandler.CHERRY_BLOSSOM.get().defaultBlockState()),
                            new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                            new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                    )
                            .ignoreVines()
                            // .heightmap(Heightmap.Type.MOTION_BLOCKING)
                            .build()));

    public static final RegistryObject<PlacedFeature> FOREST_CHERRY_TREES =
            PLACED_FEATURE.register("forest_cherry_trees",
                    () -> new PlacedFeature(FANCY_CHERRY.getHolder().get(),
                            Arrays.asList(PlacementUtils.HEIGHTMAP,
                                    PlacementUtils.countExtra(3, 0.1F, 0))));


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
