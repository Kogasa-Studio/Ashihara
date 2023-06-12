package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;

public class ChrysanthemumBushBlock extends BushBlock implements IForgeShearable
{
    public ChrysanthemumBushBlock()
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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public long getSeed(BlockState state, BlockPos pos)
    {
        return Mth.getSeed(pos.getX(), pos.getY(), pos.getZ());
    }
}
