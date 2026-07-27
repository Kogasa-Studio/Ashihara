package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class SimpleFurnitureComponent extends FurnitureComponent implements MultiBlockFurniture
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public SimpleFurnitureComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        FurnitureRenderPass rendererPassIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererPassIn);
        this.MODEL = model;
        this.SHAPE = shape;
    }

    @Override
    public VoxelShape getBaseShape() { return this.SHAPE; }

    @Override
    public boolean isComplexShape() { return true; }

    @Override
    public VoxelShape rebuildShape(Vec3 inBlockPos, float rotX, float rotY, float rotZ)
    {
        VoxelShape s = ShapeHelper.offsetShape(this.SHAPE, inBlockPos.x(), inBlockPos.y(), inBlockPos.z());
        return ShapeHelper.sliceShape(s, 1, net.minecraft.core.Vec3i.ZERO);
    }

    @Override
    public BlockBox getExtent(Direction facing)
    {
        var bb = this.SHAPE.bounds();
        int x0 = (int) Math.floor(bb.minX - 0.5), x1 = (int) Math.ceil(bb.maxX + 0.5) - 1;
        int y0 = (int) Math.floor(bb.minY), y1 = (int) Math.ceil(bb.maxY) - 1;
        int z0 = (int) Math.floor(bb.minZ - 0.5), z1 = (int) Math.ceil(bb.maxZ + 0.5) - 1;
        return new BlockBox(new BlockPos(x0, y0, z0), new BlockPos(x1, y1, z1));
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlock = beIn.inBlockVec(context.getClickLocation());
        double x = clampInBlock(inBlock.x() - 8f / 16, -0.5, 0.5);
        double y = clampInBlock(inBlock.y(), 0.0, 1.0);
        double z = clampInBlock(inBlock.z() - 8f / 16, -0.5, 0.5);
        VoxelShape shape = ShapeHelper.offsetShape(this.SHAPE, x, y, z);
        return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), null);
    }
}
