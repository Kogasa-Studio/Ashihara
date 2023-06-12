package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SimpleWoodBlock extends RotatedPillarBlock
{
    public SimpleWoodBlock()
    {
        super
                (
                        Properties.of(Material.WOOD)
                                .strength(0.2F, 0.3F)
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
