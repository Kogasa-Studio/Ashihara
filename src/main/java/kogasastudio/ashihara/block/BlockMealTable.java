package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockMealTable extends Block
{
    public BlockMealTable()
    {
        super
        (
            Properties.create(Material.WOOD)
            .sound(SoundType.BAMBOO)
            .hardnessAndResistance(0.5f)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape base = makeCuboidShape(3.0d, 0.0d, 3.0d, 13.0d, 4.5d, 13.0d);
        VoxelShape plate_base = makeCuboidShape(2.5d, 4.5d, 2.5d, 13.5d, 5.0d, 13.5d);
        VoxelShape plate = makeCuboidShape(2.0, 5.0, 2.0, 14.0, 6.0, 14.0);
        VoxelShape edge_n = makeCuboidShape(1.0, 5.0, 1.0, 15.0, 7.0, 2.0);
        VoxelShape edge_e = makeCuboidShape(14.0, 5.0, 2.0, 15.0, 7.0, 14.0);
        VoxelShape edge_s = makeCuboidShape(1.0, 5.0, 14.0, 15.0, 7.0, 15.0);
        VoxelShape edge_w = makeCuboidShape(2.0, 5.0, 1.0, 14.0, 7.0, 2.0);

        return VoxelShapes.or(base, plate_base, plate, edge_n, edge_e, edge_s, edge_w);
    }
}
