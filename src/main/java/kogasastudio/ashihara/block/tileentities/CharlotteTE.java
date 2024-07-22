package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CharlotteTE extends BlockEntity
{
    private final NonNullList<double[]> posList = NonNullList.create();
    public CharlotteTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CHARLOTTE_TE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compoundTag)
    {
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag)
    {
        super.saveAdditional(compoundTag);
    }
}
