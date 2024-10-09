package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CherryLeavesBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.Blocks.AIR;

public class AbstractFallingLeavesBlock extends CherryLeavesBlock
{
    private final boolean flammable;

    public AbstractFallingLeavesBlock(Properties properties)
    {
        this(0, true, properties);
    }

    public AbstractFallingLeavesBlock(int light, boolean flammableIn, Properties properties)
    {
        super(properties.lightLevel(p -> light));
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, Boolean.FALSE));
        this.flammable = flammableIn;
    }

    protected Block getFallenBlock()
    {
        return AIR;
    }

    protected SimpleParticleType getParticle()
    {
        return null;
    }
    protected List<ItemStack> getBonusResource()
    {
        return new ArrayList<>();
    }

    @Override
    public void playerDestroy(Level pLevel, Player player, BlockPos pPos, BlockState pState, @Nullable BlockEntity te, ItemStack item)
    {
        if (!player.isCreative() && !this.getBonusResource().isEmpty())
        {
            this.getBonusResource().forEach(itemStack -> popResource((Level) pLevel, pPos, itemStack));
        }
        super.playerDestroy(pLevel, player, pPos, pState, te, item);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random)
    {
        //原版代码
        if (!state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 7)
        {
            dropResources(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
        //若樱花方块正下方25格以内有实心方块，且该方块上方为空气，则在该方块上方生成落樱毯
        if (!(this.getFallenBlock().equals(AIR)) && (worldIn.isEmptyBlock(pos.below()) || worldIn.getBlockState(pos.below()).getBlock() instanceof CherryVinesBlock))
        {
            BlockPos pos1 = pos;
            for (int j = 0; j < 25; j += 1)
            {
                pos1 = pos1.below();
                if (this.getFallenBlock().defaultBlockState().canSurvive(worldIn, pos1.above()))
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
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (this.getParticle() != null && rand.nextInt(30) == 1 && (blockstate.isPathfindable(PathComputationType.AIR)))
        {
            worldIn.addParticle(this.getParticle(), (double) pos.getX() + 0.5, (double) pos.getY() - 0.1D, (double) pos.getZ() + 0.5, rand.nextInt(10) / 200.0F, 0, rand.nextInt(10) / 200.0F);
        }
        if (worldIn.isRainingAt(pos.above()))
        {
            if (rand.nextInt(15) == 1)
            {
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP))
                {
                    double d0 = (double) pos.getX() + rand.nextDouble();
                    double d1 = (double) pos.getY() - 0.05D;
                    double d2 = (double) pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return this.flammable ? 60 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return this.flammable ? 30 : 0;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return this.flammable;
    }
}
