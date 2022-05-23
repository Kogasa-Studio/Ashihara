package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SimpleFenceGateBlock extends FenceGateBlock
{
    public SimpleFenceGateBlock()
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
        );
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 20;}

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return true;}
}
