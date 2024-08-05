package kogasastudio.ashihara.world.feature;

import com.mojang.serialization.Codec;
import kogasastudio.ashihara.world.configuration.WildCropConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WildCropFeature extends Feature<WildCropConfiguration>
{
    public WildCropFeature(Codec<WildCropConfiguration> pCodec)
    {
        super(pCodec);
    }

    protected boolean tryPlaceInRangeY(FeaturePlaceContext<WildCropConfiguration> context, PlacedFeature feature, BlockPos pos)
    {
        WildCropConfiguration configuration = context.config();
        for (int yOffset = 0; yOffset < configuration.ySpread(); yOffset += 1)
        {
            if (feature.place(context.level(), context.chunkGenerator(), context.random(), pos.offset(0, yOffset > configuration.ySpread() / 2 ? yOffset : configuration.ySpread() / 2 - yOffset, 0))) return true;
        }
        return false;
    }

    @Override
    public boolean place(FeaturePlaceContext<WildCropConfiguration> pContext)
    {
        int placed = 0;
        WildCropConfiguration configuration = pContext.config();
        int xzSpread = configuration.xzSpread();
        RandomSource random = pContext.random();
        BlockPos origin = pContext.origin();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int tries = 0; tries < configuration.tries(); tries += 1)
        {
            pos.setWithOffset(origin, random.nextInt(-xzSpread, xzSpread), 0, random.nextInt(-xzSpread, xzSpread));
            if (tryPlaceInRangeY(pContext, configuration.primaryFeature().value(), pos)) placed += 1;
        }
        for (int tries = 0; tries < configuration.tries(); tries += 1)
        {
            pos.setWithOffset(origin, random.nextInt(-xzSpread, xzSpread), 0, random.nextInt(-xzSpread, xzSpread));
            if (tryPlaceInRangeY(pContext, configuration.secondaryFeature().value(), pos)) placed += 1;
        }
        return placed > 0;
    }
}
