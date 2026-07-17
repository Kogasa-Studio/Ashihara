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
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class SlopedRoof extends AdditionalComponent
{
    public enum Width { FULL, HALF, QUARTER }

    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;
    private final Width WIDTH;

    public SlopedRoof
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Width widthIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.MODEL = model;
        this.SHAPE = shape;
        this.WIDTH = widthIn;
    }
    @Override
    public VoxelShape getBaseShape() { return SHAPE; }

    @Override
    public VoxelShape rebuildShape(Vec3 ibp, float rx, float ry, float rz) {
        VoxelShape s = ShapeHelper.rotateShape(getBaseShape(), -ry);
        return ShapeHelper.offsetShape(s, ibp.x, ibp.y, ibp.z);
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

        double x, z;
        double y = inBlockPos.y();

        if (context.getClickedFace() == Direction.DOWN)
        {
            y = XTP(Math.round(y * 16 - 4));
            r += 180f;
        }

        int floor = (int) Math.clamp(Math.floor(y * 4), 0, 3);
        y = XTP((float) (floor * 4));

        if (this.WIDTH == Width.HALF)
        {
            if (direction.getAxis().equals(Direction.Axis.Z))
            {
                x = 0;
                if (inBlockPos.z() == XTP(8)) z = context.getClickedFace() == Direction.NORTH ? XTP(-4) : XTP(4);
                else z = inBlockPos.z() < XTP(8) ? XTP(-4) : XTP(4);
            }
            else
            {
                z = 0;
                if (inBlockPos.x() == XTP(8)) x = context.getClickedFace() == Direction.WEST ? XTP(-4) : XTP(4);
                else x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
            }
        }
        else if (this.WIDTH == Width.QUARTER)
        {
            x = inBlockPos.x() < XTP(8) ? XTP(-4) : XTP(4);
            if (inBlockPos.x() == XTP(8)) x = context.getClickedFace() == Direction.WEST ? XTP(-4) : XTP(4);
            z = inBlockPos.z() < XTP(8) ? XTP(-4) : XTP(4);
            if (inBlockPos.z() == XTP(8)) z = context.getClickedFace() == Direction.NORTH ? XTP(-4) : XTP(4);
        }
        else { x = 0; z = 0; }

        Occupation occupation;
        if (this.WIDTH == Width.QUARTER)
            occupation = Occupation.mapPosition(x + XTP(8), y, z + XTP(8));
        else
            occupation = Occupation.CENTER_ALL.get(floor);

        VoxelShape shape = ShapeHelper.rotateShape(this.SHAPE, -r);
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
