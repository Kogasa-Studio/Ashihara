package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;

/**
 * Marker interface for block entities that expose a {@link BEFluidStackHandler}.
 * Used by items (e.g. MinatoAqua) that interact with fluid storage generically.
 */
public interface IFluidHandler
{
    BEFluidStackHandler<?> getTank();
}
