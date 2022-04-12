package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.interaction.recipe.MortarRecipe;
import kogasastudio.ashihara.inventory.container.GenericItemStackHandler;
import kogasastudio.ashihara.inventory.container.MortarContainer;
import kogasastudio.ashihara.item.ItemOtsuchi;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static kogasastudio.ashihara.utils.AshiharaTags.MASHABLE;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class MortarTE extends AshiharaMachineTE implements INamedContainerProvider, IFluidHandler
{
    public MortarTE() {super(TERegistryHandler.MORTAR_TE.get());}

    public MortarInventory contents = new MortarInventory(4);
    public GenericItemStackHandler fluidIO = new GenericItemStackHandler(2);
    public LazyOptional<FluidTank> tank = LazyOptional.of(this::createTank);
    public NonNullList<ItemStack> output = NonNullList.create();
    public FluidStack fluidCost = FluidStack.EMPTY;

    public int progress;
    public int progressTotal;

    /**
     * 0: 脱谷
     * 1: 打麻糬
     * 2: 制酱
     */
    public byte recipeType = -1;
    public byte pointer = -1;
    /**
     * 0: 手
     * 1: 杵
     * 2: 大槌
     */
    public byte nextStep = -1;

    public byte[] sequence = new byte[0];
    public boolean isWorking;

    public IIntArray mortarData = new IIntArray()
    {
        public int get(int index)
        {
            switch (index)
            {
                case 0 : return progress;
                case 1 : return progressTotal;
                case 2 : return nextStep;
                default: return 0;
            }
        }

        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 : progress = value;break;
                case 1 : progressTotal = value;break;
                case 2 : nextStep = (byte) value;
            }
        }

        public int size () {return 3;}
    };

    public int getContentsActualSize() {return this.contents.getActualSize();}

    @Override
    public FluidTank createTank() {return new FluidTank(4000);}

    @Override
    public LazyOptional<FluidTank> getTank() {return this.tank;}

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap)
    {
        if (!this.isRemoved() && cap.equals(FLUID_HANDLER_CAPABILITY)) {return this.tank.cast();}
        return super.getCapability(cap);
    }

    @Override
    protected void invalidateCaps()
    {
        super.invalidateCaps();
        this.tank.invalidate();
    }

    @Override
    protected void reviveCaps()
    {
        super.reviveCaps();
        tank = LazyOptional.of(this::createTank);
    }

    public static class MortarInventory extends GenericItemStackHandler
    {
        public MortarInventory(int numSlots) {super(numSlots);}

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {return 1;}
    }

    private void process(boolean isSauceProcess)
    {
        this.progress += isSauceProcess ? this.nextStep : 1;
        if (this.progress >= this.progressTotal) {finishReciping(true);markDirty();return;}
        if (!this.isWorking) this.isWorking = true;
        if (!isSauceProcess)
        {
            this.pointer += 1;
            this.nextStep = this.sequence[this.pointer];
        }
        markDirty();
    }

    private boolean isNextStepNeeded(ItemStack stack)
    {
        switch (this.nextStep)
        {
            case 0 : return stack.isEmpty();
            case 1 : return stack.getItem().equals(ItemRegistryHandler.PESTLE.get());
            case 2 : return stack.getItem() instanceof ItemOtsuchi;
        }
        return false;
    }

    private Optional<MortarRecipe> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if(world == null) return Optional.empty();

        return world.getRecipeManager().getRecipe(MortarRecipe.TYPE, wrapper, world);
    }

    //检查当前状态, 若内容物匹配配方则尝试启用配方
    public void notifyStateChanged()
    {
        Optional<MortarRecipe> recipeIn = tryMatchRecipe(new RecipeWrapper(this.contents));
        if (recipeIn.isPresent())
        {
            boolean flag = false;
            if (!recipeIn.get().getFluidCost().isEmpty())
            {
                 flag = FluidHelper.canFluidExtractFromTank(recipeIn.get().getFluidCost(), this.tank);
            }
            if (!this.isWorking && flag) applyRecipe(recipeIn.get());
        }
        else finishReciping(false);
        if (FluidHelper.notifyFluidTankInteraction(this.fluidIO, 0, 1, this.tank.orElse(new FluidTank(0)), this.world, this.pos))
        {
            this.markDirty();
            if (this.world != null) this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    private void applyRecipe(MortarRecipe recipeIn)
    {
        this.progress = 0;
        this.pointer = 0;

        this.recipeType = recipeIn.recipeType;
        this.progressTotal = recipeIn.progress;
        this.sequence = recipeIn.sequence;
        this.fluidCost = recipeIn.getFluidCost();
        this.nextStep = recipeIn.recipeType == 2 ? -1 : this.sequence[this.pointer];
        this.output.clear();
        for (ItemStack stack : recipeIn.getOutput())
        {
            this.output.add(stack.copy());
        }
        LOGGER_MAIN.info("recipe applied: " + recipeIn.getInfo());
        markDirty();
    }

    private void finishReciping(boolean produce)
    {
        this.progress = 0;
        this.progressTotal = 0;
        this.sequence = new byte[0];
        this.recipeType = -1;
        this.pointer = -1;
        this.nextStep = -1;
        if (produce)
        {
            this.produce();
        }
        this.fluidCost = FluidStack.EMPTY;
        this.output.clear();
        this.isWorking = false;
        markDirty();
    }

    private void produce()
    {
        this.contents.clear();
        for (int i = 0; i < this.output.size(); i += 1)
        {
            ItemStack stack = this.output.get(i);
            if (!stack.isEmpty()) this.contents.setStackInSlot(i, stack);
        }
        if (!this.fluidCost.isEmpty())
        {
            this.tank.ifPresent
            (
                tank ->
                {
                    FluidStack fluidInTank = tank.getFluid();
                    if (this.fluidCost.isFluidEqual(fluidInTank))
                    {
                        fluidInTank.setAmount(Math.max(0, fluidInTank.getAmount() - this.fluidCost.getAmount()));
                        tank.setFluid(fluidInTank);
                        markDirty();
                    }
                }
            );
        }
    }

    //若不在工作状态中空手右击则取出物品，持物品右击则尝试将物品放入舂
    public boolean notifyInteraction(ItemStack stackIn, World worldIn, BlockPos posIn, PlayerEntity player)
    {
        if (isNextStepNeeded(stackIn))
        {
            player.getCooldownTracker().setCooldown(stackIn.getItem(), 8);
            if (!stackIn.isEmpty() && !player.isCreative())
            {
                stackIn.damageItem(1, player, (playerEntity) -> player.sendBreakAnimation(EquipmentSlotType.MAINHAND));
            }
            process(this.recipeType == 2);
            return true;
        }
        if (!this.isWorking)
        {
            for (int i = 0; i < this.contents.getSlots(); i += 1)
            {
                ItemStack stack = this.contents.getStackInSlot(i);
                if (!stack.isEmpty() && stackIn.isEmpty())
                {
                    this.contents.setStackInSlot(i, ItemStack.EMPTY);
                    notifyStateChanged();
                    markDirty();
                    InventoryHelper.spawnItemStack(worldIn, posIn.getX(), posIn.getY() + 0.5F, posIn.getZ(), stack);
                    return true;
                }
                else if (stack.isEmpty() && stackIn.getItem().isIn(MASHABLE))
                {
                    this.contents.insertItem(i, new ItemStack(stackIn.getItem()), false);
                    if (!player.isCreative()) stackIn.shrink(1);
                    notifyStateChanged();
                    markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putInt("progress", this.progress);
        compound.putInt("progressTotal", this.progressTotal);

        compound.putByte("recipeType", this.recipeType);
        compound.putByte("pointer", this.pointer);
        compound.putByte("nextStep", this.nextStep);

        compound.putByteArray("sequence", this.sequence);
        compound.putBoolean("isWorking", this.isWorking);

        compound.put("contents", this.contents.serializeNBT());
        compound.put("fluidCost", this.fluidCost.writeToNBT(new CompoundNBT()));
        compound.put("fluidIO", this.fluidIO.serializeNBT());

        this.tank.ifPresent(fluidTank -> compound.put("tank", fluidTank.writeToNBT(new CompoundNBT())));

        ListNBT outputIn = new ListNBT();
        for (int i = 0; i < this.output.size(); i++)
        {
            if (!this.output.get(i).isEmpty())
            {
                CompoundNBT itemTag = new CompoundNBT();
                itemTag.putInt("Slot", i);
                outputIn.add(this.output.get(i).write(itemTag));
            }
        }
        compound.put("output", outputIn);
        super.write(compound);

        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.progress = nbt.getInt("progress");
        this.progressTotal = nbt.getInt("progressTotal");

        this.recipeType = nbt.getByte("recipeType");
        this.pointer = nbt.getByte("pointer");
        this.nextStep = nbt.getByte("nextStep");

        this.sequence = nbt.getByteArray("sequence");
        this.isWorking = nbt.getBoolean("isWorking");

        this.contents.deserializeNBT(nbt.getCompound("contents"));
        this.fluidCost = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluidCost"));
        this.fluidIO.deserializeNBT(nbt.getCompound("fluidIO"));

        this.tank.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tank")));

        ListNBT outputIn = nbt.getList("output", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < outputIn.size(); i++)
        {
            CompoundNBT itemTags = outputIn.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < this.output.size())
            {
                this.output.set(slot, ItemStack.read(itemTags));
            }
        }
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("gui." + Ashihara.MODID + ".mortar");
    }

    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        if (this.world == null) return null;
        return new MortarContainer(p_createMenu_1_, p_createMenu_2_, this, this.mortarData);
    }
}
