package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.DirtCookStoveBlock;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class DirtCookStoveBE extends AshiharaCommonBE
{
    public static final int INVENTORY_SIZE = 1;

    public final BEItemStackHandler<DirtCookStoveBE> fuelStorage;
    public boolean isBurning = false;

    public DirtCookStoveBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.DIRT_COOKSTOVE_BE.get(), pos, state);
        this.fuelStorage = new BEItemStackHandler<>(INVENTORY_SIZE, this)
        {
            @Override
            public boolean isValid(int index, ItemResource resource)
            {
                if (DirtCookStoveBE.this.level == null) return false;
                return resource.toStack().getBurnTime(null, DirtCookStoveBE.this.level.fuelValues()) > 0;
            }
        };
    }

    public int remainingFuelTicks;

    public static void tick(Level level, BlockPos pos, BlockState state, DirtCookStoveBE be)
    {
        if (be.isBurning)
        {
            be.remainingFuelTicks = Math.max(0, be.remainingFuelTicks - 1);
            if (be.remainingFuelTicks == 0) be.consumeFuel();
        }
    }

    public static ResourceHandler<ItemResource> getFuelStorage(DirtCookStoveBE be, Direction side)
    {
        return be.fuelStorage;
    }

    public boolean consumeFuel()
    {
        ItemStack stack = fuelStorage.getStackInSlot(0);
        if (stack.isEmpty() || this.level == null)
        {
            this.isBurning = false;
            this.sync();
            this.setChanged();
            return false;
        }
        this.remainingFuelTicks = stack.getBurnTime(null, this.level.fuelValues());
        this.isBurning = true;
        fuelStorage.consumeItemStack(i -> i.is(stack.getItem()), 1);
        this.sync();
        this.setChanged();
        return true;
    }

    public boolean canBurn()
    {
        if (this.level == null) return false;
        BlockState bs = this.level.getBlockState(this.getBlockPos());
        return bs.getBlock().equals(this.getBlockState().getBlock()) && !bs.getValue(DirtCookStoveBlock.WATERLOGGED);
    }

    public boolean hasFuel()
    {
        return !this.fuelStorage.getStackInSlot(0).isEmpty();
    }

    public void ignite()
    {
        if (this.level == null) return;
        if (canBurn() && !this.isBurning && consumeFuel())
        {
            this.sync();
            this.setChanged();
        }
    }

    public void extinguish()
    {
        if (this.level != null && this.isBurning)
        {
            this.isBurning = false;
            this.remainingFuelTicks = 0;
            BlockState bs = this.level.getBlockState(this.getBlockPos());
            this.level.setBlock(this.getBlockPos(), bs.setValue(DirtCookStoveBlock.BURNING, false), 3);
            if (!this.level.isClientSide()) this.level.playSound(null, this.getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void updateBlockState()
    {
        if (this.level == null) return;
        BlockState bs = this.level.getBlockState(this.getBlockPos());
        if (!bs.getBlock().equals(this.getBlockState().getBlock())) return;
        boolean needUpdate = false;
        boolean burning = bs.getValue(DirtCookStoveBlock.BURNING);
        boolean hasFireWood = bs.getValue(DirtCookStoveBlock.FIREWOOD);
        boolean hasAsh = bs.getValue(DirtCookStoveBlock.ASH);
        if (!canBurn() && burning)
        {
            extinguish();
        }
        else if (burning != this.isBurning)
        {
            needUpdate = true;
            bs = bs.setValue(DirtCookStoveBlock.BURNING, this.isBurning);
        }
        if (hasFireWood != this.hasFuel())
        {
            needUpdate = true;
            bs = bs.setValue(DirtCookStoveBlock.FIREWOOD, this.hasFuel());
        }
        if (!hasAsh && this.isBurning)
        {
            needUpdate = true;
            bs = bs.setValue(DirtCookStoveBlock.ASH, true);
        }
        if (needUpdate)
        {
            this.level.setBlock(this.getBlockPos(), bs, 3);
        }
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        updateBlockState();
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putChild("fuelStorage", this.fuelStorage);
        output.putInt("remainingBurnTime", this.remainingFuelTicks);
        output.putBoolean("isBurning", this.isBurning);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        input.readChild("fuelStorage", this.fuelStorage);
        this.remainingFuelTicks = input.getIntOr("remainingBurnTime", 0);
        this.isBurning = input.getBooleanOr("isBurning", false);
    }

    public void dropContents(Level level, BlockPos pos)
    {
        Block.popResource(level, pos, this.fuelStorage.getStackInSlot(0));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        if (this.level != null && !this.level.isClientSide())
        {
            dropContents(this.level, this.getBlockPos());
        }
        super.preRemoveSideEffects(pos, state);
    }
}
