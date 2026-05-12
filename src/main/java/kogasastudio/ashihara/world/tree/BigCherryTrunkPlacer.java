package kogasastudio.ashihara.world.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.registry.WorldGenEventRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("NullableProblems")
public class BigCherryTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<BigCherryTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec
    (
        p -> trunkPlacerParts(p).apply(p, BigCherryTrunkPlacer::new)
    );
    private static final int MIN_TRUNK_HEIGHT = 12;
    private static final float ROTATION_VARIANCE = 0.3f; // 旋转变化量

    public BigCherryTrunkPlacer(int baseHeight, int heightRandA, int heightRandB)
    {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type()
    {
        return WorldGenEventRegistryHandler.BIG_CHERRY_TRUNK_PLACER_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, BlockPos origin, TreeConfiguration config) {

        // Place the base trunk
        for (int y = 0; y < baseHeight; ++y) {
            placeLog(level, trunkSetter, random, origin.above(y), config);
        }

        // Place the branches
        int branchHeight = baseHeight + heightRandA;
        placeBranch(level, trunkSetter, random, origin.above(branchHeight), config, 0, heightRandB);

        return List.of(new FoliagePlacer.FoliageAttachment(origin.above(baseHeight + heightRandA), 0, false));
    }

    private void placeBranch(WorldGenLevel level, BiConsumer<BlockPos, BlockState> biConsumer, RandomSource random, BlockPos pos, TreeConfiguration treeConfiguration, int offset, int length) {
        for (int i = offset; i <= length; ++i) {
            placeLog(level, biConsumer, random, pos.offset(0, i, 0), treeConfiguration);
        }

        if (length < 1) {
            return;
        }

        int branchDirection = random.nextInt(3) - 1;
        placeBranch(level, biConsumer, random, pos.offset(branchDirection * length, length, 0), treeConfiguration, 0, length - 1);
        placeBranch(level, biConsumer, random, pos.offset(-branchDirection * length, length, 0), treeConfiguration, 0, length - 1);
    }

    private static class FoliageCoords
    {
        final FoliagePlacer.FoliageAttachment attachment;
        final int branchY;
        public FoliageCoords(BlockPos pos, int y)
        {
            this.attachment = new FoliagePlacer.FoliageAttachment(pos, 0, false);
            this.branchY = y;
        }
    }
}
