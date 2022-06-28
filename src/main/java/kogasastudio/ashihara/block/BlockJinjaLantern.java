package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class BlockJinjaLantern extends BlockDoubleLantern
{
    public BlockJinjaLantern()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.5F)
            .sound(SoundType.WOOD)
            .setLightLevel(getLightValueLit(15))
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape UPPER_X = Block.makeCuboidShape(1.8D, 0.0D, 12.9D, 14.2D, 13.1D, 3.2D);
        VoxelShape UPPER_Z = Block.makeCuboidShape(12.9D, 0.0D, 1.8D, 3.2D, 12.3D, 14.2D);
        VoxelShape LOWER = Block.makeCuboidShape(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {return LOWER;}
        else {return state.get(AXIS) == Direction.Axis.X ? UPPER_Z : UPPER_X;}
    }
}
