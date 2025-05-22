package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class PailBE extends AshiharaMachineBE implements IFluidHandler
{
    FluidTank bucket = this.createTank();

    public PailBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.PAIL_BE.get(), pos, state);
    }

    @Override
    public FluidTank createTank()
    {
        return new FluidTank(16000);
    }

    @Override
    public FluidTank getTank()
    {
        return this.bucket;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        super.loadAdditional(pTag, pRegistries);
        this.bucket = bucket.readFromNBT(pRegistries, pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider)
    {
        super.saveAdditional(compound, provider);
        bucket.writeToNBT(provider, compound);
    }
}
