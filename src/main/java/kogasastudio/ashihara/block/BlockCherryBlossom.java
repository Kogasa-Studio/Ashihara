package kogasastudio.ashihara.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockCherryBlossom extends LeavesBlock
{
    public BlockCherryBlossom()
    {
        super
        (
            Properties.create(Material.LEAVES)
            .hardnessAndResistance(0.2F)
            .tickRandomly()
            .sound(SoundType.PLANT)
            .notSolid()
            .setLightLevel((state) -> 15)
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, Boolean.FALSE));
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 0;
    }
}
