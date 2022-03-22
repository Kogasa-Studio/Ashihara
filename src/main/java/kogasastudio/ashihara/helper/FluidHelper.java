package kogasastudio.ashihara.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

import static net.minecraft.util.SoundCategory.BLOCKS;
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

    public static boolean notifyFluidTankInteraction(PlayerEntity player, Hand hand, ItemStack stackIn, FluidTank fluidTank, World world, BlockPos pos)
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
                    SoundEvent event = fluidTank.getFluid().getFluid().getAttributes().getFillSound();
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
                        if (world != null)
                        {
                            world.playSound(player, pos, event, BLOCKS, 1.0f, 1.0f);
                        }
                        return true;
                    }
                }
            }
            else
            {
                fluidTank.fill(fluidInItem, SIMULATE);
                int remainder = fluidTank.getFluidAmount();
                int storedAmount = fluidInItem.getAmount();
                int capacity = fluidTank.getCapacity();

                int filledAmount = Math.min(capacity - remainder, storedAmount);
                boolean filled = false;
                FluidStack drained = handler.drain(new FluidStack(fluidInItem, filledAmount), player.isCreative() ? SIMULATE : EXECUTE);
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
                        if (world != null)
                        {
                            SoundEvent event = drained.getFluid().getAttributes().getEmptySound();
                            world.playSound(null, pos, event, BLOCKS, 1.0f, 1.0f);
                        }
                        fluidTank.fill(drained, EXECUTE);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean notifyFluidTankInteraction(ItemStackHandler itemHandler, int in, int out, ItemStack stackIn, FluidTank fluidTank, World world, BlockPos pos)
    {
        ItemStack stack = stackIn.copy();
        ItemStack outStack = itemHandler.getStackInSlot(out);
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
            if (fluidInItem.isEmpty()) //抽取流体
            {
                if (!outStack.isEmpty()) return false;
                if (!fluidTank.isEmpty())
                {
                    SoundEvent event = fluidTank.getFluid().getFluid().getAttributes().getFillSound();
                    int filled = handler.fill(fluidTank.getFluid(), EXECUTE);
                    ItemStack container = handler.getContainer();
                    if (filled > 0)
                    {
                        stackIn.shrink(1);
                        itemHandler.setStackInSlot(out, container);
//                        if (stackIn.getCount() == 1)
//                        {
//                        }
//                        else
//                        {
//                            if (world != null)
//                            {world.addEntity(new ItemEntity(world, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, container));}
//                            stackIn.shrink(1);
//                        }
                        if (world != null)
                        {
                            world.playSound(null, pos, event, BLOCKS, 1.0f, 1.0f);
                        }
                        fluidTank.drain(filled, EXECUTE);
                        return true;
                    }
                }
            }
            else //添加流体
            {
                fluidTank.fill(fluidInItem, SIMULATE);
                int remainder = fluidTank.getFluidAmount();
                int storedAmount = fluidInItem.getAmount();
                int capacity = fluidTank.getCapacity();

                int filledAmount = Math.min(capacity - remainder, storedAmount);
//                boolean filled = false;
                boolean outEmpty = outStack.isEmpty();
                boolean canOutStack =
                    storedAmount == filledAmount && handler.getContainer().isItemEqual(outStack) && outStack.getCount() + 1 <= outStack.getMaxStackSize();
                if (!(outEmpty || canOutStack)) return false;
                FluidStack drained = handler.drain(new FluidStack(fluidInItem, filledAmount), EXECUTE);
                if (!drained.isEmpty())
                {
                    ItemStack container = handler.getContainer();
                    stackIn.shrink(1);
                    if (outEmpty)
                    {
                        itemHandler.setStackInSlot(out, container);
                    }
                    else
                    {
                        itemHandler.setStackInSlot(out, new ItemStack(container.getItem(), outStack.getCount() + 1));
                    }
//                    filled = true;
//                    if (!container.isEmpty())
//                    {
//                        if (stackIn.getCount() == 1)
//                        {
//                        }
//                        else if (world != null)
//                        {
//                            {world.addEntity(new ItemEntity(world, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, container));}
//                            stackIn.shrink(1);
//                            filled = true;
//                        }
//                    }
//                    else
//                    {
//                        stackIn.shrink(1);
//                        filled = true;
//                    }
                    if (world != null)
                    {
                        SoundEvent event = drained.getFluid().getAttributes().getEmptySound();
                        world.playSound(null, pos, event, BLOCKS, 1.0f, 1.0f);
                    }
                    fluidTank.fill(drained, EXECUTE);
                    return true;
//                    if (filled)
//                    {
//                    }
                }
            }
        }
        return false;
    }
}
