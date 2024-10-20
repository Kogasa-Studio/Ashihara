package kogasastudio.ashihara.utils.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.phys.shapes.*;

import java.util.BitSet;

public class VoxelShapeSerializer {

    // <editor-fold desc="Save.">

    public static CompoundTag saveShape(VoxelShape shape, HolderLookup.Provider registry) {
        var type = VoxelShapeType.from(shape.getClass());
        if (type == null) {
            return null;
        }

        var tag = new CompoundTag();
        tag.putString("type", type.getName());

        var discrete = saveDiscrete(shape.shape, registry);
        if (discrete == null) {
            return null;
        }
        tag.put("discrete", discrete);

        switch (type) {
            case ARRAY_VOXEL_SHAPE -> saveArrayShape(tag, (ArrayVoxelShape) shape, registry);
            case SLICE_SHAPE -> saveSliceShape(tag, (SliceShape) shape, registry);
            case CUBE_VOXEL_SHAPE -> saveCubeShape(tag, (CubeVoxelShape) shape, registry);
        }
        return tag;
    }

    private static void saveArrayShape(CompoundTag tag, ArrayVoxelShape shape, HolderLookup.Provider registry) {
        var xList = new ListTag();
        shape.xs.forEach(d -> xList.add(DoubleTag.valueOf(d)));
        tag.put("xList", xList);
        var yList = new ListTag();
        shape.ys.forEach(d -> yList.add(DoubleTag.valueOf(d)));
        tag.put("yList", yList);
        var zList = new ListTag();
        shape.zs.forEach(d -> zList.add(DoubleTag.valueOf(d)));
        tag.put("zList", zList);
    }

    private static void saveSliceShape(CompoundTag tag, SliceShape shape, HolderLookup.Provider registry) {
        var delegate = saveShape(shape.delegate, registry);
        if (delegate == null) {
            return;
        }
        tag.put("delegate", delegate);
        tag.putString("axis", shape.axis.getName());
    }

    private static void saveCubeShape(CompoundTag tag, CubeVoxelShape shape, HolderLookup.Provider registry) {
    }

    private static CompoundTag saveDiscrete(DiscreteVoxelShape shape, HolderLookup.Provider registry) {
        var tag = new CompoundTag();
        var type = DiscreteShapeType.from(shape.getClass());
        if (type == null) {
            return null;
        }
        tag.putString("type", type.getName());
        tag.putInt("xSize", shape.xSize);
        tag.putInt("ySize", shape.ySize);
        tag.putInt("zSize", shape.zSize);
        return tag;
    }

    private static void saveBitSetDiscrete(CompoundTag tag, BitSetDiscreteVoxelShape shape,
                                           HolderLookup.Provider registry) {
        tag.putLongArray("storage", shape.storage.toLongArray());
        tag.putInt("xMax", shape.xMax);
        tag.putInt("yMax", shape.yMax);
        tag.putInt("zMax", shape.zMax);
    }

    private static void saveSubDiscrete(CompoundTag tag, SubShape shape, HolderLookup.Provider registry) {
        var parent = saveDiscrete(shape.parent, registry);
        if (parent == null) {
            return;
        }

        tag.put("parent", parent);
        tag.putInt("startX", shape.startX);
        tag.putInt("startY", shape.startY);
        tag.putInt("startZ", shape.startZ);
        tag.putInt("endX", shape.endX);
        tag.putInt("endY", shape.endY);
        tag.putInt("endZ", shape.endZ);
    }

    // </editor-fold>

    // <editor-fold desc="Load.">

    public static VoxelShape loadShape(CompoundTag tag, HolderLookup.Provider registry) {
        if (!tag.contains("type")) {
            return null;
        }
        var type = VoxelShapeType.from(tag.getString("type"));
        if (type == null) {
            return null;
        }

        if (!tag.contains("discrete")) {
            return null;
        }
        var discrete = loadDiscrete(tag.getCompound("discrete"), registry);
        if (discrete == null) {
            return null;
        }

        switch (type) {
            case ARRAY_VOXEL_SHAPE -> {
                return loadArrayShape(tag, registry, discrete);
            }
            case SLICE_SHAPE -> {
                return loadSliceShape(tag, registry, discrete);
            }
            case CUBE_VOXEL_SHAPE -> {
                return loadCubeShape(tag, registry, discrete);
            }
        }

        return null;
    }

    private static VoxelShape loadArrayShape(CompoundTag tag, HolderLookup.Provider registry,
                                             DiscreteVoxelShape discrete) {
        if (!tag.contains("xList")) {
            return null;
        }
        var xList = tag.getList("xList", Tag.TAG_DOUBLE);
        var xs = new DoubleArrayList(xList.stream().map(t -> ((DoubleTag) t).getAsDouble()).toList());

        if (!tag.contains("yList")) {
            return null;
        }
        var yList = tag.getList("yList", Tag.TAG_DOUBLE);
        var ys = new DoubleArrayList(yList.stream().map(t -> ((DoubleTag) t).getAsDouble()).toList());

        if (!tag.contains("zList")) {
            return null;
        }
        var zList = tag.getList("zList", Tag.TAG_DOUBLE);
        var zs = new DoubleArrayList(zList.stream().map(t -> ((DoubleTag) t).getAsDouble()).toList());

        return new ArrayVoxelShape(discrete, xs, ys, zs);
    }

    private static VoxelShape loadSliceShape(CompoundTag tag, HolderLookup.Provider registry,
                                             DiscreteVoxelShape discrete) {
        if (!tag.contains("delegate")) {
            return null;
        }
        var delegate = loadShape(tag.getCompound("delegate"), registry);
        if (delegate == null) {
            return null;
        }

        if (!tag.contains("axis")) {
            return null;
        }
        var axis = Direction.Axis.byName(tag.getString("axis"));
        if (axis == null) {
            return null;
        }
        var result = new SliceShape(delegate, axis, 0);
        result.shape = discrete;
        return result;
    }

    private static VoxelShape loadCubeShape(CompoundTag tag, HolderLookup.Provider registry,
                                            DiscreteVoxelShape discrete) {
        return new CubeVoxelShape(discrete);
    }

    private static DiscreteVoxelShape loadDiscrete(CompoundTag tag, HolderLookup.Provider registry) {
        if (!tag.contains("type")) {
            return null;
        }
        var type = DiscreteShapeType.from(tag.getString("type"));
        if (type == null) {
            return null;
        }

        if (!tag.contains("xSize")) {
            return null;
        }
        var xSize = tag.getInt("xSize");

        if (!tag.contains("ySize")) {
            return null;
        }
        var ySize = tag.getInt("ySize");

        if (!tag.contains("zSize")) {
            return null;
        }
        var zSize = tag.getInt("zSize");

        switch (type) {
            case BIT_SET -> {
                return loadBitSetDiscrete(tag, registry, xSize, ySize, zSize);
            }
            case SUB -> {
                return loadSubDiscrete(tag, registry, xSize, ySize, zSize);
            }
        }
        return null;
    }

    private static DiscreteVoxelShape loadBitSetDiscrete(CompoundTag tag, HolderLookup.Provider registry,
                                                         int xSize, int ySize, int zSize) {
        if (!tag.contains("storage")) {
            return null;
        }
        var storageArray = tag.getLongArray("storage");
        var storage = BitSet.valueOf(storageArray);

        if (!tag.contains("xMax")) {
            return null;
        }
        var xMax = tag.getInt("xMax");

        if (!tag.contains("yMax")) {
            return null;
        }
        var yMax = tag.getInt("yMax");

        if (!tag.contains("zMax")) {
            return null;
        }
        var zMax = tag.getInt("zMax");

        var result = new BitSetDiscreteVoxelShape(xSize, ySize, zSize);
        result.storage = storage;
        result.xMax = xMax;
        result.yMax = yMax;
        result.zMax = zMax;
        return result;
    }

    private static DiscreteVoxelShape loadSubDiscrete(CompoundTag tag, HolderLookup.Provider registry,
                                                      int xSize, int ySize, int zSize) {
        if (!tag.contains("parent")) {
            return null;
        }
        var parent = loadDiscrete(tag.getCompound("parent"), registry);
        if (parent == null) {
            return null;
        }

        if (!tag.contains("startX")) {
            return null;
        }
        var startX = tag.getInt("startX");

        if (!tag.contains("startY")) {
            return null;
        }
        var startY = tag.getInt("startY");

        if (!tag.contains("startZ")) {
            return null;
        }
        var startZ = tag.getInt("startZ");

        if (!tag.contains("endX")) {
            return null;
        }
        var endX = tag.getInt("endX");

        if (!tag.contains("endY")) {
            return null;
        }
        var endY = tag.getInt("endY");

        if (!tag.contains("endZ")) {
            return null;
        }
        var endZ = tag.getInt("endZ");

        return new SubShape(parent, startX, startY, startZ, endX, endY, endZ);
    }

    // </editor-fold>
}
