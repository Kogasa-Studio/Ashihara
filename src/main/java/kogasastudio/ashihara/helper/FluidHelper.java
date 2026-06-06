package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
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

    /**
     * 让创造模式也会消耗流体的交互方式
     */
    public static boolean interactFluidStrict(Player player, InteractionHand hand, BlockPos pos, ResourceHandler<FluidResource> handler)
    {
        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : 40; // 40 = offhand
        var handAccess = ItemAccess.forPlayerSlot(player, slot).oneByOne();
        var handFluid = handAccess.getCapability(Capabilities.Fluid.ITEM);
        if (handFluid == null) return false;

        var r = ResourceHandlerUtil.moveFirst(handler, handFluid, fr -> true, Integer.MAX_VALUE, null);
        if (r != null) { FluidUtil.triggerSoundAndGameEvent(r.resource(), player.level(), pos.getCenter(), player, true); return true; }
        r = ResourceHandlerUtil.moveFirst(handFluid, handler, fr -> true, Integer.MAX_VALUE, null);
        if (r != null) { FluidUtil.triggerSoundAndGameEvent(r.resource(), player.level(), pos.getCenter(), player, false); return true; }
        return false;
    }
}
