package kogasastudio.ashihara.world.tree;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.registry.WorldGenEventRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import java.util.function.BiConsumer;

public class BigCherryFoliagePlacer extends BlobFoliagePlacer
{
    public static final MapCodec<BigCherryFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec
    (
        p -> blobParts(p).apply(p, BigCherryFoliagePlacer::new)
    );

    public BigCherryFoliagePlacer(IntProvider radius, IntProvider offset, int leavesPerRadius) {
        super(radius, offset, leavesPerRadius);
    }

    @Override
    protected FoliagePlacerType<?> type()
    {
        return WorldGenEventRegistryHandler.BIG_CHERRY_TRUNK_PLACER_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader levelSimulatedReader, FoliageSetter biConsumer, RandomSource random, TreeConfiguration treeConfiguration, int maxFreeTreeHeight, FoliagePlacer.FoliageAttachment foliageAttachment, int foliageHeight, int radius, int offset) {

        BlockPos blockPos = foliageAttachment.pos();
        placeLeaves(levelSimulatedReader, biConsumer, random, treeConfiguration, blockPos, radius, offset);
    }

    private void placeLeaves(LevelSimulatedReader levelSimulatedReader, FoliageSetter biConsumer, RandomSource random, TreeConfiguration treeConfiguration, BlockPos pos, int radius, int offset) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    mutableBlockPos.setWithOffset(pos, x, y + offset, z);
                    if (shouldPlaceLeaf(levelSimulatedReader, random, mutableBlockPos, radius, offset)) {
                        tryPlaceLeaf(levelSimulatedReader, biConsumer, random, treeConfiguration, mutableBlockPos);
                    }
                }
            }
        }
    }

    private boolean shouldPlaceLeaf(LevelSimulatedReader levelSimulatedReader, RandomSource random, BlockPos pos, int radius, int offset) {
        if (pos.distSqr(new Vec3i(pos.getX(), pos.getY() - offset, pos.getZ())) >= radius * radius) {
            return false;
        }

        return random.nextInt(Math.max(1, (int) (8.0 / Math.cbrt(radius * radius)))) == 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int x, int y, int z, int range, boolean large)
    {
        // 更宽松的圆形检查（允许边缘更饱满）
        double distanceSq = x * x + z * z;
        double maxDistanceSq = (range + (large ? 0.5 : 0)) * (range + (large ? 0.5 : 0));
        return distanceSq > maxDistanceSq + random.nextDouble() * 2;  // 减少随机跳过
    }
}