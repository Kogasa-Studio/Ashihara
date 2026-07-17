package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class AdvancedFencePillar extends AdditionalComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    protected VoxelShape SHAPE;

    public AdvancedFencePillar
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape;
    }
    @Override
    public VoxelShape getBaseShape() { return SHAPE; }

    @Override
    public VoxelShape rebuildShape(Vec3 ibp, float rx, float ry, float rz) {
        VoxelShape s = ShapeHelper.rotateShape(getBaseShape(), -ry);
        return ShapeHelper.offsetShape(s, ibp.x, ibp.y, ibp.z);
    }



    public AdvancedFencePillar
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        this(idIn, typeIn, model, null, materialIn, dropsIn);
        initShape();
    }

    private void initShape()
    {
        this.SHAPE = Shapes.box(0.375, 0, 0.375, 0.625, 0.71875, 0.625);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        Vec3 inBlockPos = beIn.inBlockVec(context.getClickLocation());

        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };

        double y = inBlockPos.y();

        // 8-floor division: rawFloor 0-1 -> F1, 2-3 -> F2, 4-5 -> F3, 6-7 -> F4
        int rawFloor = (int) Math.clamp(Math.floor(y * 8), 0, 7);
        int occFloor = rawFloor / 2;

        y = XTP((float) (rawFloor * 2));

        Occupation occupation = Occupation.CENTER_ALL.get(occFloor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, r, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }
}
