package kogasastudio.ashihara.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Optional;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;
import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class FluidHelper
{
    public static ItemStack fillContainer(ItemStack itemStack, Fluid fluid, int capacity)
    {
        ItemStack itemStack1 = itemStack.copy();
        CompoundNBT fluidTag = new CompoundNBT();
        new FluidStack(fluid, capacity).writeToNBT(fluidTag);
        itemStack1.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
        return itemStack1;
    }

    public static ItemStack fillContainer(ItemStack itemStack, Fluid fluid) {
        if (fluid != null && !itemStack.isEmpty() && CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null && FluidUtil.getFluidHandler(itemStack) != null) {
            ItemStack itemStack1 = itemStack.copy();
            FluidUtil.getFluidHandler(itemStack1).ifPresent
            (data ->
            {
                CompoundNBT fluidTag = new CompoundNBT();
                new FluidStack(fluid, data.getTankCapacity(0)).writeToNBT(fluidTag);
                itemStack1.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
            });
            return itemStack1;
        }
        return itemStack;
    }

    public static boolean notifyFluidTankInteraction(PlayerEntity player, Hand hand, ItemStack stackIn, FluidTank fluidTank)
    {
        ItemStack stack = stackIn.copy();
        stack.setCount(1);
        Optional<IFluidHandlerItem> fluidHandlerItem = FluidUtil.getFluidHandler(stack).resolve();
        if (fluidHandlerItem.isPresent())
        {
            IFluidHandlerItem handler = fluidHandlerItem.get();
            FluidStack fluidInItem;
            if (fluidTank.isEmpty())
            {
                //If we don't have a fluid stored try draining in general
                fluidInItem = handler.drain(Integer.MAX_VALUE, SIMULATE);
            }
            else
            {
                //Otherwise, try draining the same type of fluid we have stored
                // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
                // second tank but just asking to drain a specific amount
                fluidInItem = handler.drain(new FluidStack(fluidTank.getFluid(), Integer.MAX_VALUE), SIMULATE);
            }
            if (fluidInItem.isEmpty())
            {
                if (!fluidTank.isEmpty())
                {
                    int filled = handler.fill(fluidTank.getFluid(), player.isCreative() ? SIMULATE : EXECUTE);
                    ItemStack container = handler.getContainer();
                    if (filled > 0) {
                        if (stackIn.getCount() == 1)
                        {
                            player.setHeldItem(hand, container);
                        }
                        else if (stackIn.getCount() > 1 && player.addItemStackToInventory(container))
                        {
                            stackIn.shrink(1);
                        }
                        else
                        {
                            player.dropItem(container, false, true);
                            stackIn.shrink(1);
                        }
                        fluidTank.drain(filled, EXECUTE);
                        return true;
                    }
                }
            } else {
                fluidTank.fill(fluidInItem, SIMULATE);
                int remainder = fluidTank.getFluidAmount();
                int storedAmount = fluidInItem.getAmount();
                int capacity = fluidTank.getCapacity();
                if (remainder + storedAmount <= capacity)
                {
                    boolean filled = false;
                    FluidStack drained = handler.drain(new FluidStack(fluidInItem, storedAmount), player.isCreative() ? SIMULATE : EXECUTE);
                    if (!drained.isEmpty())
                    {
                        ItemStack container = handler.getContainer();
                        if (player.isCreative())
                        {
                            filled = true;
                        }
                        else if (!container.isEmpty())
                        {
                            if (stackIn.getCount() == 1)
                            {
                                player.setHeldItem(hand, container);
                                filled = true;
                            }
                            else if (player.addItemStackToInventory(container))
                            {
                                stackIn.shrink(1);
                                filled = true;
                            }
                        }
                        else
                        {
                            stackIn.shrink(1);
                            if (stackIn.isEmpty())
                            {
                                player.setHeldItem(hand, ItemStack.EMPTY);
                            }
                            filled = true;
                        }
                        if (filled)
                        {
                            fluidTank.fill(drained, EXECUTE);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
