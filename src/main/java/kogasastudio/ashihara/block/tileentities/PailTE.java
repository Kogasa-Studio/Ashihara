package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class PailTE extends AshiharaMachineTE implements IFluidHandler
{
    public PailTE(BlockPos pos, BlockState state) {
        super(TERegistryHandler.PAIL_TE.get(), pos, state);
    }

    LazyOptional<FluidTank> bucket = LazyOptional.of(this::createTank);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap)
    {
        if (!this.isRemoved() && cap.equals(FLUID_HANDLER_CAPABILITY)) {return this.bucket.cast();}
        return super.getCapability(cap);
    }

    @Override
    public FluidTank createTank() {return new FluidTank(4000);}

    @Override
    public LazyOptional<FluidTank> getTank() {return this.bucket;}

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        bucket.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("bucket")));
        if (this.level != null)
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        bucket.ifPresent(fluidTank -> compound.put("bucket", fluidTank.writeToNBT(new CompoundTag())));
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        this.bucket.invalidate();
    }

    @Override
    public void reviveCaps()
    {
        super.reviveCaps();
        bucket = LazyOptional.of(this::createTank);
    }
}
