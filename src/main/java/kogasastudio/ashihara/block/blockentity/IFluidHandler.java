package kogasastudio.ashihara.block.blockentity;

import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface IFluidHandler
{
    FluidTank createTank();

    default FluidTank createTank(int capacity)
    {
        return this.createTank();
    }

    FluidTank getTank();
}
