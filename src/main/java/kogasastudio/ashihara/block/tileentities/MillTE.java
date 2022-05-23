package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.interaction.recipe.MillRecipe;
import kogasastudio.ashihara.inventory.container.GenericItemStackHandler;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class MillTE extends AshiharaMachineTE implements TickableTileEntity, MenuProvider, IFluidHandler {
    public GenericItemStackHandler input = new GenericItemStackHandler(4);
    public GenericItemStackHandler output = new GenericItemStackHandler(4);
    public GenericItemStackHandler fluidIO = new GenericItemStackHandler(3);
    public byte round; //现在已经转的圈数
    public byte roundTotal; //完成这个配方需要转的总圈数, 由所选配方决定
    public int roundProgress; //现在正在转的这一圈的进度
    public int roundTicks; //每转一圈所需要的时间, 由所选配方决定
    public float exp; //转完配方可以得到的经验，由所选配方决定
    public boolean isWorking;
    public LazyOptional<FluidTank> tankIn = LazyOptional.of(this::createTank);
    public LazyOptional<FluidTank> tankOut = LazyOptional.of(this::createTank);
    public ContainerData millData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> round;
                case 1 -> roundTotal;
                case 2 -> roundProgress;
                case 3 -> roundTicks;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> round = (byte) value;
                case 1 -> roundTotal = (byte) value;
                case 2 -> roundProgress = value;
                case 3 -> roundTicks = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private MillRecipe recipe;
    public MillTE(BlockPos pos, BlockState state) {
        super(TERegistryHandler.MILL_TE.get(), pos, state);
    }

    private Optional<MillRecipe> tryMatchRecipe(RecipeWrapper wrapper) {
        if (level == null) {
            return Optional.empty();
        }

        return level.getRecipeManager().getRecipeFor(MillRecipe.TYPE, wrapper, level);
    }

    private boolean hasInput() {
        for (int i = 0; i < 4; i += 1) {
            if (!input.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //检测当前匹配的配方其产出物是否能够填充进输出栏
    private boolean canProduce(MillRecipe recipeIn) {
        boolean flag0 = false;
        boolean flag1 = true;
        boolean flag2 = true;
        if (hasInput()) {
            NonNullList<ItemStack> outputIn = recipeIn.getCraftingResult();//配方产物列表
            if (this.output.getStackInSlot(0).isEmpty()) {
                flag0 = true;
            } else {
                int availableSlotCount = 0;
                int emptySlotCount = 0;
                /*遍历给定产物的每一个物品并遍历当前输出栏的每一个格子
                若格子为空，空格子数+1
                若物品可以与格子中物品合并，有效格数+1
                若对于产出的每一个物品，都至少有一个不重复的格子可用，
                即空格子数+有效格子数大于给定产物的种数，则输出true*/
                for (ItemStack stack : outputIn) {
                    for (int j = 0; j < 4; j += 1) {
                        if (output.getStackInSlot(j).isEmpty()) {
                            emptySlotCount += 1;
                        }
                    }
                    for (int i = 0; i < 4; i += 1) {
                        if (!output.getStackInSlot(i).isEmpty() && output.isItemValid(i, stack)) {
                            ItemStack stackInstant = output.getStackInSlot(i);
                            if (stack.sameItem(stackInstant) && stack.getCount() + stackInstant.getCount() <= stackInstant.getMaxStackSize()) {
                                availableSlotCount += 1;
                                break;
                            }
                        }
                    }
                }
                if (availableSlotCount + emptySlotCount >= outputIn.size()) {
                    flag0 = true;
                }
            }
        }
        if (!recipeIn.getInputFluid().isEmpty()) {
            flag1 = FluidHelper.canFluidExtractFromTank(recipeIn.getInputFluid(), this.tankIn);
        }
        if (!recipeIn.getOutputFluid().isEmpty()) {
            flag2 = FluidHelper.canFluidAddToTank(recipeIn.getOutputFluid(), this.tankOut);
        }
        return flag0 && flag1 && flag2;
    }

    private void applyRecipe(MillRecipe recipeIn) {
        this.round = 0;
        this.roundProgress = 0;
        this.roundTotal = recipeIn.round;
        this.roundTicks = recipeIn.roundTicks;
        this.exp = recipeIn.exp;
        this.recipe = recipeIn;
        this.isWorking = true;
        this.sync();
        setChanged();
    }

    //结束时调用, 将te重置并生成产出物
    private void finishReciping(MillRecipe recipeIn) {
        this.round = 0;
        this.roundTotal = 0;
        this.roundProgress = 0;
        this.roundTicks = 0;
        this.isWorking = false;
        if (recipeIn != null) {
            if (recipeIn.getCraftingResult() != null) {
                for (ItemStack stack : this.recipe.getCraftingResult()) {
                    output.addItem(stack);
                }
            }
            for (Ingredient ingredient : recipeIn.getIngredients()) {
                for (int i = 0; i < this.input.getSlots(); i += 1) {
                    ItemStack stack = this.input.getStackInSlot(i);
                    if (!stack.isEmpty() && ingredient.test(stack)) {
                        stack.shrink(recipeIn.getCosts(ingredient));
                        break;
                    }
                }
            }
            if (!recipeIn.getInputFluid().isEmpty()) {
                this.tankIn.ifPresent
                        (
                                tank ->
                                {
                                    FluidStack fluid = recipeIn.getInputFluid();
                                    FluidStack fluidInTank = tank.getFluid();
                                    if (fluid.isFluidEqual(fluidInTank)) {
                                        fluidInTank.setAmount(Math.max(0, fluidInTank.getAmount() - fluid.getAmount()));
                                        tank.setFluid(fluidInTank);
                                        setChanged();
                                    }
                                }
                        );
            }
            if (!recipeIn.getOutputFluid().isEmpty()) {
                this.tankOut.ifPresent
                        (
                                tank ->
                                {
                                    FluidStack fluid = recipeIn.getOutputFluid();
                                    FluidStack fluidInTank = tank.getFluid();
                                    if (tank.isEmpty()) {
                                        tank.fill(fluid, EXECUTE);
                                    } else if (fluid.isFluidEqual(fluidInTank)) {
                                        fluidInTank.setAmount(Math.min(fluid.getAmount() + fluidInTank.getAmount(), tank.getCapacity()));
                                        tank.setFluid(fluidInTank);
                                        setChanged();
                                    }
                                }
                        );
            }
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition),
                        this.level.getBlockState(this.worldPosition), 3);
            }
        }
        this.recipe = null;
        this.sync();
        setChanged();
    }

    //获取磨石的旋转角度（角度制）
    public int getMillStoneRotation() {
        return this.roundTicks != 0 ? 360 * this.roundProgress / this.roundTicks : 0;
    }

    public ItemStackHandler getInput() {
        return input;
    }

    public GenericItemStackHandler getOutput() {
        return output;
    }

    @Override
    public FluidTank createTank() {
        return new FluidTank(4000);
    }

    @Override
    public FluidTank createTank(int capacity) {
        return new FluidTank(capacity);
    }

    @Override
    public LazyOptional<FluidTank> getTank() {
        return this.tankIn;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (!this.isRemoved() && cap.equals(FLUID_HANDLER_CAPABILITY)) {
            return this.tankIn.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.tankIn.invalidate();
        this.tankOut.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.tankIn = LazyOptional.of(this::createTank);
        this.tankOut = LazyOptional.of(this::createTank);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.round = nbt.getByte("round");
        this.roundTotal = nbt.getByte("roundTotal");
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
        this.input.deserializeNBT(nbt.getCompound("input"));
        this.fluidIO.deserializeNBT(nbt.getCompound("fluidIO"));
        this.output.deserializeNBT(nbt.getCompound("output"));
        this.tankIn.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tankIn")));
        this.tankOut.ifPresent(fluidTank -> fluidTank.readFromNBT(nbt.getCompound("tankOut")));
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putByte("round", this.round);
        compound.putByte("roundTotal", this.roundTotal);
        compound.putInt("roundProgress", this.roundProgress);
        compound.putInt("roundTicks", this.roundTicks);
        compound.put("input", this.input.serializeNBT());
        compound.put("fluidIO", this.fluidIO.serializeNBT());
        compound.put("output", this.output.serializeNBT());
        this.tankIn.ifPresent(fluidTank -> compound.put("tankIn", fluidTank.writeToNBT(new CompoundTag())));
        this.tankOut.ifPresent(fluidTank -> compound.put("tankOut", fluidTank.writeToNBT(new CompoundTag())));
        super.saveAdditional(compound);
    }

    @Override
    public void tick() {
        // todo 这时的 level 不会是 null
        // if (this.level == null) {
        //     return;
        // }

        if
        (
                FluidHelper.notifyFluidTankInteraction(this.fluidIO, 0, 2, this.tankIn.orElse(new FluidTank(0)), this.level, this.worldPosition)
                        || FluidHelper.notifyFluidTankInteraction(this.fluidIO, 1, 2, this.tankOut.orElse(new FluidTank(0)), this.level, this.worldPosition)
        ) {
            this.setChanged();
            if (this.level != null) {
                // todo 最后一个参数不再在混淆的时候去除了，可以在 Block 里调用常量
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition),
                        //                                           todo 之前你写的 3
                        this.level.getBlockState(this.worldPosition), Block.UPDATE_ALL);
            }
        }

        if (!this.level.isClientSide()) {
            if (hasInput()) {
                boolean matchAny = false;
                Optional<MillRecipe> recipeIn = tryMatchRecipe(new RecipeWrapper(input));
                if (recipeIn.isPresent() && canProduce(recipeIn.get())) {
                    if (this.recipe == null || !this.recipe.equals(recipeIn.get())) {
                        applyRecipe(recipeIn.get());
                    }
                    matchAny = true;
                }
                if (!matchAny) {
                    finishReciping(null);
                }
                if (isWorking) {
                    if (round == roundTotal) {
                        finishReciping(recipe);
                    } else {
                        if (roundProgress == roundTicks) {
                            roundProgress = 0;
                            round += 1;
                        } else {
                            roundProgress += 1;
                        }
                        this.sync();
                        setChanged();
                    }
                }
            } else if (isWorking) finishReciping(null);
        }
    }

    @Override
    public TranslatableComponent getDisplayName() {
        return new TranslatableComponent("gui." + Ashihara.MODID + ".mill");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        if (this.level == null) {
            return null;
        }
        return new MillContainer(p_createMenu_1_, p_createMenu_2_, this.level, this.worldPosition, this.millData);
    }

    // todo 我感觉很没有必要
    private void sync() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("roundProgress", this.roundProgress);
        nbt.putInt("roundTicks", this.roundTicks);
        this.tankIn.ifPresent(tank -> nbt.put("tankIn", tank.writeToNBT(new CompoundTag())));
        this.tankOut.ifPresent(tank -> nbt.put("tankOut", tank.writeToNBT(new CompoundTag())));
        //                                                                  todo create 的单参调用 getUpdateTag
        ClientboundBlockEntityDataPacket p = ClientboundBlockEntityDataPacket.create(this, (be) -> nbt);
        ((ServerLevel) this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false)
                .forEach(k -> k.connection.send(p));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        this.roundProgress = nbt.getInt("roundProgress");
        this.roundTicks = nbt.getInt("roundTicks");
        this.tankIn.ifPresent(tank -> tank.readFromNBT(nbt.getCompound("tankIn")));
        this.tankOut.ifPresent(tank -> tank.readFromNBT(nbt.getCompound("tankOut")));
    }
}
