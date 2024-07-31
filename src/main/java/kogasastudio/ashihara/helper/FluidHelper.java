package kogasastudio.ashihara.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

import static net.minecraft.sounds.SoundSource.BLOCKS;
import static net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidHelper
{
    /**
     * 判断特定流体是否能被添加进FluidTank
     *
     * @param fluidIn 待加入的流体
     * @param tank    待接收的tank
     * @return 接收可行性
     */
    public static boolean canFluidAddToTank(FluidStack fluidIn, FluidTank tank)
    {
        return (tank.isEmpty() || fluidIn.is((tank.getFluid().getFluid()))) && fluidIn.getAmount() + tank.getFluid().getAmount() <= tank.getCapacity();
    }

    /**
     * 与上方方法类似，判断是否能从fluidTank中抽取特定流体
     */
    public static boolean canFluidExtractFromTank(FluidStack fluidIn, FluidTank tank)
    {
        return fluidIn.is(tank.getFluid().getFluid()) && tank.getFluid().getAmount() >= fluidIn.getAmount();
    }

    public static boolean notifyFluidTankInteraction(Player player, InteractionHand hand, ItemStack stackIn, FluidTank fluidTank, Level world, BlockPos pos)
    {
        ItemStack stack = stackIn.copy();
        stack.setCount(1);
        Optional<IFluidHandlerItem> fluidHandlerItem = FluidUtil.getFluidHandler(stack);
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
                // Otherwise, try draining the same type of fluid we have stored
                // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
                // second tank but just asking to drain a specific amount
                fluidInItem = handler.drain(new FluidStack(fluidTank.getFluid().getFluid(), Integer.MAX_VALUE), SIMULATE);
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
                FluidStack drained = handler.drain(new FluidStack(fluidInItem.getFluid(), filledAmount), player.isCreative() ? SIMULATE : EXECUTE);
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
        Optional<IFluidHandlerItem> fluidHandlerItem = FluidUtil.getFluidHandler(stack);
        Optional<IFluidHandlerItem> simItem = FluidUtil.getFluidHandler(simStack);
        if (fluidHandlerItem.isPresent() && simItem.isPresent())
        {
            IFluidHandlerItem handler = fluidHandlerItem.get();
            IFluidHandlerItem sim = simItem.get();
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
                fluidInItem = handler.drain(new FluidStack(fluidTank.getFluid().getFluid(), Integer.MAX_VALUE), SIMULATE);
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
                sim.drain(new FluidStack(fluidInItem.getFluid(), filledAmount), EXECUTE);
                boolean outEmpty = outStack.isEmpty();
                boolean canOutStack = storedAmount == filledAmount && sim.getContainer().equals(outStack) && outStack.getCount() + 1 <= outStack.getMaxStackSize();
                if (!(outEmpty || canOutStack)) return false;
                FluidStack drained = handler.drain(new FluidStack(fluidInItem.getFluid(), filledAmount), EXECUTE);
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
