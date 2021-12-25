package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockCherryBlossom extends LeavesBlock
{
    public BlockCherryBlossom()
    {
        super
        (
            Properties.create(Material.LEAVES)
            .hardnessAndResistance(0.05F)
            .tickRandomly()
            .sound(SoundType.PLANT)
            .notSolid()
            .setLightLevel((state) -> 5)
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, Boolean.FALSE));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {builder.add(DISTANCE, PERSISTENT);}

    @Override
    public boolean ticksRandomly(BlockState state) {return true;}

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        //原版代码
        if (!state.get(PERSISTENT) && state.get(DISTANCE) == 7)
        {
            spawnDrops(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
        //若樱花方块正下方50格以内有实心方块，且该方块上方为空气，则在该方块上方生成落樱毯
        if (worldIn.isAirBlock(pos.down()))
        {
            BlockPos pos1 = pos;
            for (int j = 0; j < 50; j += 1)
            {
                pos1 = pos1.down();
                BlockState state1 = worldIn.getBlockState(pos1);
                if (state1.isSolidSide(worldIn, pos1, Direction.UP))
                {
                    if (worldIn.isAirBlock(pos1.up()))
                    {
                        worldIn.setBlockState(pos1.up(), BlockRegistryHandler.FALLEN_SAKURA.get().getDefaultState());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (rand.nextInt(30) == 1 && (blockstate.allowsMovement(worldIn, blockpos, PathType.AIR)))
        {
            worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, ParticleRegistryHandler.SAKURA.get()), (double)pos.getX() + 0.5, (double)pos.getY() - 0.1D, (double)pos.getZ() + 0.5, rand.nextInt(10) / 200.0F, 0, rand.nextInt(10) / 200.0F);
        }
        if (worldIn.isRainingAt(pos.up()))
        {
            if (rand.nextInt(15) == 1)
            {
                if (!blockstate.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, Direction.UP))
                {
                    double d0 = (double)pos.getX() + rand.nextDouble();
                    double d1 = (double)pos.getY() - 0.05D;
                    double d2 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
