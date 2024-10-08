package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
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

import static kogasastudio.ashihara.block.building.BaseMultiBuiltBlock.FACING;
import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class QuarterFloor extends AdditionalComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    private VoxelShape SHAPE;

    public QuarterFloor
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

    public QuarterFloor
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
        this.SHAPE = Shapes.box(0.25, 0, 0.25, 0.75, 0.25, 0.75);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r = switch (beIn.getBlockState().getValue(FACING))
        {
            case WEST -> -90;
            case SOUTH -> -180;
            case EAST -> -270;
            default -> 0;
        };
        double y = inBlockPos.y();

        int floor = (int) Math.clamp(Math.floor(y * 4), 0, 3);

        y = XTP((float) (floor * 4));

        double x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
        double z = inBlockPos.z() <= XTP(8) ? XTP(-4) : XTP(4);

        Occupation occupation = Occupation.mapPosition(x + XTP(8), y, z + XTP(8));

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.offsetShape(shape, x, y, z);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, r, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }
}
