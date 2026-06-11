package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FermentationBlockEntity extends AshiharaCommonBE
{
    public enum Size
    {
        BASIN(1), VAT(1), LARGE_VAT(2);

        public final int blockSize;

        Size(int blockSize) { this.blockSize = blockSize; }
    }

    public final Size size;

    public FermentationBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.FERMENTATION_BE.get(), pos, state);
        this.size = state.getBlock() instanceof IFermentationSizeProvider fp
            ? fp.getSize() : Size.BASIN;
    }

    public interface IFermentationSizeProvider
    {
        Size getSize();
    }
}