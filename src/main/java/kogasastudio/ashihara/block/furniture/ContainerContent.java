package kogasastudio.ashihara.block.furniture;

import net.neoforged.neoforge.transfer.StacksResourceHandler;
import org.jspecify.annotations.Nullable;

/**
 * Extrinsic state for {@link ContainerComponent} flyweights.
 * Stores the content type alongside the mutable handler.
 * <p>
 * Stored in {@link kogasastudio.ashihara.block.building.component.ComponentStateDefinition#customData()}.
 */
public record ContainerContent(
    ContainerState.ContentType type,
    @Nullable StacksResourceHandler<?, ?> handler
)
{
    public static final ContainerContent EMPTY = new ContainerContent(ContainerState.ContentType.EMPTY, null);

    /** True if the handler is null or all its slots are empty. */
    public boolean isEmpty()
    {
        if (handler == null) return true;
        for (int i = 0; i < handler.size(); i++)
            if (!handler.getResource(i).isEmpty()) return false;
        return true;
    }
}