package kogasastudio.ashihara.utils.shape;

import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;

public enum DiscreteShapeType {
    BIT_SET("bit_set", BitSetDiscreteVoxelShape.class),
    SUB("sub", SubShape.class),
    ;

    private final String name;
    private final Class<?> clazz;

    DiscreteShapeType(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static DiscreteShapeType from(String name) {
        for (var v : values()) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static DiscreteShapeType from(Class<?> clazz) {
        for (var v : values()) {
            if (v.getClazz().equals(clazz)) {
                return v;
            }
        }
        return null;
    }
}
