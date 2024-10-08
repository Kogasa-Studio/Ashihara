package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class BigTou extends BuildingComponent
{
    private VoxelShape SHAPE;

    public BigTou(String idIn, BuildingComponents.Type typeIn, Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, materialIn, dropsIn);
        initShape();
    }

    protected void initShape()
    {
        this.SHAPE = Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.40625, 0.8125);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        double y = inBlockPos.y();

        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);

        int floor = y == 0 ? 0 : 2;

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, 0, 0,
            shape,
            AdditionalModels.RED_BIG_TOU,
            List.of(Occupation.CENTER_ALL.get(floor))
        );
    }
}
