package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.inventory.container.PotMenu;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

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

    /** Liquid storage (water, dashi, etc.). */
    public final BEFluidStackHandler<PotBlockEntity> fluidTank =
            new BEFluidStackHandler<>(FLUID_CAPACITY, this);

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
    public static ResourceHandler<ItemResource>  getItemHandler (PotBlockEntity be, Direction side) { return be.inventory; }
    public static ResourceHandler<FluidResource> getFluidHandler(PotBlockEntity be, Direction side) { return be.fluidTank; }

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
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putChild("inventory",   this.inventory);
        output.putChild("fluid",       this.fluidTank);
        output.putInt    ("cookTime",    this.cookTime);
        output.putInt    ("maxCookTime", this.maxCookTime);
        output.putBoolean("isCooking",   this.isCooking);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        input.readChild("inventory", this.inventory);
        input.readChild("fluid",     this.fluidTank);
        this.cookTime    = input.getIntOr    ("cookTime",    0);
        this.maxCookTime = input.getIntOr    ("maxCookTime", 0);
        this.isCooking   = input.getBooleanOr("isCooking",   false);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    /** Drop all ingredient items into the world (called from PotBlock#onRemove). */
    public void dropContents(Level level, BlockPos pos)
    {
        for (int i = 0; i < this.inventory.size(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                net.minecraft.world.level.block.Block.popResource(level, pos, stack);
            }
        }
    }
}
