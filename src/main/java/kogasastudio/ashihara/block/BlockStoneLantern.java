package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static kogasastudio.ashihara.utils.EasyBlockActionHandler.getLightValueLit;

public class BlockStoneLantern extends BlockDoubleLantern
{
    public BlockStoneLantern()
    {
        super
        (
            Properties.create(Material.ROCK)
            .hardnessAndResistance(4.0F)
            .sound(SoundType.STONE)
            .setLightLevel(getLightValueLit(15))
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT) && stateIn.get(HALF) == DoubleBlockHalf.UPPER)
        {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.125D;
            double d2 = (double)pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape v1 = Block.makeCuboidShape(2.0d, 0.0d, 2.0d, 14.0d, 8.0d, 14.0d);
        VoxelShape v2 = Block.makeCuboidShape(3.0d, 8.0d, 3.0d, 13.0d, 16.0d, 13.0d);
        VoxelShape LOWER = VoxelShapes.or(v1, v2);
        VoxelShape v3 = Block.makeCuboidShape(5.0d, 0.0d, 5.0d, 11.0d, 5.0d, 11.0d);
        VoxelShape v4 = Block.makeCuboidShape(2.0d, 5.0d, 2.0d, 14.0d, 11.25d, 14.0d);
        VoxelShape v5 = Block.makeCuboidShape(7.0d, 11.25d, 7.0d, 9.0d, 13.25d, 9.0d);
        VoxelShape UPPER = VoxelShapes.or(v3, v4, v5);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {return LOWER;}
        else {return UPPER;}
    }
}
