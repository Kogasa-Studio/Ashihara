package kogasastudio.ashihara.block.tileentities;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface IFluidHandler
{
    FluidTank createTank();

    default FluidTank createTank(int capacity)
    {
        return this.createTank();
    }

    LazyOptional<FluidTank> getTank();
}
