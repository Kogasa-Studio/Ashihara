package kogasastudio.ashihara.block.furniture;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockBox;

/**
 * Marks a FurnitureComponent that occupies multiple blocks.
 * The component's model extends beyond one block; on placement,
 * proxy components are created in adjacent blocks to handle
 * interaction and destruction correctly.
 */
public interface MultiBlockFurniture
{
    /**
     * @param facing  placement direction
     * @return bounding box relative to the placement-origin block,
     *         in block coordinates (inclusive on both ends)
     */
    BlockBox getExtent(Direction facing);
}
