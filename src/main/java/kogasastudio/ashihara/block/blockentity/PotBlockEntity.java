package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.inventory.container.PotMenu;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Block entity for the clay pot cooking block.
 *
 * <p>Holds 4 ingredient slots (matching {@code item_slot_0..3} geo bones)
 * and a fluid tank for water / broth.
 *
 * <p>Capabilities are registered in {@link kogasastudio.ashihara.registry.Capabilities}.
 */
public class PotBlockEntity extends AshiharaMachineBE implements MenuProvider
{
    // ── Constants ─────────────────────────────────────────────────────────────
    /** Number of ingredient slots (matches the geo bone count). */
    public static final int INVENTORY_SIZE = 4;
    /** Fluid tank capacity in mB. */
    public static final int FLUID_CAPACITY = 4000;

    // ── Storage ───────────────────────────────────────────────────────────────
    /** Ingredient inventory, hooked up to NeoForge capability. */
    public final BEItemStackHandler<PotBlockEntity> inventory =
            new BEItemStackHandler<>(INVENTORY_SIZE, this);

    /**
     * Liquid storage (water, dashi, etc.).
     * Not {@code final} because {@link FluidTank#readFromNBT} returns a new instance.
     */
    public FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);

    // ── Cooking state ─────────────────────────────────────────────────────────
    private boolean isCooking  = false;
    private int     cookTime   = 0;
    private int     maxCookTime = 0;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PotBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.POT_BE.get(), pos, state);
    }

    // ── Capability providers ──────────────────────────────────────────────────
    public static IItemHandler  getItemHandler (PotBlockEntity be, Direction side) { return be.inventory; }
    public static IFluidHandler getFluidHandler(PotBlockEntity be, Direction side) { return be.fluidTank; }

    // ── MenuProvider ─────────────────────────────────────────────────────────

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.ashihara.pot");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        // 服务端侧：将实际字段绑定为 ContainerData，使 menu 能向客户端同步烹饪进度
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
    public boolean isCooking()      { return isCooking; }
    public int     getCookTime()    { return cookTime; }
    public int     getMaxCookTime() { return maxCookTime; }
    public float   getCookProgress()
    {
        return maxCookTime > 0 ? (float) cookTime / maxCookTime : 0f;
    }

    // ── Cooking control ───────────────────────────────────────────────────────
    /** Server-side: start cooking and sync state to clients. */
    public void startCooking(int totalCookTime)
    {
        this.isCooking    = true;
        this.maxCookTime  = totalCookTime;
        this.cookTime     = 0;
        this.setChanged();
        this.sync();
    }

    /** Server-side: stop/reset cooking and sync state to clients. */
    public void stopCooking()
    {
        this.isCooking    = false;
        this.cookTime     = 0;
        this.maxCookTime  = 0;
        this.setChanged();
        this.sync();
    }

    // ── Tick (server-side only) ───────────────────────────────────────────────
    public static void serverTick(Level level, BlockPos pos, BlockState state, PotBlockEntity be)
    {
        if (!be.isCooking) return;
        be.cookTime++;
        if (be.cookTime >= be.maxCookTime)
        {
            be.stopCooking();
        }
        be.setChanged();
    }

    // ── NBT ───────────────────────────────────────────────────────────────────
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("inventory",   this.inventory.serializeNBT(registries));
        tag.put("fluid",       this.fluidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt    ("cookTime",    this.cookTime);
        tag.putInt    ("maxCookTime", this.maxCookTime);
        tag.putBoolean("isCooking",   this.isCooking);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("fluid"))     this.fluidTank = this.fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        this.cookTime    = tag.getInt    ("cookTime");
        this.maxCookTime = tag.getInt    ("maxCookTime");
        this.isCooking   = tag.getBoolean("isCooking");
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    /** Drop all ingredient items into the world (called from PotBlock#onRemove). */
    public void dropContents(Level level, BlockPos pos)
    {
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                net.minecraft.world.level.block.Block.popResource(level, pos, stack);
            }
        }
    }
}
