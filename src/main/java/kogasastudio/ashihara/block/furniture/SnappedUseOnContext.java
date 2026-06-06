package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Wraps a UseOnContext, overriding {@link #getClickLocation()} to return
 * a grid-snapped position.
 * <p>
 * getHitResult() is protected in UseOnContext, so we reconstruct the
 * BlockHitResult from public API and store the snapped location separately.
 */
public class SnappedUseOnContext extends BlockPlaceContext
{
    private final Vec3 snappedLocation;
    private final boolean simulate;

    public SnappedUseOnContext(UseOnContext original, int gridStep, boolean simulate)
    {
        super(
            original.getLevel(),
            original.getPlayer(),
            original.getHand(),
            original.getItemInHand().copy(),
            new BlockHitResult(
                original.getClickLocation(),
                original.getClickedFace(),
                original.getClickedPos(),
                original.isInside()
            )
        );
        this.snappedLocation = GridSnapHelper.snapClickLocation(original.getClickLocation(), original.getClickedPos(), gridStep);
        this.simulate = simulate;
    }

    @Override
    public Vec3 getClickLocation()
    {
        return this.snappedLocation;
    }

    public boolean simulate()
    {
        return this.simulate;
    }
}