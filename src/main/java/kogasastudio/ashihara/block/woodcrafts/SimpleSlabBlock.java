package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public class SimpleSlabBlock extends SlabBlock
{
    public SimpleSlabBlock()
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
        );
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 20;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return true;}
}
