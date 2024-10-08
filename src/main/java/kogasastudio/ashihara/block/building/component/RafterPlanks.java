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

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class RafterPlanks extends AdditionalComponent
{
    private final BuildingComponentModelResourceLocation MODEL;

    protected VoxelShape SHAPE;

    public RafterPlanks
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

    public RafterPlanks
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
        this.SHAPE = Shapes.box(0, 0, 0.25, 1, 0.25625, 0.75);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        float r;
        double x;
        double y = inBlockPos.y();
        double z;

        int floor = (int) Math.clamp(Math.floor(y * 4), 0, 3);

        y = XTP((float) (floor * 4));
        if (direction.getAxis().equals(Direction.Axis.Z))
        {
            x = 0;
            z = inBlockPos.z() <= XTP(8) ? XTP(-4) : XTP(4);
            r = 0;
        }
        else
        {
            z = 0;
            x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
            r = 90;
        }

        Occupation occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
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
