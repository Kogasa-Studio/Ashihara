package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class HouseLikeHangingLanternBlock extends LanternBlock
{
    public HouseLikeHangingLanternBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.METAL)
                                .strength(2.0F)
                                .sound(SoundType.LANTERN)
                                .lightLevel(getLightValueLit(15))
                );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape1 = Block.box(3,4.5,3,13,10,13);
        VoxelShape shape2 = Block.box(0,10,0,16,15,16);
        return Shapes.or(shape1, shape2);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        if (stateIn.getValue(LIT))
        {
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + 0.53125D;
            double d2 = (double) pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.above()).getBlock() != Blocks.AIR;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
