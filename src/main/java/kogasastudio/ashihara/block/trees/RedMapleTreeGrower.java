package kogasastudio.ashihara.block.trees;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RedMapleTreeGrower extends AbstractTreeGrower
{

    @org.jetbrains.annotations.Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean b)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Ashihara.MODID, "red_maple_tree"));
//        return WorldGenEventRegistryHandler.RED_MAPLE.getKey();
    }
}
