package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class FluidHelper
{
    /**
     * Returns {@code true} if {@code fluidIn} can be added to {@code tank}
     * (same fluid type or tank is empty, and there is enough remaining capacity).
     */
    public static boolean canFluidAddToTank(FluidStack fluidIn, BEFluidStackHandler<?> tank)
    {
        FluidStack stored = tank.getFluidStack();
        return (tank.isEmpty() || fluidIn.is(stored.getFluid()))
                && fluidIn.getAmount() + tank.getFluidAmount() <= tank.getCapacity();
    }

    /**
     * Returns {@code true} if {@code fluidIn} can be extracted from {@code tank}
     * (matching fluid type and sufficient amount stored).
     */
    public static boolean canFluidExtractFromTank(FluidStack fluidIn, BEFluidStackHandler<?> tank)
    {
        FluidStack stored = tank.getFluidStack();
        return !stored.isEmpty() && fluidIn.is(stored.getFluid()) && tank.getFluidAmount() >= fluidIn.getAmount();
    }

    /**
     * Handles a player right-clicking a block with a fluid container item on a fluid tank.
     * Delegates to {@link FluidUtil#interactWithFluidHandler} which handles sound, item swapping,
     * and game events automatically.
     *
     * @return {@code true} if fluid was moved
     */
    public static boolean notifyFluidTankInteraction(Player player, InteractionHand hand,
                                                     BEFluidStackHandler<?> fluidTank, BlockPos pos)
    {
        return FluidUtil.interactWithFluidHandler(player, hand, pos, fluidTank);
    }
}
