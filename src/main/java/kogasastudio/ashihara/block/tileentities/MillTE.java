package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipe.MillRecipe;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;

import java.util.Optional;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class MillTE extends AshiharaMachineTE implements ITickableTileEntity, INamedContainerProvider, IFluidHandler
{
    public MillTE() {super(TERegistryHandler.MILL_TE.get());}

    public ItemStackHandler input = new ItemStackHandler(4);
    public Inventory output = new Inventory(4);
    public byte round; //现在已经转的圈数
    public byte roundTotal; //完成这个配方需要转的总圈数, 由所选配方决定
    public int roundProgress; //现在正在转的这一圈的进度
    public int roundTicks; //每转一圈所需要的时间, 由所选配方决定
    public float exp; //转完配方可以得到的经验，由所选配方决定
    public boolean isWorking;
    private MillRecipe recipe;
    public LazyOptional<FluidTank> tank = LazyOptional.of(this::createTank);
    public IIntArray millData = new IIntArray()
    {
        public int get(int index)
        {
            switch (index)
            {
                case 0:return round;
                case 1:return roundTotal;
                case 2:return roundProgress;
                case 3:return roundTicks;
                default:return 0;
            }
        }

        public void set(int index, int value)
        {
            switch (index)
            {
                case 0:round = (byte) value;break;
                case 1:roundTotal = (byte) value;break;
                case 2:roundProgress = value;break;
                case 3:roundTicks = value;
            }
        }

        public int size () {return 4;}
    };

    private Optional<MillRecipe> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if(world == null) return Optional.empty();

        return world.getRecipeManager().getRecipe(MillRecipe.TYPE, wrapper, world);
    }

    private boolean hasInput()
    {
        for (int i = 0; i < 4; i += 1) {if (!input.getStackInSlot(i).isEmpty()) return true;}
        return false;
    }

    //检测当前匹配的配方其产出物是否能够填充进输出栏
    private boolean canProduce(MillRecipe recipe)
    {
        boolean flag = false;
        if (hasInput())
        {
            NonNullList<ItemStack> outputIn = recipe.getCraftingResult();//配方产物列表
            if (this.output.isEmpty()) flag = true;
            else
            {
                int availableSlotCount = 0;
                int emptySlotCount = 0;
                /*遍历给定产物的每一个物品并遍历当前输出栏的每一个格子
                若格子为空，空格子数+1
                若物品可以与格子中物品合并，有效格数+1 23 68 65
                若对于产出的每一个物品，都至少有一个不重复的格子可用，
                即空格子数+有效格子数大于给定产物的种数，则输出true*/
                for (ItemStack stack : outputIn)
                {
                    for (int j = 0; j < 4; j += 1) {if (output.getStackInSlot(j).isEmpty()) {emptySlotCount += 1;}}
                    for (int i = 0; i < 4; i += 1)
                    {
                        if (!output.getStackInSlot(i).isEmpty() && output.isItemValidForSlot(i, stack))
                        {
                            ItemStack stackInstant = output.getStackInSlot(i);
                            if (stack.isItemEqual(stackInstant) && stack.getCount() + stackInstant.getCount() <= stackInstant.getMaxStackSize())
                            {availableSlotCount += 1;break;}
                        }
                    }
                }
                if (availableSlotCount + emptySlotCount >= outputIn.size()) flag = true;
            }
        }
        return flag;
    }

    private void applyRecipe(MillRecipe recipeIn)
    {
        this.round = 0;
        this.roundProgress = 0;
        this.roundTotal = recipeIn.round;
        this.roundTicks = recipeIn.roundTicks;
        this.exp = recipeIn.exp;
        this.recipe = recipeIn;
        this.isWorking = true;
        this.sync();
        markDirty();
    }

    //结束时调用, 将te重置并生成产出物
    private void finishReciping(MillRecipe recipeIn)
    {
        this.round = 0;
        this.roundTotal = 0;
        this.roundProgress = 0;
        this.roundTicks = 0;
        this.isWorking = false;
        if (recipeIn != null)
        {
            if (recipeIn.getCraftingResult() != null)
            {
                for (ItemStack stack : this.recipe.getCraftingResult()) {output.addItem(stack);}
            }
            for (Ingredient ingredient : recipeIn.getIngredients())
            {
                for (int i = 0; i < this.input.getSlots(); i += 1)
                {
                    ItemStack stack = this.input.getStackInSlot(i);
                    if (!stack.isEmpty() && ingredient.test(stack)) {stack.shrink(recipeIn.getCosts(ingredient));break;}
                }
            }
        }
        this.recipe = null;
        this.sync();
        markDirty();
    }

    //获取磨石的旋转角度（角度制）
    public int getMillStoneRotation()
    {
        return this.roundTicks != 0 ? 360 * this.roundProgress / this.roundTicks : 0;
    }

    public ItemStackHandler getInput()
    {
        return input;
    }
    public Inventory getOutput() {return output;}

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

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        this.round = nbt.getByte("round");
        this.roundTotal = nbt.getByte("roundTotal");
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
        this.input.deserializeNBT(nbt.getCompound("input"));
        this.output.read(nbt.getList("output", 10));
        this.tank.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tank")));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putByte("round", this.round);
        compound.putByte("roundTotal", this.roundTotal);
        compound.putInt("roundProgress", this.roundProgress);
        compound.putInt("roundTicks", this.roundTicks);
        compound.put("input", this.input.serializeNBT());
        compound.put("output", this.output.write());
        this.tank.ifPresent(fluidTank -> compound.put("tank", fluidTank.writeToNBT(new CompoundNBT())));
        return super.write(compound);
    }

    @Override
    public void tick()
    {
        if (this.world == null) return;

        if (!this.world.isRemote())
        {
            if (hasInput())
            {
                boolean matchAny = false;
                Optional<MillRecipe> recipeIn = tryMatchRecipe(new RecipeWrapper(input));
                if (recipeIn.isPresent() && canProduce(recipeIn.get()))
                {
                    if (this.recipe == null || !this.recipe.equals(recipeIn.get())) applyRecipe(recipeIn.get());
                    matchAny = true;
                }
                if (!matchAny) {finishReciping(null);}
                if (isWorking)
                {
                    if (round == roundTotal) {finishReciping(recipe);}
                    else
                    {
                        if (roundProgress == roundTicks) {roundProgress = 0; round += 1;}
                        else {roundProgress += 1;}
                        this.sync();
                        markDirty();
                    }
                }
            }
            else if (isWorking) finishReciping(null);
        }
    }

    @Override
    public ITextComponent getDisplayName() {return new TranslationTextComponent("gui." + Ashihara.MODID + ".mill");}

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        if (this.world == null) return null;
        return new MillContainer(p_createMenu_1_, p_createMenu_2_, this.world, this.pos, this.millData);
    }

    private void sync()
    {
        if (this.world == null) return;
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("roundProgress", this.roundProgress);
        nbt.putInt("roundTicks", this.roundTicks);
        SUpdateTileEntityPacket p = new SUpdateTileEntityPacket(this.pos, 1, nbt);
        ((ServerWorld)this.world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.pos), false)
        .forEach(k -> k.connection.sendPacket(p));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getNbtCompound();
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
    }
}
