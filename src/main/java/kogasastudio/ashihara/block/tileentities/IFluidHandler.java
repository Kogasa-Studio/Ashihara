package kogasastudio.ashihara.block.tileentities;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface IFluidHandler
{
    FluidTank createTank();

    LazyOptional<FluidTank> getTank();
}
