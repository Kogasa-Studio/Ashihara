package kogasastudio.ashihara.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

import static net.minecraft.sounds.SoundSource.BLOCKS;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;
import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class FluidHelper
{
    public static ItemStack fillContainer(ItemStack itemStack, Fluid fluid, int capacity)
    {
        ItemStack itemStack1 = itemStack.copy();
        CompoundTag fluidTag = new CompoundTag();
        new FluidStack(fluid, capacity).writeToNBT(fluidTag);
        itemStack1.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
        return itemStack1;
    }

    public static ItemStack fillContainer(ItemStack itemStack, Fluid fluid)
    {
        if (fluid != null && !itemStack.isEmpty() && ForgeCapabilities.FLUID_HANDLER_ITEM != null && FluidUtil.getFluidHandler(itemStack) != null)
        {
            ItemStack itemStack1 = itemStack.copy();
            FluidUtil.getFluidHandler(itemStack1).ifPresent
                    (data ->
                    {
                        CompoundTag fluidTag = new CompoundTag();
                        new FluidStack(fluid, data.getTankCapacity(0)).writeToNBT(fluidTag);
                        itemStack1.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
                    });
            return itemStack1;
        }
        return itemStack;
    }

    /**
     * 判断特定流体是否能被添加进FluidTank
     *
     * @param fluidIn 待加入的流体
     * @param tank    待接收的tank
     * @return 接收可行性
     */
    public static boolean canFluidAddToTank(FluidStack fluidIn, FluidTank tank)
    {
        return (tank.isEmpty() || fluidIn.isFluidEqual(tank.getFluid())) && fluidIn.getAmount() + tank.getFluid().getAmount() <= tank.getCapacity();
    }

    public static boolean canFluidAddToTank(FluidStack fluidIn, LazyOptional<FluidTank> tank)
    {
        return canFluidAddToTank(fluidIn, tank.orElse(new FluidTank(0)));
    }

    /**
     * 与上方方法类似，判断是否能从fluidTank中抽取特定流体
     */
    public static boolean canFluidExtractFromTank(FluidStack fluidIn, FluidTank tank)
    {
        return fluidIn.isFluidEqual(tank.getFluid()) && tank.getFluid().getAmount() >= fluidIn.getAmount();
    }

    public static boolean canFluidExtractFromTank(FluidStack fluidIn, LazyOptional<FluidTank> tank)
    {
        return canFluidExtractFromTank(fluidIn, tank.orElse(new FluidTank(0)));
    }

    public static boolean notifyFluidTankInteraction(Player player, InteractionHand hand, ItemStack stackIn, FluidTank fluidTank, Level world, BlockPos pos)
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
            } else
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
                    SoundEvent event = fluidTank.getFluid().getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL);
                    int filled = handler.fill(fluidTank.getFluid(), player.isCreative() ? SIMULATE : EXECUTE);
                    ItemStack container = handler.getContainer();
                    if (filled > 0)
                    {
                        if (stackIn.getCount() == 1)
                        {
                            player.setItemInHand(hand, container);
                        } else if (stackIn.getCount() > 1 && player.addItem(container))
                        {
                            stackIn.shrink(1);
                        } else
                        {
                            player.drop(container, false, true);
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
            } else
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
                    } else if (!container.isEmpty())
                    {
                        if (stackIn.getCount() == 1)
                        {
                            player.setItemInHand(hand, container);
                            filled = true;
                        } else if (player.addItem(container))
                        {
                            stackIn.shrink(1);
                            filled = true;
                        }
                    } else
                    {
                        stackIn.shrink(1);
                        if (stackIn.isEmpty())
                        {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                        filled = true;
                    }
                    if (filled)
                    {
                        if (world != null)
                        {
                            SoundEvent event = drained.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
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

    public static boolean notifyFluidTankInteraction(ItemStackHandler itemHandler, int in, int out, FluidTank fluidTank, Level world, BlockPos pos)
    {
        ItemStack stackIn = itemHandler.getStackInSlot(in);
        ItemStack stack = stackIn.copy();
        ItemStack simStack = stackIn.copy();
        ItemStack outStack = itemHandler.getStackInSlot(out);
        stack.setCount(1);
        Optional<IFluidHandlerItem> fluidHandlerItem = FluidUtil.getFluidHandler(stack).resolve();
        Optional<IFluidHandlerItem> simItem = FluidUtil.getFluidHandler(simStack).resolve();
        if (fluidHandlerItem.isPresent())
        {
            IFluidHandlerItem handler = fluidHandlerItem.get();
            IFluidHandlerItem sim = simItem.orElse(new FluidHandlerItemStack(handler.getContainer(), handler.getTankCapacity(handler.getTanks())));
            FluidStack fluidInItem;
            if (fluidTank.isEmpty())
            {
                //If we don't have a fluid stored try draining in general
                fluidInItem = handler.drain(Integer.MAX_VALUE, SIMULATE);
            } else
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
                    SoundEvent event = fluidTank.getFluid().getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL);
                    int filled = handler.fill(fluidTank.getFluid(), EXECUTE);
                    ItemStack container = handler.getContainer();
                    if (filled > 0)
                    {
                        stackIn.shrink(1);
                        itemHandler.setStackInSlot(out, container);
                        if (world != null)
                        {
                            world.playSound(null, pos, event, BLOCKS, 1.0f, 1.0f);
                        }
                        fluidTank.drain(filled, EXECUTE);
                        return true;
                    }
                }
            } else //添加流体
            {
//                fluidTank.fill(fluidInItem, SIMULATE);
                int remainder = fluidTank.getFluidAmount();
                int storedAmount = fluidInItem.getAmount();
                int capacity = fluidTank.getCapacity();

                int filledAmount = Math.min(capacity - remainder, storedAmount);
                sim.drain(new FluidStack(fluidInItem, filledAmount), EXECUTE);
                boolean outEmpty = outStack.isEmpty();
                boolean canOutStack =
                        storedAmount == filledAmount && sim.getContainer().equals(outStack, false) && outStack.getCount() + 1 <= outStack.getMaxStackSize();
                if (!(outEmpty || canOutStack)) return false;
                FluidStack drained = handler.drain(new FluidStack(fluidInItem, filledAmount), EXECUTE);
                if (!drained.isEmpty())
                {
                    ItemStack container = handler.getContainer();
                    stackIn.shrink(1);
                    if (outEmpty)
                    {
                        itemHandler.setStackInSlot(out, container);
                    } else
                    {
                        itemHandler.setStackInSlot(out, new ItemStack(container.getItem(), outStack.getCount() + 1));
                    }
                    if (world != null)
                    {
                        SoundEvent event = drained.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
                        world.playSound(null, pos, event, BLOCKS, 1.0f, 1.0f);
                    }
                    fluidTank.fill(drained, EXECUTE);
                    return true;
                }
            }
        }
        return false;
    }
}
