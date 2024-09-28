package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Function;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class RoundTile extends AdditionalComponent
{
    protected final BuildingComponentModelResourceLocation MODEL;
    public final Function<Double, Integer> floorFunc;
    public final float xRotation;
    public final float yRotation;
    public final float yOffset;

    protected VoxelShape SHAPE;

    public RoundTile
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Function<Double, Integer> floorFuncIn,
        float xRotationIn,
        float yRotationIn,
        float yOffsetIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape;
        this.floorFunc = floorFuncIn;
        this.xRotation = xRotationIn;
        this.yRotation = yRotationIn;
        this.yOffset = yOffsetIn;
    }

    @Override
    public SoundType getSoundType()
    {
        return SoundType.DEEPSLATE_TILES;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };
        double y = inBlockPos.y();

        int floor = this.floorFunc.apply(y);

        y = XTP((float) (floor * 4));

        Occupation occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, 0, y + yOffset, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y + yOffset, 0),
            -xRotation, r - yRotation, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }
}
