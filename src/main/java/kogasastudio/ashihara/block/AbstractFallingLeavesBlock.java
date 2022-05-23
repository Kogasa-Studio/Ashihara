package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.GenericParticleType;
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

import static net.minecraft.block.Blocks.AIR;

import net.minecraft.block.AbstractBlock.Properties;

public class AbstractFallingLeavesBlock extends LeavesBlock
{
    public AbstractFallingLeavesBlock()
    {
        this(0, true);
    }

    public AbstractFallingLeavesBlock(int light, boolean flammableIn)
    {
        super
        (
            Properties.of(Material.LEAVES)
            .strength(0.05F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .lightLevel((state) -> light)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, Boolean.FALSE));
        this.flammable = flammableIn;
    }

    private final boolean flammable;

    protected Block getFallenBlock() {return AIR;}

    protected GenericParticleType getParticle() {return null;}

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {builder.add(DISTANCE, PERSISTENT);}

    @Override
    public boolean isRandomlyTicking(BlockState state) {return true;}

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        //原版代码
        if (!state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 7)
        {
            dropResources(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
        //若樱花方块正下方50格以内有实心方块，且该方块上方为空气，则在该方块上方生成落樱毯R
        if (!this.getFallenBlock().is(AIR) && worldIn.isEmptyBlock(pos.below()))
        {
            BlockPos pos1 = pos;
            for (int j = 0; j < 50; j += 1)
            {
                pos1 = pos1.below();
                BlockState state1 = worldIn.getBlockState(pos1);
                if (state1.isFaceSturdy(worldIn, pos1, Direction.UP))
                {
                    if (worldIn.isEmptyBlock(pos1.above()))
                    {
                        worldIn.setBlockAndUpdate(pos1.above(), this.getFallenBlock().defaultBlockState());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (this.getParticle() != null && rand.nextInt(30) == 1 && (blockstate.isPathfindable(worldIn, blockpos, PathType.AIR)))
        {
            worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, this.getParticle()), (double)pos.getX() + 0.5, (double)pos.getY() - 0.1D, (double)pos.getZ() + 0.5, rand.nextInt(10) / 200.0F, 0, rand.nextInt(10) / 200.0F);
        }
        if (worldIn.isRainingAt(pos.above()))
        {
            if (rand.nextInt(15) == 1)
            {
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP))
                {
                    double d0 = (double)pos.getX() + rand.nextDouble();
                    double d1 = (double)pos.getY() - 0.05D;
                    double d2 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable ? 60 : 0;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable ? 30 : 0;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable;}
}
