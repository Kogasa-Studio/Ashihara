package kogasastudio.ashihara.utils.shape;

import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.SliceShape;

public enum VoxelShapeType {
    ARRAY_VOXEL_SHAPE("array", ArrayVoxelShape.class),
    SLICE_SHAPE("slice", SliceShape.class),
    CUBE_VOXEL_SHAPE("cube", CubeVoxelShape.class),
    ;

    private final String name;
    private final Class<?> clazz;

    VoxelShapeType(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static VoxelShapeType from(String name) {
        for (var v : values()) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static VoxelShapeType from(Class<?> clazz) {
        for (var v : values()) {
            if (v.getClazz().equals(clazz)) {
                return v;
            }
        }
        return null;
    }
}
