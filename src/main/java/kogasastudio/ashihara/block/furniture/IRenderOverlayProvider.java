package kogasastudio.ashihara.block.furniture;

import org.jspecify.annotations.Nullable;

/**
 * Implemented by container FurnitureComponents that can provide
 * a content overlay snapshot for the MealRenderDispatch (MRD) system.
 */
public interface IRenderOverlayProvider
{
    @Nullable
    ContainerState getContainerState();
}