package kogasastudio.ashihara.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockJinjaLantern extends BlockDoubleLantern
{
    public BlockJinjaLantern()
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD)
            .lightLevel(getLightValueLit(15))
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape UPPER_X = Block.box(1.8D, 0.0D, 12.9D, 14.2D, 13.1D, 3.2D);
        VoxelShape UPPER_Z = Block.box(12.9D, 0.0D, 1.8D, 3.2D, 12.3D, 14.2D);
        VoxelShape LOWER = Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {return LOWER;}
        else {return state.getValue(AXIS) == Direction.Axis.X ? UPPER_Z : UPPER_X;}
    }
}
