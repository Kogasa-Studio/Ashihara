package kogasastudio.ashihara.helper;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class ShapeHelper
{
    public static Map<Direction, VoxelShape> genFourWayShapes(Direction dir, double xS, double yS, double zS, double xE, double yE, double zE)
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();

        shapes.put(dir, rotateBy90(dir, xS, yS, zS, xE, yE, zE));
        shapes.put(dir.rotateY(), rotateBy90(dir.rotateY(), xS, yS, zS, xE, yE, zE));
        shapes.put(dir.getOpposite(), rotateBy90(dir.getOpposite(), xS, yS, zS, xE, yE, zE));
        shapes.put(dir.getOpposite().rotateY(), rotateBy90(dir.getOpposite().rotateY(), xS, yS, zS, xE, yE, zE));

        return shapes;
    }

    private static VoxelShape rotateBy90(Direction dir, double xS, double yS, double zS, double xE, double yE, double zE)
    {
        switch (dir.getAxis())
        {
            case Z: return Block.makeCuboidShape(16d - zE, yS, xS, 16d - zS, yE, xE);
            case X: return Block.makeCuboidShape(zS, yS, xS, zE, yE, xE);
            default:return Block.makeCuboidShape(xS, yS, zS, xE, yE, zE);
        }
    }
}
