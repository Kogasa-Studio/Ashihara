package kogasastudio.ashihara.block.trees;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class RedMapleTree extends Tree
{
    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive)
    {
        return WorldGenEventRegistryHandler.RED_MAPLE;
    }
}
