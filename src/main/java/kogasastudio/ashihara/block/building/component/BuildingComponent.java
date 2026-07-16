package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.context.UseOnContext;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.block.SoundType;
import java.util.List;
import java.util.function.Supplier;

public abstract class BuildingComponent
{
    public final String id;
    public final BuildingComponents.Type type;
    public final List<ItemStack> drops;

    public final Supplier<BaseMultiBuiltBlock> material;
    public SoundType sound;
    public boolean complexShape = false;

    public float getxMaxRange() {return xMaxRange;}
    public float getxMinRange() {return xMinRange;}
    public float getyMaxRange() {return yMaxRange;}
    public float getyMinRange() {return yMinRange;}
    public float getzMaxRange() {return zMaxRange;}
    public float getzMinRange() {return zMinRange;}

    protected float xMinRange = -0.5f, xMaxRange = 0.5f;
    protected float yMinRange = 0f, yMaxRange = 1f;
    protected float zMinRange = -0.5f, zMaxRange = 0.5f;

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn, Supplier<BaseMultiBuiltBlock> materialIn, SoundType soundIn)
    {
        this.id = idIn;
        this.type = typeIn;
        this.drops = dropsIn;
        this.sound = soundIn;
        this.material = materialIn;
    }

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, dropsIn, materialIn, SoundType.WOOD);
    }

    public abstract ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context);

    /**
     * Returns the items to drop when this component is removed.
     * Override in subclasses that have dynamic drops (e.g. containers with contents).
     */
    public List<ItemStack> getDrops(ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        return List.copyOf(this.drops);
    }

    public Supplier<BaseMultiBuiltBlock> getMaterial() {return material;}

    public SoundType getSoundType() {return this.sound;}

    public BuildingComponent setSound(SoundType soundIn)
    {
        this.sound = soundIn;
        return this;
    }

    @Nullable
    public VoxelShape getBaseShape() { return null; }

    public boolean isComplexShape() {return complexShape;}

    public BuildingComponent setComplexShape(boolean complexShapeIn)
    {
        this.complexShape = complexShapeIn;
        return this;
    }

    /**
     * Rebuild shape from stored fields. Override in subclasses whose shape
     * follows rotate(base, angle) + offset(inBlockPos) pattern.
     */
    public VoxelShape rebuildShape(Vec3 inBlockPos, float rotationX, float rotationY, float rotationZ)
    {
        VoxelShape base = getBaseShape();
        return base != null ? ShapeHelper.offsetShape(base, inBlockPos.x, inBlockPos.y, inBlockPos.z) : Shapes.empty();
    }
}
