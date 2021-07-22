package kogasastudio.ashihara.world;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockExampleContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.FancyFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;

import java.util.OptionalInt;

public class WorldGenEventRegistryHandler
{
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_CHERRY = register("fancy_cherry", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(BlockExampleContainer.BLOCK_CHERRY_LOG.getDefaultState()), new SimpleBlockStateProvider(BlockExampleContainer.BLOCK_CHERRY_BLOSSOM.getDefaultState()), new FancyFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(4), 4), new FancyTrunkPlacer(3, 11, 0), new TwoLayerFeature(0, 0, 0, OptionalInt.of(4)))).setIgnoreVines().setHeightmap(Heightmap.Type.MOTION_BLOCKING).build()));

    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature)
    {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Ashihara.MODID, key), configuredFeature);
    }
}
