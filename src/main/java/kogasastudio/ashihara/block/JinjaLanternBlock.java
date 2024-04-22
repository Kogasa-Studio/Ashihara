package kogasastudio.ashihara.block;

import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class JinjaLanternBlock extends DoubleLanternBlock.AxisAlignedVariant
{
    public JinjaLanternBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(0.5F)
                                .sound(SoundType.WOOD)
                                .lightLevel(getLightValueLit(15)),
                        0, 0, 0
                );
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {return;}

    VoxelShape UPPER_X = Block.box(1.8D, 0.0D, 3.2D, 14.2D, 13.1D, 12.9D);
    VoxelShape UPPER_Z = ShapeHelper.rotateShape(UPPER_X, 90);
    VoxelShape LOWER = Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
        {
            return LOWER;
        } else
        {
            return state.getValue(AXIS) == Direction.Axis.X ? UPPER_Z : UPPER_X;
        }
    }
}
