package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LanternBlock extends Block implements SimpleWaterloggedBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final double flameX;
    protected final double flameY;
    protected final double flameZ;

    public LanternBlock(Properties properties, double XIn, double YIn, double ZIn)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(WATERLOGGED, false));

        this.flameX = XIn;
        this.flameY = YIn;
        this.flameZ = ZIn;
    }

    public boolean showFlameParticle()
    {
        return true;
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        if (!this.showFlameParticle()) return;
        if (stateIn.getValue(LIT))
        {
            double x = (double) pos.getX() + this.flameX;
            double y = (double) pos.getY() + this.flameY;
            double z = (double) pos.getZ() + this.flameZ;
            worldIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult)
    {
        RandomSource random = pLevel.getRandom();
        if (pState.getValue(LIT))
        {
            pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public InteractionResult useItemOn(ItemStack pStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        RandomSource random = worldIn.getRandom();
        if (player.getItemInHand(handIn).getItem().equals(Items.FLINT_AND_STEEL) && !state.getValue(LIT))
        {
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, true));
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, WATERLOGGED);
    }

    public static class HangingLanternBlock extends LanternBlock
    {
        public HangingLanternBlock(Properties properties, double XIn, double YIn, double ZIn)
        {
            super(properties, XIn, YIn, ZIn);
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
        {
            return worldIn.getBlockState(pos.above()).getBlock() != Blocks.AIR;
        }

        @Override
        protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random)
        {
            return !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
