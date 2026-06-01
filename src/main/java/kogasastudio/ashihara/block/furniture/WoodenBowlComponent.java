package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class WoodenBowlComponent extends FurnitureComponent
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public WoodenBowlComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        FurnitureRendererType rendererTypeIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererTypeIn);
        this.MODEL = model;
        this.SHAPE = shape != null ? shape : Shapes.box(5.5f / 16, 0, 5.5f / 16, 10.5f / 16, 3.5f / 16, 10.5f / 16);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlock = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));
        double x = inBlock.x() - 8f / 16;
        double y = inBlock.y();
        double z = inBlock.z() - 8f / 16;

        VoxelShape shape = ShapeHelper.offsetShape(this.SHAPE, x, y, z);

        return new ComponentStateDefinition
        (
            FurnitureComponents.get(this.id),
            new Vec3(x, y, z),
            0, 0, 0,
            shape,
            MODEL,
            List.of()
        );
    }
}
