package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class ShortColumn extends BuildingComponent
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public ShortColumn(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = modelIn;
        this.SHAPE = Block.box(3d, 0d, 3d, 13d, 8d, 13d);
    }

    public ShortColumn(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, VoxelShape shapeIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.MODEL = modelIn;
        this.SHAPE = shapeIn;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        double y = inBlockPos.y();
        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);
        int floor = y == 0 ? 0 : 2;

        VoxelShape shape = this.SHAPE;
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, 0, 0,
            shape,
            MODEL,
            List.of(Occupation.CENTER_ALL.get(floor), Occupation.CENTER_ALL.get(floor + 1))
        );
    }
}
