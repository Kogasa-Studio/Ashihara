package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class SimpleLogBlock extends RotatedPillarBlock
{
    public SimpleLogBlock()
    {
        super
                (
                        Properties.of(Material.WOOD, (state) ->
                                        state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.COLOR_ORANGE : MaterialColor.STONE)
                                .strength(2.0F)
                                .sound(SoundType.WOOD)
                );
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return 5;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return true;
    }
}
