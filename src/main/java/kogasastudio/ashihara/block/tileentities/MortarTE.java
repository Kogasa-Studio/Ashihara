package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import java.util.HashMap;
import java.util.Map;

public class MortarTE extends AshiharaMachineTE implements IFluidHandler // extends AshiharaMachineTE implements MenuProvider, IFluidHandler
{
    public final FluidTank fluidTank = new FluidTank(16000);

    public static final int SLOT_0 = 0;
    public static final int SLOT_1 = 1;
    public static final int SLOT_2 = 2;
    public static final int SLOT_3 = 3;

    public boolean renderFloatingTip = false;
    public boolean transitingLiquidLevel = false;

    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    public Map<Integer, ItemStack> inventory = new HashMap<>();

    public MortarTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MORTAR_TE.get(), pos, state);
    }

    @Override
    public int getTanks()
    {
        return 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return fluidTank.getFluidInTank(tank);
    }

    public static IItemHandler getInv(MortarTE te, Direction side)
    {
        if (side.getAxis().equals(Direction.Axis.Y))
        {
            return new RangedWrapper(te, 0, 4);
        }
        return null;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return fluidTank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        return fluidTank.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return fluidTank.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return fluidTank.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return fluidTank.drain(maxDrain, action);
    }
}
