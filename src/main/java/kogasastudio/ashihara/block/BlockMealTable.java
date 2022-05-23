package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockMealTable extends Block {
    public BlockMealTable() {
        super
                (
                        Properties.of(Material.WOOD)
                                .sound(SoundType.BAMBOO)
                                .strength(0.5f)
                );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape base = box(3.0d, 0.0d, 3.0d, 13.0d, 4.5d, 13.0d);
        VoxelShape plate_base = box(2.5d, 4.5d, 2.5d, 13.5d, 5.0d, 13.5d);
        VoxelShape plate = box(2.0, 5.0, 2.0, 14.0, 6.0, 14.0);
        VoxelShape edge_n = box(1.0, 5.0, 1.0, 15.0, 7.0, 2.0);
        VoxelShape edge_e = box(14.0, 5.0, 2.0, 15.0, 7.0, 14.0);
        VoxelShape edge_s = box(1.0, 5.0, 14.0, 15.0, 7.0, 15.0);
        VoxelShape edge_w = box(2.0, 5.0, 1.0, 14.0, 7.0, 2.0);

        return Shapes.or(base, plate_base, plate, edge_n, edge_e, edge_s, edge_w);
    }
}
