package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class SimpleWoodBlock extends RotatedPillarBlock
{
    public SimpleWoodBlock()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.2F, 0.3F)
            .sound(SoundType.WOOD)
        );
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return true;}
}
