package kogasastudio.ashihara.utils.shape;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.*;

import java.util.BitSet;
import java.util.Optional;

public class VoxelShapeSerializer {

    // <editor-fold desc="Save.">

    /**
     * Serializes a {@link VoxelShape} into the given {@link ValueOutput}.
     *
     * @return {@code true} if the shape was successfully written; {@code false} if the shape type is unsupported.
     */
    public static boolean saveShape(VoxelShape shape, ValueOutput output) {
        var type = VoxelShapeType.from(shape.getClass());
        if (type == null) {
            return false;
        }

        output.putString("type", type.getName());

        if (!saveDiscrete(output.child("discrete"), shape.shape)) {
            return false;
        }

        switch (type) {
            case ARRAY_VOXEL_SHAPE -> saveArrayShape(output, (ArrayVoxelShape) shape);
            case SLICE_SHAPE -> saveSliceShape(output, (SliceShape) shape);
            case CUBE_VOXEL_SHAPE -> { /* nothing extra */ }
        }
        return true;
    }

    private static void saveArrayShape(ValueOutput output, ArrayVoxelShape shape) {
        var xList = output.list("xList", Codec.DOUBLE);
        shape.xs.forEach(xList::add);
        var yList = output.list("yList", Codec.DOUBLE);
        shape.ys.forEach(yList::add);
        var zList = output.list("zList", Codec.DOUBLE);
        shape.zs.forEach(zList::add);
    }

    private static void saveSliceShape(ValueOutput output, SliceShape shape) {
        boolean ok = saveShape(shape.delegate, output.child("delegate"));
        if (!ok) {
            output.discard("delegate");
            return;
        }
        output.putString("axis", shape.axis.getName());
    }

    private static boolean saveDiscrete(ValueOutput output, DiscreteVoxelShape shape) {
        var type = DiscreteShapeType.from(shape.getClass());
        if (type == null) {
            return false;
        }

        output.putString("type", type.getName());
        output.putInt("xSize", shape.xSize);
        output.putInt("ySize", shape.ySize);
        output.putInt("zSize", shape.zSize);

        switch (type) {
            case BIT_SET -> saveBitSetDiscrete(output, (BitSetDiscreteVoxelShape) shape);
            case SUB -> saveSubDiscrete(output, (SubShape) shape);
        }
        return true;
    }

    private static void saveBitSetDiscrete(ValueOutput output, BitSetDiscreteVoxelShape shape) {
        var storageList = output.list("storage", Codec.LONG);
        for (long l : shape.storage.toLongArray()) {
            storageList.add(l);
        }
        output.putInt("xMin", shape.xMin);
        output.putInt("yMin", shape.yMin);
        output.putInt("zMin", shape.zMin);
        output.putInt("xMax", shape.xMax);
        output.putInt("yMax", shape.yMax);
        output.putInt("zMax", shape.zMax);
    }

    private static void saveSubDiscrete(ValueOutput output, SubShape shape) {
        boolean ok = saveDiscrete(output.child("parent"), shape.parent);
        if (!ok) {
            output.discard("parent");
            return;
        }
        output.putInt("startX", shape.startX);
        output.putInt("startY", shape.startY);
        output.putInt("startZ", shape.startZ);
        output.putInt("endX", shape.endX);
        output.putInt("endY", shape.endY);
        output.putInt("endZ", shape.endZ);
    }

    // </editor-fold>

    // <editor-fold desc="Load.">

    /**
     * Deserializes a {@link VoxelShape} from the given {@link ValueInput}.
     *
     * @return the loaded shape, or {@code null} if the data is absent or unrecognised.
     */
    public static VoxelShape loadShape(ValueInput input) {
        Optional<String> typeStr = input.getString("type");
        if (typeStr.isEmpty()) {
            return null;
        }
        var type = VoxelShapeType.from(typeStr.get());
        if (type == null) {
            return null;
        }

        var discrete = loadDiscrete(input.childOrEmpty("discrete"));
        if (discrete == null) {
            return null;
        }

        return switch (type) {
            case ARRAY_VOXEL_SHAPE -> loadArrayShape(input, discrete);
            case SLICE_SHAPE       -> loadSliceShape(input, discrete);
            case CUBE_VOXEL_SHAPE  -> loadCubeShape(discrete);
        };
    }

    private static VoxelShape loadArrayShape(ValueInput input, DiscreteVoxelShape discrete) {
        Optional<ValueInput.TypedInputList<Double>> xListOpt = input.list("xList", Codec.DOUBLE);
        if (xListOpt.isEmpty()) return null;
        var xs = new DoubleArrayList(xListOpt.get().stream().toList());

        Optional<ValueInput.TypedInputList<Double>> yListOpt = input.list("yList", Codec.DOUBLE);
        if (yListOpt.isEmpty()) return null;
        var ys = new DoubleArrayList(yListOpt.get().stream().toList());

        Optional<ValueInput.TypedInputList<Double>> zListOpt = input.list("zList", Codec.DOUBLE);
        if (zListOpt.isEmpty()) return null;
        var zs = new DoubleArrayList(zListOpt.get().stream().toList());

        return new ArrayVoxelShape(discrete, xs, ys, zs);
    }

    private static VoxelShape loadSliceShape(ValueInput input, DiscreteVoxelShape discrete) {
        Optional<String> axisStr = input.getString("axis");
        if (axisStr.isEmpty()) return null;
        var axis = Direction.Axis.byName(axisStr.get());
        if (axis == null) return null;

        var delegate = loadShape(input.childOrEmpty("delegate"));
        if (delegate == null) return null;

        var result = new SliceShape(delegate, axis, 0);
        result.shape = discrete;
        return result;
    }

    private static VoxelShape loadCubeShape(DiscreteVoxelShape discrete) {
        return new CubeVoxelShape(discrete);
    }

    private static DiscreteVoxelShape loadDiscrete(ValueInput input) {
        Optional<String> typeStr = input.getString("type");
        if (typeStr.isEmpty()) return null;
        var type = DiscreteShapeType.from(typeStr.get());
        if (type == null) return null;

        Optional<Integer> xSizeOpt = input.getInt("xSize");
        if (xSizeOpt.isEmpty()) return null;
        Optional<Integer> ySizeOpt = input.getInt("ySize");
        if (ySizeOpt.isEmpty()) return null;
        Optional<Integer> zSizeOpt = input.getInt("zSize");
        if (zSizeOpt.isEmpty()) return null;

        int xSize = xSizeOpt.get();
        int ySize = ySizeOpt.get();
        int zSize = zSizeOpt.get();

        return switch (type) {
            case BIT_SET -> loadBitSetDiscrete(input, xSize, ySize, zSize);
            case SUB     -> loadSubDiscrete(input, xSize, ySize, zSize);
        };
    }

    private static DiscreteVoxelShape loadBitSetDiscrete(ValueInput input, int xSize, int ySize, int zSize) {
        Optional<ValueInput.TypedInputList<Long>> storageListOpt = input.list("storage", Codec.LONG);
        if (storageListOpt.isEmpty()) return null;
        long[] storageArray = storageListOpt.get().stream().mapToLong(Long::longValue).toArray();
        var storage = BitSet.valueOf(storageArray);

        Optional<Integer> xMinOpt = input.getInt("xMin"); if (xMinOpt.isEmpty()) return null;
        Optional<Integer> yMinOpt = input.getInt("yMin"); if (yMinOpt.isEmpty()) return null;
        Optional<Integer> zMinOpt = input.getInt("zMin"); if (zMinOpt.isEmpty()) return null;
        Optional<Integer> xMaxOpt = input.getInt("xMax"); if (xMaxOpt.isEmpty()) return null;
        Optional<Integer> yMaxOpt = input.getInt("yMax"); if (yMaxOpt.isEmpty()) return null;
        Optional<Integer> zMaxOpt = input.getInt("zMax"); if (zMaxOpt.isEmpty()) return null;

        var result = new BitSetDiscreteVoxelShape(xSize, ySize, zSize);
        result.storage = storage;
        result.xMin = xMinOpt.get();
        result.yMin = yMinOpt.get();
        result.zMin = zMinOpt.get();
        result.xMax = xMaxOpt.get();
        result.yMax = yMaxOpt.get();
        result.zMax = zMaxOpt.get();
        return result;
    }

    private static DiscreteVoxelShape loadSubDiscrete(ValueInput input, int xSize, int ySize, int zSize) {
        var parent = loadDiscrete(input.childOrEmpty("parent"));
        if (parent == null) return null;

        Optional<Integer> startXOpt = input.getInt("startX"); if (startXOpt.isEmpty()) return null;
        Optional<Integer> startYOpt = input.getInt("startY"); if (startYOpt.isEmpty()) return null;
        Optional<Integer> startZOpt = input.getInt("startZ"); if (startZOpt.isEmpty()) return null;
        Optional<Integer> endXOpt   = input.getInt("endX");   if (endXOpt.isEmpty())   return null;
        Optional<Integer> endYOpt   = input.getInt("endY");   if (endYOpt.isEmpty())   return null;
        Optional<Integer> endZOpt   = input.getInt("endZ");   if (endZOpt.isEmpty())   return null;

        return new SubShape(parent,
                startXOpt.get(), startYOpt.get(), startZOpt.get(),
                endXOpt.get(),   endYOpt.get(),   endZOpt.get());
    }

    // </editor-fold>
}
