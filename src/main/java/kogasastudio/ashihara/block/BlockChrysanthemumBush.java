package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockChrysanthemumBush extends BushBlock implements IForgeShearable
{
    public BlockChrysanthemumBush()
    {
        super
        (
            Properties.of(Material.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public long getSeed(BlockState state, BlockPos pos)
    {
        return MathHelper.getSeed(pos.getX(), pos.getY(), pos.getZ());
    }
}
