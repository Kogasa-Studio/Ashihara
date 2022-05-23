package kogasastudio.ashihara.block.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class PailTE extends AshiharaMachineTE implements IFluidHandler
{
    public PailTE()
    {
        super(TERegistryHandler.PAIL_TE.get());
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
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        bucket.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("bucket")));
        if (this.level != null)
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);
        bucket.ifPresent(fluidTank -> compound.put("bucket", fluidTank.writeToNBT(new CompoundNBT())));
        return compound;
    }

    @Override
    protected void invalidateCaps()
    {
        super.invalidateCaps();
        this.bucket.invalidate();
    }

    @Override
    protected void reviveCaps()
    {
        super.reviveCaps();
        bucket = LazyOptional.of(this::createTank);
    }
}
