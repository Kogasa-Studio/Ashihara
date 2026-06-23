package kogasastudio.ashihara.block.furniture;

import org.jspecify.annotations.Nullable;

/**
 * Render-time snapshot of a container's contents.
 * Built by ContainerComponent.getContainerState() and passed to
 * MealRenderDispatch for overlay rendering decisions.
 */
public record ContainerState(
    ContainerType containerType,
    ContainerSize size,
    ContentType contentType,
    int count,
    @Nullable Object contentData
)
{
    public static final ContainerState EMPTY = new ContainerState(ContainerType.BOWL, ContainerSize.MID, ContentType.EMPTY, 0, null);

    /** Size categories for container rendering. */
    public enum ContainerSize
    {
        SMALL("small"), MID("mid"), LARGE("large"),
        ;
        public final String id;
        ContainerSize(String id) { this.id = id; }
    }

    /** Enum of container form factors. */
    public enum ContainerType
    {
        BOWL, PLATE, CUP,
        ;

        public String contextKey(ContainerSize size) { return name().toLowerCase() + "_" + size.id; }
    }

    /** Content type enum for ContainerState. */
    public enum ContentType
    {
        EMPTY,
        ITEM,
        FLUID,
        MEAL,
        ;
    }
}
