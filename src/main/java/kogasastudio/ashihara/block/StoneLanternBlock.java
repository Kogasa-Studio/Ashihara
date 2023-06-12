package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;
import static net.minecraft.world.item.Items.GLASS_PANE;

public class StoneLanternBlock extends DoubleLanternBlock
{
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty MOSSY = BooleanProperty.create("mossy");

    public StoneLanternBlock()
    {
        super
                (
                        Properties.of(Material.STONE)
                                .strength(4.0F)
                                .sound(SoundType.STONE)
                                .lightLevel(getLightValueLit(15))
                );
        this.registerDefaultState(this.defaultBlockState().setValue(SEALED, false).setValue(MOSSY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SEALED, MOSSY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT) && stateIn.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + 0.125D;
            double d2 = (double) pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.LOWER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            } else if (state.getValue(LIT) && state.getValue(WATERLOGGED) && !state.getValue(SEALED))
            {
                worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        } else if (doubleblockhalf == DoubleBlockHalf.LOWER)
        {
            BlockPos blockpos = pos.above();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.UPPER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (player.getItemInHand(handIn).getItem() == Items.AIR && state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            if (player.isShiftKeyDown() && state.getValue(SEALED))
            {
                worldIn.playSound(player, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, state.setValue(SEALED, false));
                if (!player.isCreative())
                {
                    player.setItemInHand(handIn, new ItemStack(GLASS_PANE));
                }
                return InteractionResult.SUCCESS;
            } else if (!state.getValue(WATERLOGGED) || state.getValue(SEALED))
            {
                Random random = worldIn.getRandom();
                Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
                worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockAndUpdate(pos, state.setValue(LIT, !instantState));
                return InteractionResult.SUCCESS;
            } else
            {
                return InteractionResult.PASS;
            }
        } else if (player.getItemInHand(handIn).getItem().equals(GLASS_PANE) && state.getValue(HALF).equals(DoubleBlockHalf.UPPER) && !state.getValue(SEALED))
        {
            worldIn.playSound(player, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockAndUpdate(pos, state.setValue(SEALED, true));
            if (!player.isCreative())
            {
                player.getItemInHand(handIn).shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else
        {
            return InteractionResult.PASS;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape v1 = Block.box(2.0d, 0.0d, 2.0d, 14.0d, 8.0d, 14.0d);
        VoxelShape v2 = Block.box(3.0d, 8.0d, 3.0d, 13.0d, 16.0d, 13.0d);
        VoxelShape LOWER = Shapes.or(v1, v2);
        VoxelShape v3 = Block.box(5.0d, 0.0d, 5.0d, 11.0d, 5.0d, 11.0d);
        VoxelShape v4 = Block.box(2.0d, 5.0d, 2.0d, 14.0d, 11.25d, 14.0d);
        VoxelShape v5 = Block.box(7.0d, 11.25d, 7.0d, 9.0d, 13.25d, 9.0d);
        VoxelShape UPPER = Shapes.or(v3, v4, v5);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
        {
            return LOWER;
        } else
        {
            return UPPER;
        }
    }

    @Override
    public void handlePrecipitation(BlockState pState, Level pLevel, BlockPos pPos, Biome.Precipitation pPrecipitation)
    {
        super.handlePrecipitation(pState, pLevel, pPos, pPrecipitation);
    }

    // todo 应该是上面的方法(handlePrecipitation)，我不清楚你想要的功能，没用过，你自己定夺 @Override
    public void handleRain(Level worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.is(BlockRegistryHandler.STONE_LANTERN.get()))
        {
            return;
        }
        Random random = worldIn.getRandom();
        if (random.nextInt(10) <= 5)
        {
            BlockPos offset = state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            worldIn.setBlockAndUpdate(pos, worldIn.getBlockState(pos).setValue(MOSSY, true));
            worldIn.setBlockAndUpdate(offset, worldIn.getBlockState(offset).setValue(MOSSY, true));
        }
    }
}
