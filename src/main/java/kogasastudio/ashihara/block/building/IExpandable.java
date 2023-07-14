package kogasastudio.ashihara.block.building;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;

public interface IExpandable
{
    default void expand(Axis axis, LR lr)
    {
        Direction direction;
        switch (lr)
        {
            case LEFT ->
            {
                if (axis.equals(Axis.XP)) direction = Direction.NORTH;
                else if (axis.equals(Axis.ZP)) direction = Direction.EAST;
                else direction = Direction.DOWN;
            }
            case RIGHT ->
            {
                if (axis.equals(Axis.XP)) direction = Direction.SOUTH;
                else if (axis.equals(Axis.ZP)) direction = Direction.WEST;
                else direction = Direction.UP;
            }
            default -> throw new IllegalStateException("Given direction doesn't match anything existed!");
        }
        this.expand(direction);
    }

    void expand(Direction direction);

    /**
     *
     */
    enum LR
    {
        LEFT,
        RIGHT
    }
}
