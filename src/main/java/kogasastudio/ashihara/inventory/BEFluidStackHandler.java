package kogasastudio.ashihara.inventory;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

/**
 * A {@link FluidStacksResourceHandler} tied to a {@link BlockEntity}.
 * Automatically calls {@code setChanged()} and {@code sendBlockUpdated()} on content change.
 *
 * <p>Convenience accessors {@link #getFluidStack()}, {@link #getFluidAmount()},
 * {@link #getCapacity()} and {@link #isEmpty()} are provided for common read operations
 * without needing to unwrap the {@link FluidResource} manually.
 */
public class BEFluidStackHandler<B extends BlockEntity> extends FluidStacksResourceHandler
{
    public final B be;
    private final int capacityMb;

    /**
     * Creates a single-slot fluid tank with the given capacity, backed by the supplied BE.
     *
     * @param capacityMb tank capacity in millibuckets
     * @param be         the owning block entity
     */
    public BEFluidStackHandler(int capacityMb, B be)
    {
        super(1, capacityMb);
        this.capacityMb = capacityMb;
        this.be = be;
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents)
    {
        be.setChanged();
        if (be.getLevel() != null)
        {
            be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), UPDATE_ALL);
        }
    }

    // ── Convenience accessors ─────────────────────────────────────────────────

    /** Returns the current stored {@link FluidStack} (index 0). Never null; may be empty. */
    public FluidStack getFluidStack()
    {
        return FluidUtil.getStack(this, 0);
    }

    /** Returns the current stored amount in millibuckets. */
    public int getFluidAmount()
    {
        return getAmountAsInt(0);
    }

    /** Returns the maximum capacity of this tank in millibuckets. */
    public int getCapacity()
    {
        return capacityMb;
    }

    /** Returns {@code true} if the tank holds no fluid. */
    public boolean isEmpty()
    {
        return getResource(0).isEmpty();
    }
}

