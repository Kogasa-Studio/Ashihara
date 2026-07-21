package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.FermentationBlock;
import kogasastudio.ashihara.block.LargeFermentationVatBlock;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.interaction.recipes.FermentationRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.inventory.container.FermentationMenu;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class FermentationBlockEntity extends AshiharaCommonBE implements MenuProvider, IItemHandler<FermentationBlockEntity>, IFluidHandler
{
    // -- Size ----------------------------------------------------------------

    public enum Size
    {
        BASIN(1), VAT(1), LARGE_VAT(2);

        public final int blockSize;

        Size(int blockSize) { this.blockSize = blockSize; }
    }

    public interface IFermentationSizeProvider
    {
        Size getSize();
    }

    // -- Storage -------------------------------------------------------------

    public final BEItemStackHandler<FermentationBlockEntity> inventory;
    public final BEFluidStackHandler<FermentationBlockEntity> fluid;
    public final Size size;

    // -- Recipe state --------------------------------------------------------

    private boolean isFermenting;
    private int fermentTime;
    private int maxFermentTime;
    public int parallel = 1;
    @Nullable public FermentationRecipe currentRecipe;
    @Nullable private Identifier lastRecipe;
    @Nullable public FermentationRecipe availableRecipe;
    @Nullable private List<Component> unavailabilityMessages;
    private boolean refreshing;
    @Nullable private String pendingRecipeId;

    // -- Misc / rendering ----------------------------------------------------

    public float prevFluidLevel = 0f;
    public float fluidLevel = 0f;
    public boolean fluidLevelChanged = false;
    public boolean inited = false;
    private FermentationDisplayModel model;
    public Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();

    // -- Constructor ---------------------------------------------------------

    public FermentationBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.FERMENTATION_BE.get(), pos, state);
        this.size = state.getBlock() instanceof IFermentationSizeProvider fp ? fp.getSize() : Size.BASIN;
        this.inventory = switch (this.size)
        {
            case BASIN -> new BEItemStackHandler<>(4, this, this::refreshRecipe);
            case VAT -> new BEItemStackHandler<>(8, this, this::refreshRecipe);
            case LARGE_VAT -> new BEItemStackHandler<>(16, this, this::refreshRecipe);
        };
        this.fluid = switch (this.size)
        {
            case BASIN -> new BEFluidStackHandler<>(16000, this, this::refreshRecipe);
            case VAT -> new BEFluidStackHandler<>(128000, this, this::refreshRecipe);
            case LARGE_VAT -> new BEFluidStackHandler<>(1024000, this, this::refreshRecipe);
        };
    }

    // -- Capability providers ------------------------------------------------

    public static ResourceHandler<ItemResource> getItemHandler(FermentationBlockEntity be, Direction direction) { return be.inventory; }
    public static ResourceHandler<FluidResource> getFluidHandler(FermentationBlockEntity be, Direction direction) { return be.fluid; }

    @Override public BEFluidStackHandler<?> getTank() { return this.fluid; }
    @Override public ResourceHandler<ItemResource> getItemResource(FermentationBlockEntity be, Direction direction) { return getItemHandler(be, direction); }

    // -- MenuProvider --------------------------------------------------------

    @Override
    public Component getDisplayName()
    {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        ContainerData data = new ContainerData()
        {
            @Override public int get(int index)
            {
                return switch (index) { case 0 -> fermentTime; case 1 -> maxFermentTime; default -> 0; };
            }
            @Override public void set(int index, int value)
            {
                switch (index) { case 0 -> fermentTime = value; case 1 -> maxFermentTime = value; }
            }
            @Override public int getCount() { return 2; }
        };
        return new FermentationMenu(containerId, inventory, this, data);
    }

    // -- Accessors -----------------------------------------------------------

    public boolean isFermenting()        { return isFermenting; }
    public int     getFermentTime()      { return fermentTime; }
    public int     getMaxFermentTime()   { return maxFermentTime; }
    public float   getFermentProgress()  { return maxFermentTime > 0 ? (float) fermentTime / maxFermentTime : 0f; }
    public void    setParallel(int val)  { this.parallel = val; }
    public @Nullable FermentationRecipe getAvailableRecipe() { return availableRecipe; }

    public int getMaxParallel()
    {
        return switch (this.size)
        {
            case BASIN -> 16;
            case VAT -> 128;
            case LARGE_VAT -> 1024;
        };
    }

    public List<Component> getUnavailabilityMessages()
    {
        return this.unavailabilityMessages == null ? List.of() : this.unavailabilityMessages;
    }

    public String getSizeId()
    {
        return switch (this.size)
        {
            case VAT -> "vat";
            case LARGE_VAT -> "large_vat";
            default -> "basin";
        };
    }

    public char getSizePrefix()
    {
        return switch (this.size)
        {
            case VAT -> 'v';
            case LARGE_VAT -> 'l';
            default -> 'b';
        };
    }

    public BoneTracer getBoneTracer(String id)
    {
        return this.boneTracers.get(id + this.getSizeId());
    }

    // -- Recipe detection (lazy) ---------------------------------------------

    private boolean hasAir()
    {
        if (this.level == null) return true;
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (state.getBlock() instanceof FermentationBlock)
        {
            return !state.getValue(FermentationBlock.HAS_LID);
        }
        return true;
    }

    public void refreshRecipe()
    {
        if (this.refreshing || this.level == null) return;
        this.refreshing = true;
        try
        {
            Collection<RecipeHolder<FermentationRecipe>> recipes = RecipeHelper.getRecipesByType(this.level, RecipeTypes.FERMENTATION.get());
            FermentationRecipe match = null;

            // 1) Sticky: prefer last-known recipe
            if (this.lastRecipe != null)
            {
                match = recipes.stream()
                    .filter(h -> h.value().getId().equals(this.lastRecipe) && h.value().testBE(this))
                    .map(RecipeHolder::value)
                    .findFirst().orElse(null);
            }

            // 2) priority-based matching
            if (match == null)
            {
                boolean hasItems = !this.inventory.isEmpty();
                if (hasItems)
                {
                    match = recipes.stream()
                        .filter(h -> h.value().hasItemInput() && h.value().testBE(this))
                        .map(RecipeHolder::value)
                        .max(Comparator.comparingInt(FermentationRecipe::getPriority))
                        .orElse(null);
                }
                if (match == null)
                {
                    match = recipes.stream()
                        .filter(h -> h.value().testBE(this))
                        .map(RecipeHolder::value)
                        .max(Comparator.comparingInt(FermentationRecipe::getPriority))
                        .orElse(null);
                }
            }

            this.availableRecipe = match;

            if (match == null)
            {
                acceptRecipe(null);
                return;
            }

            List<Component> issues = validateRecipe(match);
            if (!issues.isEmpty())
            {
                this.unavailabilityMessages = issues;
                this.currentRecipe = match;
                pauseFermentation();
                return;
            }

            acceptRecipe(match);
        }
        finally
        {
            this.refreshing = false;
        }
    }

    private List<Component> validateRecipe(FermentationRecipe recipe)
    {
        List<Component> issues = new ArrayList<>();
        int p = this.parallel;

        // 1) size check
        if (!recipe.getNeedSize().isEmpty() && !recipe.getNeedSize().contains(this.getSizeId()))
        {
            issues.add(Component.translatable("tooltip.ashihara.fermentation.wrong_size"));
        }

        // 2) ventilation check (skip when needAir is null)
        Boolean needAir = recipe.getNeedAir();
        if (needAir != null)
        {
            boolean air = hasAir();
            if (needAir && !air)
            {
                issues.add(Component.translatable("tooltip.ashihara.fermentation.need_air"));
            }
            else if (!needAir && air)
            {
                issues.add(Component.translatable("tooltip.ashihara.fermentation.need_sealed"));
            }
        }

        // 3) fluid input check
        if (!recipe.getInputFluids().isEmpty())
        {
            int consume = recipe.getTotalConsumeAmount();
            int absoluteMin = consume * p;
            Integer minFluid = recipe.getMinFluidAmount();
            if (minFluid != null) minFluid *= p;
            Integer maxFluid = recipe.getMaxFluidAmount();
            if (maxFluid != null) maxFluid *= p;
            int effectiveMin = Math.max(absoluteMin, minFluid != null ? minFluid : 0);

            FluidStack stored = this.fluid.getFluidStack();
            boolean typeMatch = !this.fluid.isEmpty();
            if (typeMatch)
            {
                for (var fst : recipe.getInputFluids())
                {
                    if (!stored.is(fst.create().getFluid())) { typeMatch = false; break; }
                }
            }
            if (!typeMatch)
            {
                FluidStack required = recipe.getInputFluids().get(0).create();
                issues.add(Component.translatable("tooltip.ashihara.recipe.need_fluid", required.getHoverName(), effectiveMin));
            }
            else
            {
                int actual = stored.getAmount();
                if (actual < effectiveMin)
                {
                    issues.add(Component.translatable("tooltip.ashihara.recipe.need_fluid", stored.getHoverName(), effectiveMin));
                }
                else if (maxFluid != null && actual > maxFluid)
                {
                    issues.add(Component.translatable("tooltip.ashihara.recipe.fluid_excess", maxFluid));
                }
            }
        }

        // 4) item output space check
        List<ItemStack> outputs = recipe.getOutputStacks();
        if (!outputs.isEmpty())
        {
            int totalConsumed = 0;
            for (var si : recipe.getInputItems()) totalConsumed += si.count() * p;
            int totalProduced = 0;
            for (ItemStack out : outputs) totalProduced += out.getCount() * p;
            int freeCapacity = 0;
            for (int i = 0; i < this.inventory.size(); i++)
            {
                ItemStack s = this.inventory.getStackInSlot(i);
                freeCapacity += s.isEmpty() ? 64 : s.getMaxStackSize() - s.getCount();
            }
            if (totalProduced > freeCapacity + totalConsumed)
            {
                issues.add(Component.translatable("tooltip.ashihara.recipe.output_full"));
            }
        }

        // 5) fluid production space check (clear-then-fill)
        FluidStack outFluid = recipe.getOutputFluid();
        if (!outFluid.isEmpty() && !recipe.getInputFluids().isEmpty())
        {
            int actual = this.fluid.getFluidAmount();
            int production = recipe.computeFluidProduction(actual, p);
            if (production > this.fluid.getCapacity())
            {
                issues.add(Component.translatable("tooltip.ashihara.recipe.fluid_output_full", production - this.fluid.getCapacity()));
            }
        }

        return issues;
    }

    public ItemStack getAvailableOutput()
    {
        if (this.availableRecipe != null && this.availableRecipe.getOutputStacks() != null && !this.availableRecipe.getOutputStacks().isEmpty())
        {
            return this.availableRecipe.getOutputStacks().getFirst();
        }
        return ItemStack.EMPTY;
    }

    public FluidStack getAvailableFluidOutput()
    {
        if (this.availableRecipe != null)
        {
            return this.availableRecipe.getOutputFluid();
        }
        return FluidStack.EMPTY;
    }

    public List<Component> getProductionTooltip()
    {
        if (this.availableRecipe == null) return List.of();
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("tooltip.ashihara.pot_production"));
        ItemStack outputItem = this.getAvailableOutput();
        FluidStack outputFluid = this.getAvailableFluidOutput();
        if (!outputItem.isEmpty()) tooltip.add(Component.translatable("tooltip.ashihara.item_production").append(outputItem.getDisplayName()).append(" * ").append(String.valueOf(outputItem.getCount() * this.parallel)));
        if (!outputFluid.isEmpty()) tooltip.add(Component.translatable("tooltip.ashihara.fluid_production").append(outputFluid.getHoverName()).append(" * ").append(String.valueOf(outputFluid.getAmount() * this.parallel)).append(" mB."));
        return tooltip;
    }

    public static Component stylizeTime(int seconds)
    {
        MutableComponent component = Component.empty();
        int mod = seconds;
        if (mod >= 3600)
        {
            component.append(Component.translatable("tooltip.ashihara.hours", mod / 3600));
            mod = mod % 3600;
        }
        if (mod >= 60)
        {
            component.append(Component.translatable("tooltip.ashihara.minutes", mod / 60));
            mod = mod % 60;
        }
        if (mod > 0)
        {
            component.append(Component.translatable("tooltip.ashihara.seconds", mod));
        }
        return component;
    }

    private void acceptRecipe(@Nullable FermentationRecipe recipe)
    {
        if (this.currentRecipe != null) this.lastRecipe = this.currentRecipe.getId();
        boolean isSameRecipe = recipe == this.currentRecipe;
        this.currentRecipe = recipe;
        this.unavailabilityMessages = null;
        if (recipe != null)
        {
            this.availableRecipe = recipe;
            if (!isSameRecipe) startFermentation(recipe);
            else resumeFermentation();
        }
        else
        {
            stopFermentation();
        }
    }

    // -- Fermentation lifecycle ----------------------------------------------

    private void startFermentation(FermentationRecipe recipe)
    {
        this.isFermenting   = true;
        this.maxFermentTime = recipe.getTime();
        this.fermentTime    = 0;
        this.setChanged();
        this.sync();
    }

    private void pauseFermentation()
    {
        this.isFermenting = false;
        this.setChanged();
        this.sync();
    }

    private void resumeFermentation()
    {
        if (this.currentRecipe == null) return;
        this.isFermenting   = true;
        this.maxFermentTime = this.currentRecipe.getTime();
        this.setChanged();
        this.sync();
    }

    private void stopFermentation()
    {
        this.isFermenting   = false;
        this.fermentTime    = 0;
        this.maxFermentTime = 0;
        this.currentRecipe  = null;
        this.setChanged();
        this.sync();
    }

    private void finishRecipe()
    {
        FermentationRecipe recipe = this.currentRecipe;
        if (recipe == null)
        {
            stopFermentation();
            return;
        }
        int p = this.parallel;

        // consume input items
        if (!recipe.getInputItems().isEmpty())
        {
            this.inventory.testIngredients(recipe.getInputItems(), p, false);
        }

        // fluid handling
        FluidStack outFluid = recipe.getOutputFluid();
        if (!outFluid.isEmpty() && !recipe.getInputFluids().isEmpty())
        {
            // input + output: clear tank, fill with bonus-adjusted production
            int actual = this.fluid.getFluidAmount();
            int production = recipe.computeFluidProduction(actual, p);
            try (Transaction tx = Transaction.openRoot())
            {
                this.fluid.extract(this.fluid.getResource(0), actual, tx);
                tx.commit();
            }
            try (Transaction tx = Transaction.openRoot())
            {
                this.fluid.insert(FluidResource.of(outFluid), production, tx);
                tx.commit();
            }
        }
        else if (!recipe.getInputFluids().isEmpty())
        {
            // input only: consume by exact amount
            recipe.consumeFluidInputs(this.fluid, p);
        }
        else if (!outFluid.isEmpty())
        {
            // output only: insert directly
            int amount = outFluid.getAmount() * p;
            try (Transaction tx = Transaction.openRoot())
            {
                this.fluid.insert(FluidResource.of(outFluid), amount, tx);
                tx.commit();
            }
        }

        // produce output items (overflow pops to world)
        for (ItemStack out : recipe.getOutputStacks())
        {
            ItemStack toInsert = out.copyWithCount(out.getCount() * p);
            ItemStack remainder = this.inventory.insert(toInsert, false);
            if (!remainder.isEmpty() && this.level != null)
            {
                Block.popResource(this.level, this.worldPosition, remainder);
            }
        }

        if (this.level != null) this.level.playSound(null, this.getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1F, 1F);

        acceptRecipe(null);
        refreshRecipe();
    }

    // -- Tick ----------------------------------------------------------------

    public static void serverTick(Level level, BlockPos pos, BlockState state, FermentationBlockEntity be)
    {
        if (!be.isFermenting) return;
        be.fermentTime++;
        if (be.fermentTime >= be.maxFermentTime)
        {
            be.fermentTime = be.maxFermentTime;
            if (!level.isClientSide()) be.finishRecipe();
        }
    }

    // -- Multi-block removal -------------------------------------------------

    // -- Persistence ---------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putChild("inventory", this.inventory);
        output.putChild("fluid", this.fluid);
        output.putString("currentRecipe", this.currentRecipe == null ? "" : this.currentRecipe.getId().toString());
        output.putString("lastRecipe", this.lastRecipe == null ? "" : this.lastRecipe.toString());
        output.putInt("fermentTime", this.fermentTime);
        output.putInt("maxFermentTime", this.maxFermentTime);
        output.putInt("parallel", this.parallel);
        output.putBoolean("isFermenting", this.isFermenting);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        input.readChild("inventory", this.inventory);
        input.readChild("fluid", this.fluid);
        this.prevFluidLevel = this.fluidLevel;
        this.fluidLevel = (float) this.fluid.getFluidAmount() / (float) this.fluid.getCapacity();
        this.fluidLevelChanged = true;
        this.fermentTime    = input.getIntOr("fermentTime", 0);
        this.maxFermentTime = input.getIntOr("maxFermentTime", 0);
        this.parallel       = input.getIntOr("parallel", 1);
        this.isFermenting   = input.getBooleanOr("isFermenting", false);

        input.getString("lastRecipe").ifPresent(id ->
        {
            if (!id.isEmpty()) this.lastRecipe = Identifier.parse(id);
        });

        input.getString("currentRecipe").ifPresent(id ->
        {
            this.pendingRecipeId = id.isEmpty() ? null : id;
        });
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.pendingRecipeId != null && this.level instanceof ServerLevel serverLevel)
        {
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(this.pendingRecipeId));
            serverLevel.recipeAccess().byKey(key).ifPresent(holder -> this.currentRecipe = (FermentationRecipe) holder.value());
            this.pendingRecipeId = null;
        }
        refreshRecipe();
    }

    // -- Utilities -----------------------------------------------------------

    public void dropContents(Level level, BlockPos pos)
    {
        for (int i = 0; i < this.inventory.size(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) Block.popResource(level, pos, stack);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        if (this.level != null && !this.level.isClientSide())
        {
            dropContents(this.level, this.getBlockPos());
            if (this.size == Size.LARGE_VAT) LargeFermentationVatBlock.removeAllSubBlocks(this.level, this.worldPosition, this.getBlockState());
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        float t = (float) this.fluid.getFluidAmount() / (float) this.fluid.getCapacity();
        if (this.fluidLevel != t)
        {
            this.prevFluidLevel = this.fluidLevel;
            this.fluidLevel = t;
            this.fluidLevelChanged = true;
            if (this.level != null && !this.level.isClientSide()) this.sync();
        }
        if (this.level != null && !this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    // -- Rendering model -----------------------------------------------------

    public FermentationDisplayModel getModel()
    {
        if (this.level == null || !this.level.isClientSide()) return null;
        if (this.model == null)
        {
            this.model = new FermentationDisplayModel("assistance/fermentation_block_display", "textures/geo/empty.png", "block/fermentation_block_display");
            int maxSlot = switch (this.size)
            {
                case VAT -> 8;
                case LARGE_VAT -> 16;
                default -> 4;
            };
            for (int i = 0; i < maxSlot; i++)
            {
                String id = "item_display_" + this.getSizePrefix() + i;
                createTracer(id);
            }
            createTracer("fluid_display_" + this.getSizeId());
            createTracer("item_display_" + this.getSizeId());
        }
        return this.model;
    }

    private void createTracer(String name)
    {
        BoneTracer tracer = new BoneTracer(b -> b.name().equals(name));
        boneTracers.put(name, tracer);
        this.model.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
    }
}
