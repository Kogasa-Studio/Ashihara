package kogasastudio.ashihara.block.trees;

import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class RedMapleTree extends AbstractTreeGrower {
    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random randomIn, boolean largeHive)
    {
        return WorldGenEventRegistryHandler.RED_MAPLE.getHolder().get();
    }
}
