package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class MillTE extends AshiharaMachineTE implements IItemHandler, IFluidHandler {

    public MillTE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    private final static int INVENTORY_CAPACITY = 3;
    private final static int MILLSTONE_SLOT_ID = 0;
    private final static int INPUT_SLOT_ID = 1;
    private final static int OUTPUT_SLOT_ID = 2;

    private final static int TANK_CAPACITY = 2;
    private final static int INPUT_TANK_ID = 0;
    private final static int OUTPUT_TANK_ID = 1;

    private final static float MAX_ANGULAR_VELOCITY = 5;

    private boolean working = false;

    private ItemStackHandler inventory = new ItemStackHandler(INVENTORY_CAPACITY);

    private FluidStack inputTank = FluidStack.EMPTY;
    private FluidStack outputTank = FluidStack.EMPTY;

    private float angularVelocity = 0;
    private float circle = 0;

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return inventory.isItemValid(slot, stack);
    }

    @Override
    public int getTanks() {
        return TANK_CAPACITY;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return switch (tank) {
            case INPUT_SLOT_ID -> inputTank;
            default -> throw new IllegalStateException("Unexpected value: " + tank);
        };
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return null;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        return null;
    }

    /*public GenericItemStackHandler input = new GenericItemStackHandler(4);
    public GenericItemStackHandler output = new GenericItemStackHandler(4);

    public NonNullList<ItemStack> outList = NonNullList.create();
    public GenericItemStackHandler fluidIO = new GenericItemStackHandler(3);
    public byte round; //现在已经转的圈数
    public byte roundTotal; //完成这个配方需要转的总圈数, 由所选配方决定
    public int roundProgress; //现在正在转的这一圈的进度
    public int roundTicks; //每转一圈所需要的时间, 由所选配方决定
    public float exp; //转完配方可以得到的经验，由所选配方决定
    public boolean isWorking;
    public FluidTank tankIn = this.createTank();
    public FluidTank tankOut = this.createTank();

    private final RecipeManager.CachedCheck<RecipeWrapper, MillRecipe> quickCheck;
    public ContainerData millData = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> round;
                case 1 -> roundTotal;
                case 2 -> roundProgress;
                case 3 -> roundTicks;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> round = (byte) value;
                case 1 -> roundTotal = (byte) value;
                case 2 -> roundProgress = value;
                case 3 -> roundTicks = value;
            }
        }

        @Override
        public int getCount()
        {
            return 4;
        }
    };

    public MillTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MILL_TE.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(RecipeTypes.MILL.get());
    }

    private Optional<RecipeHolder<MillRecipe>> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if (this.level == null) return Optional.empty();

        return this.quickCheck.getRecipeFor(wrapper, this.level);
    }

    private boolean hasInput()
    {
        return !this.input.isEmpty();
    }


    //todo:
    // 好像性能没差多少，但是我感觉好看点了（
    // simulate 为 true 的时候不会修改内容，但是会判断能不能放入
    // 我的观念是能复用轮子就复用轮子
    //检测当前匹配的配方其产出物是否能够填充进输出栏
    private boolean canProduce(MillRecipe r)
    {
        var result = r.matches(input.getContent().stream().filter(i -> !i.isEmpty()).collect(Collectors.toList())) &&
                r.testInputFluid(tankIn) &&
                tankOut.fill(r.getOutputFluid(), SIMULATE) == r.getOutputFluid().getAmount();

        if (result)
        {
            var outputList = new ArrayList<>(r.getCraftingResult());
            int slotNeeded = 0;

            for (int i = 0; i < outputList.size(); i++)
            {
                for (int i1 = 0; i1 < output.getActualSize(); i1++)
                {
                    if (!outputList.get(i).isEmpty())
                    {
                        outputList.set(i, output.addItem(outputList.get(i)));
                    }
                }
            }

            return outputList.stream().allMatch(ItemStack::isEmpty);
        }

        return false;
    }

    private void applyRecipe(MillRecipe recipeIn)
    {
        this.round = 0;
        this.roundProgress = 0;
        this.roundTotal = recipeIn.round;
        this.roundTicks = recipeIn.roundTicks;
        this.outList.clear();
        for (ItemStack stack : recipeIn.getCraftingResult())
        {
            this.outList.add(stack.copy());
        }
        this.exp = recipeIn.exp;
        this.isWorking = true;
        this.sync();
        setChanged();
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
                for (ItemStack stack : this.outList)
                {
                    output.addItem(stack);
                }
            }

            int size = input.getSlots();
            List<Ingredient> ingredients = new ArrayList<>(recipeIn.getIngredients());

            int ingredientSize = ingredients.size();
            for (int i = 0; i < size - ingredientSize; i++)
            {
                ingredients.add(Ingredient.EMPTY);
            }

            int[] matches = RecipeMatcher.findMatches(input.getContent(), ingredients);

            if (matches != null)
            {
                for (int i = 0; i < matches.length; i++)
                {
                    input.extractItem(i, recipeIn.getCosts(ingredients.get(matches[i])), false);
                }
            }

            if (!recipeIn.getInputFluid().isEmpty())
            {
                // todo 直接用 drain
                tankIn.ifPresent(tank ->
                {
                    tank.drain(recipeIn.getInputFluid(), EXECUTE);
                });
                // this.tankIn.ifPresent
                //         (
                //                 tank ->
                //                 {
                //                     FluidStack fluid = recipeIn.getInputFluid();
                //                     FluidStack fluidInTank = tank.getFluid();
                //                     if (fluid.isFluidEqual(fluidInTank)) {
                //                         fluidInTank.setAmount(Math.max(0, fluidInTank.getAmount() - fluid.getAmount()));
                //                         tank.setFluid(fluidInTank);
                //                         setChanged();
                //                     }
                //                 }
                //         );
            }
            if (!recipeIn.getOutputFluid().isEmpty())
            {
                this.tankOut.ifPresent
                        (
                                tank ->
                                {
                                    FluidStack fluid = recipeIn.getOutputFluid();
                                    FluidStack fluidInTank = tank.getFluid();
                                    if (tank.isEmpty())
                                    {
                                        tank.fill(fluid, EXECUTE);
                                    } else if (fluid.isFluidEqual(fluidInTank))
                                    {
                                        fluidInTank.setAmount(Math.min(fluid.getAmount() + fluidInTank.getAmount(), tank.getCapacity()));
                                        tank.setFluid(fluidInTank);
                                        setChanged();
                                    }
                                }
                        );
            }
            if (this.level != null)
            {
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition),
                        this.level.getBlockState(this.worldPosition), 3);
            }
        }
        this.outList.clear();
        this.sync();
        setChanged();
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

    public GenericItemStackHandler getOutput()
    {
        return output;
    }

    @Override
    public FluidTank createTank()
    {
        return new FluidTank(4000);
    }

    @Override
    public FluidTank createTank(int capacity)
    {
        return new FluidTank(capacity);
    }

    @Override
    public LazyOptional<FluidTank> getTank()
    {
        return this.tankIn;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap)
    {
        if (!this.isRemoved() && cap.equals(ForgeCapabilities.FLUID_HANDLER))
        {
            return this.tankIn.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        this.tankIn.invalidate();
        this.tankOut.invalidate();
    }

    @Override
    public void reviveCaps()
    {
        super.reviveCaps();
        this.tankIn = LazyOptional.of(this::createTank);
        this.tankOut = LazyOptional.of(this::createTank);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        this.round = nbt.getByte("round");
        this.roundTotal = nbt.getByte("roundTotal");
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
        this.input.deserializeNBT(nbt.getCompound("input"));
        this.fluidIO.deserializeNBT(nbt.getCompound("fluidIO"));
        this.output.deserializeNBT(nbt.getCompound("output"));
        this.tankIn.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tankIn")));
        this.tankOut.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tankOut")));

        ListTag outputIn = nbt.getList("outList", Tag.TAG_COMPOUND);
        for (int i = 0; i < outputIn.size(); i++)
        {
            CompoundTag itemTags = outputIn.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0)
            {
                this.outList.add(slot, ItemStack.of(itemTags));
            }
        }
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound)
    {
        compound.putByte("round", this.round);
        compound.putByte("roundTotal", this.roundTotal);
        compound.putInt("roundProgress", this.roundProgress);
        compound.putInt("roundTicks", this.roundTicks);
        compound.put("input", this.input.serializeNBT());
        compound.put("fluidIO", this.fluidIO.serializeNBT());
        compound.put("output", this.output.serializeNBT());
        this.tankIn.ifPresent(fluidTank -> compound.put("tankIn", fluidTank.writeToNBT(new CompoundTag())));
        this.tankOut.ifPresent(fluidTank -> compound.put("tankOut", fluidTank.writeToNBT(new CompoundTag())));

        ListTag outputIn = new ListTag();
        for (int i = 0; i < this.outList.size(); i++)
        {
            if (!this.outList.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                outputIn.add(this.outList.get(i).save(itemTag));
            }
        }
        compound.put("outList", outputIn);

        super.saveAdditional(compound);
    }

    @Override
    public void tick()
    {
        if (this.level == null)
        {
            return;
        }

        if
        (
                FluidHelper.notifyFluidTankInteraction(this.fluidIO, 0, 2, this.tankIn.orElse(new FluidTank(0)), this.level, this.worldPosition)
                        || FluidHelper.notifyFluidTankInteraction(this.fluidIO, 1, 2, this.tankOut.orElse(new FluidTank(0)), this.level, this.worldPosition)
        )
        {
            this.setChanged();
            if (this.level != null)
            {
                // todo 最后一个参数不再在混淆的时候去除了，可以在 Block 里调用常量
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition),
                        //                                           todo 之前你写的 3
                        this.level.getBlockState(this.worldPosition), Block.UPDATE_ALL);
            }
        }

        if (!this.level.isClientSide())
        {
            if (hasInput())
            {

                boolean matchAny = false;
                Optional<MillRecipe> recipeIn = tryMatchRecipe();
                if (recipeIn.isPresent() && canProduce(recipeIn.get()))
                {
                    if (!isWorking) applyRecipe(recipeIn.get());
                    matchAny = true;
                }
                if (!matchAny)
                {
                    finishReciping(null);
                }
                if (isWorking)
                {
                    if (round >= roundTotal)
                    {
                        finishReciping(recipeIn.get());
                    } else
                    {
                        if (roundProgress == roundTicks)
                        {
                            roundProgress = 0;
                            round += 1;
                        } else
                        {
                            roundProgress += 1;
                        }
                        this.sync();
                        setChanged();
                    }
                }
            } else if (isWorking)
            {
                finishReciping(null);
            }
        }
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("gui." + Ashihara.MODID + ".mill");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_)
    {
        if (this.level == null)
        {
            return null;
        }
        return new MillContainer(p_createMenu_1_, p_createMenu_2_, this.level, this.worldPosition, this.millData);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("roundProgress", this.roundProgress);
        nbt.putInt("roundTicks", this.roundTicks);
        this.tankIn.ifPresent(tank -> nbt.put("tankIn", tank.writeToNBT(new CompoundTag())));
        this.tankOut.ifPresent(tank -> nbt.put("tankOut", tank.writeToNBT(new CompoundTag())));
        return nbt;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag nbt = pkt.getTag();
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
        this.tankIn.ifPresent(tank -> tank.readFromNBT(nbt.getCompound("tankIn")));
        this.tankOut.ifPresent(tank -> tank.readFromNBT(nbt.getCompound("tankOut")));
    }*/
}
