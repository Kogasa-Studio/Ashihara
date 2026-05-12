package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.IShearable;

public class ChrysanthemumBushBlock extends BushBlock implements IShearable
{
    public ChrysanthemumBushBlock(Properties properties)
    {
        super(properties);
    }

    public ChrysanthemumBushBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollision()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XYZ)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    }

    @Override
    
    public long getSeed(BlockState state, BlockPos pos)
    {
        return Mth.getSeed(pos.getX(), pos.getY(), pos.getZ());
    }
}
