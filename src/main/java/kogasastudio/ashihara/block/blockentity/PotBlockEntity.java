package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.client.gui3d.PotScreen;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.PotModel;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.interaction.recipes.PotRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.inventory.container.PotMenu;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PotBlockEntity extends AshiharaMachineBE implements MenuProvider
{
    public static final int INVENTORY_SIZE  = 4;
    public static final int OUTPUT_SIZE     = 1;
    public static final int FLUID_CAPACITY  = 10000;

    // ── Storage ───────────────────────────────────────────────────────────────

    public final BEItemStackHandler<PotBlockEntity> inventory;
    public final BEItemStackHandler<PotBlockEntity> output;
    public final BEFluidStackHandler<PotBlockEntity> fluidTank;

    // ── Cooking state ─────────────────────────────────────────────────────────

    private boolean isCooking;
    private int     cookTime;
    private int     maxCookTime;
    public  int     parallel = 1;
    @Nullable
    public  PotRecipe currentRecipe;
    @Nullable
    private Identifier lastRecipe;
    @Nullable
    private PotRecipe availableRecipe;
    @Nullable
    private List<Component> unavailabilityMessages;
    private boolean refreshing;

    // ── Misc ──────────────────────────────────────────────────────────────────

    public float prevFluidLevel = 0f;
    public float fluidLevel = 0f;
    public boolean fluidLevelChanged = false;
    public boolean inited = false;
    private PotModel potModel;
    public Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public PotBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.POT_BE.get(), pos, state);
        this.inventory = new BEItemStackHandler<>(INVENTORY_SIZE, this, () -> { if (!isCooking) refreshRecipe(); });
        this.output    = new BEItemStackHandler<>(OUTPUT_SIZE,    this, () -> { if (!isCooking) refreshRecipe(); });
        this.fluidTank = new BEFluidStackHandler<>(FLUID_CAPACITY, this, () -> { if (!isCooking) refreshRecipe(); });
    }

    // ── Capability providers ──────────────────────────────────────────────────

    public static ResourceHandler<ItemResource>  getItemHandler (PotBlockEntity be, Direction side)
    {
        return side == Direction.UP ? be.inventory : be.output;
    }
    public static ResourceHandler<FluidResource> getFluidHandler(PotBlockEntity be, Direction side)
    {
        return be.fluidTank;
    }

    // ── MenuProvider ─────────────────────────────────────────────────────────

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.ashihara.pot");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        ContainerData cookData = new ContainerData()
        {
            @Override public int get(int index)
            {
                return switch (index) { case 0 -> cookTime; case 1 -> maxCookTime; default -> 0; };
            }
            @Override public void set(int index, int value)
            {
                switch (index) { case 0 -> cookTime = value; case 1 -> maxCookTime = value; }
            }
            @Override public int getCount() { return 2; }
        };
        return new PotMenu(containerId, inventory, this, cookData);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public boolean isCooking()           { return isCooking; }
    public int     getCookTime()         { return cookTime; }
    public int     getMaxCookTime()      { return maxCookTime; }
    public float   getCookProgress()     { return maxCookTime > 0 ? (float) cookTime / maxCookTime : 0f; }
    public void    setParallel(int val)  { this.parallel = val; }
    public int     getMaxParallel()      { return 64; }
    public @Nullable PotRecipe getAvailableRecipe() { return availableRecipe; }

    public PotModel getPotModel()
    {
        if (this.level == null || !this.level.isClientSide()) return null;
        if (this.potModel == null)
        {
            this.potModel = new PotModel("block/pot", /*"textures/block/pot.png"*/"textures/geo/empty.png", "gui/pot");
            for (int i = 0; i < 5; i++)
            {
                String id = "item_display_" + i;
                createTracer(id);
            }
            createTracer("fluid_display");
            createTracer("item_display");
        }
        return this.potModel;
    }

    public List<Component> getUnavailabilityMessages()
    {
        return this.unavailabilityMessages == null ? List.of() : this.unavailabilityMessages;
    }

    // ── Recipe detection (lazy) ───────────────────────────────────────────────

    public void refreshRecipe()
    {
        if (this.refreshing || this.level == null) return;
        this.refreshing = true;
        try
        {
            Collection<RecipeHolder<PotRecipe>> recipes = RecipeHelper.getRecipesByType(this.level, RecipeTypes.POT.get());
            PotRecipe match = null;

            // 1) Sticky: prefer last-known recipe
            if (this.lastRecipe != null)
            {
                match = recipes.stream()
                    .filter(h -> h.value().getId().equals(this.lastRecipe) && h.value().testBE(this))
                    .map(RecipeHolder::value)
                    .findFirst().orElse(null);
            }
            // 2) Fallback: any matching recipe
            if (match == null)
            {
                match = recipes.stream()
                    .filter(h -> h.value().testBE(this))
                    .map(RecipeHolder::value)
                    .findFirst().orElse(null);
            }

            this.availableRecipe = match;
            setScreenChanged();
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
                return;
            }

            acceptRecipe(match);
        }
        finally
        {
            this.refreshing = false;
        }
    }

    private List<Component> validateRecipe(PotRecipe recipe)
    {
        List<Component> issues = new ArrayList<>();
        int p = this.parallel;

        // 1) Output slot capacity
        ItemStack outStack = recipe.getOutput().copyWithCount(recipe.getOutput().getCount() * p);
        if (!outStack.isEmpty())
        {
            ItemStack remainder = this.output.insert(outStack, true);
            if (!remainder.isEmpty())
            {
                issues.add(Component.translatable("tooltip.ashihara.pot.output_full"));
            }
        }

        // 2) Input fluid type + amount
        if (!recipe.getFluidCost().isEmpty())
        {
            FluidStack tankFluid = this.fluidTank.getFluidStack();
            FluidStack required  = recipe.getFluidCost().copyWithAmount(recipe.getFluidCost().getAmount() * p);
            if (!tankFluid.is(required.getFluid()) || tankFluid.getAmount() < required.getAmount())
            {
                issues.add(Component.translatable("tooltip.ashihara.pot.need_fluid",
                    required.getAmount(), required.getHoverName()));
            }
        }

        // 3) Output fluid space
        if (!recipe.getFluidProduction().isEmpty())
        {
            int produced = recipe.getFluidProduction().getAmount() * p;
            int space    = this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount();
            if (!this.fluidTank.isEmpty()
                && !this.fluidTank.getFluidStack().is(recipe.getFluidProduction().getFluid()))
            {
                issues.add(Component.translatable("tooltip.ashihara.pot.fluid_type_mismatch"));
            }
            else if (produced > space)
            {
                issues.add(Component.translatable("tooltip.ashihara.pot.fluid_output_full"));
            }
        }

        return issues;
    }

    private void acceptRecipe(@Nullable PotRecipe recipe)
    {
        if (this.currentRecipe != null) this.lastRecipe = this.currentRecipe.getId();
        this.currentRecipe = recipe;
        this.unavailabilityMessages = null;
        if (recipe != null)
        {
            this.availableRecipe = recipe;
            startCooking(recipe);
        }
        else
        {
            stopCooking();
        }
    }

    public ItemStack getAvailableOutput()
    {
        if (this.availableRecipe != null)
        {
            return this.availableRecipe.getOutput();
        }
        return ItemStack.EMPTY;
    }

    // ── Cooking lifecycle ─────────────────────────────────────────────────────

    private void startCooking(PotRecipe recipe)
    {
        this.isCooking   = true;
        this.maxCookTime = recipe.getCookTime();
        this.cookTime    = 0;
        this.setChanged();
        this.sync();
    }

    private void stopCooking()
    {
        this.isCooking     = false;
        this.cookTime      = 0;
        this.maxCookTime   = 0;
        this.currentRecipe = null;
        this.setChanged();
        this.sync();
    }

    private void finishRecipe()
    {
        PotRecipe recipe = this.currentRecipe;
        if (recipe == null) return;
        int p = this.parallel;

        // Consume input items
        this.inventory.testIngredients(recipe.getInput(), p, false);

        // Produce output item
        ItemStack out = recipe.getOutput().copyWithCount(recipe.getOutput().getCount() * p);
        if (!out.isEmpty())
        {
            List<ItemStack> leftovers = this.output.insert(List.of(out), false);
            for (ItemStack leftover : leftovers)
            {
                if (!leftover.isEmpty() && this.level != null)
                {
                    Block.popResource(this.level, this.worldPosition, leftover);
                }
            }
        }

        // Consume input fluid
        if (!recipe.getFluidCost().isEmpty())
        {
            FluidStack cost = recipe.getFluidCost().copyWithAmount(recipe.getFluidCost().getAmount() * p);
            try (Transaction tx = Transaction.openRoot())
            {
                this.fluidTank.extract(FluidResource.of(cost), cost.getAmount(), tx);
                tx.commit();
            }
        }

        // Produce output fluid
        if (!recipe.getFluidProduction().isEmpty())
        {
            FluidStack prod = recipe.getFluidProduction().copyWithAmount(recipe.getFluidProduction().getAmount() * p);
            try (Transaction tx = Transaction.openRoot())
            {
                this.fluidTank.insert(FluidResource.of(prod), prod.getAmount(), tx);
                tx.commit();
            }
        }

        // Reset and check for next recipe
        acceptRecipe(null);
        refreshRecipe();
    }

    // ── Tick (server-side only) ───────────────────────────────────────────────

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotBlockEntity be)
    {
        if (!be.isCooking) return;
        be.cookTime++;
        if (be.cookTime >= be.maxCookTime)
        {
            be.finishRecipe();
            return;
        }
        be.setChanged();
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putString("currentRecipe", this.currentRecipe == null ? "" : this.currentRecipe.getId().toString());
        output.putString("lastRecipe",    this.lastRecipe    == null ? "" : this.lastRecipe.toString());
        output.putChild ("inventory",     this.inventory);
        output.putChild ("output",        this.output);
        output.putChild ("fluid",         this.fluidTank);
        output.putInt   ("cookTime",      this.cookTime);
        output.putInt   ("maxCookTime",   this.maxCookTime);
        output.putInt   ("parallel",      this.parallel);
        output.putBoolean("isCooking",    this.isCooking);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        input.readChild("inventory", this.inventory);
        input.readChild("output",    this.output);
        input.readChild("fluid",     this.fluidTank);
        this.cookTime    = input.getIntOr   ("cookTime",    0);
        this.maxCookTime = input.getIntOr   ("maxCookTime", 0);
        this.parallel    = input.getIntOr   ("parallel",    1);
        this.isCooking   = input.getBooleanOr("isCooking",  false);

        input.getString("lastRecipe").ifPresent(id ->
        {
            if (!id.isEmpty()) this.lastRecipe = Identifier.parse(id);
        });

        if (this.level instanceof ServerLevel serverLevel)
        {
            input.getString("currentRecipe").ifPresent(id ->
            {
                if (!id.isEmpty())
                {
                    RecipeManager rm = serverLevel.recipeAccess();
                    ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(id));
                    rm.byKey(key).ifPresent(holder -> this.currentRecipe = (PotRecipe) holder.value());
                }
            });
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }

        refreshRecipe();
        setChanged();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    public void dropContents(Level level, BlockPos pos)
    {
        Block.popResource(level, pos, this.output.getStackInSlot(0));
        for (int i = 0; i < this.inventory.size(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                Block.popResource(level, pos, stack);
            }
        }
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        float t = (float) this.fluidTank.getFluidAmount() / (float) this.fluidTank.getCapacity();
        if (this.fluidLevel != t)
        {
            this.prevFluidLevel = this.fluidLevel;
            this.fluidLevel = t;
            this.fluidLevelChanged = true;
        }
        if (this.level != null && !this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    protected void setScreenChanged()
    {
        if (this.level != null && this.level.isClientSide())
        {
            if (Minecraft.getInstance().screen instanceof PotScreen pt)
            {
                pt.potScreen3D.setChanged();
            }
        }
    }

    private void createTracer(String name)
    {
        BoneTracer tracer = new BoneTracer(b -> b.name().equals(name));
        boneTracers.put(name, tracer);
        this.potModel.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
    }
}
