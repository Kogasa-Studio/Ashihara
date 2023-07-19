package kogasastudio.ashihara.block.building;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface IExpandable
{
    BooleanProperty L = BooleanProperty.create("l");
    BooleanProperty R = BooleanProperty.create("r");
    BooleanProperty U = BooleanProperty.create("u");
    BooleanProperty D = BooleanProperty.create("d");

    default Direction getDirectionByAxis(Direction.Axis axis, RelativeDirection lrud)
    {
        Direction direction;
        switch (lrud)
        {
            case LEFT ->
            {
                if (axis.equals(Direction.Axis.X)) direction = Direction.EAST;
                else if (axis.equals(Direction.Axis.Z)) direction = Direction.SOUTH;
                else direction = Direction.UP;
            }
            case RIGHT ->
            {
                if (axis.equals(Direction.Axis.X)) direction = Direction.WEST;
                else if (axis.equals(Direction.Axis.Z)) direction = Direction.NORTH;
                else direction = Direction.DOWN;
            }
            case UP -> direction = Direction.UP;
            case DOWN -> direction = Direction.DOWN;
            default -> throw new IllegalStateException("Given direction doesn't match anything existed!");
        }
        return direction;
    }

    default BlockState expand(RelativeDirection direction, BlockState stateIn, boolean flag)
    {
        if (direction.equals(RelativeDirection.LEFT)) stateIn = stateIn.setValue(L, flag);
        if (direction.equals(RelativeDirection.RIGHT)) stateIn = stateIn.setValue(R, flag);
        if (direction.equals(RelativeDirection.UP)) stateIn = stateIn.setValue(U, flag);
        if (direction.equals(RelativeDirection.DOWN)) stateIn = stateIn.setValue(D, flag);
        return stateIn;
    }

    enum RelativeDirection
    {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
