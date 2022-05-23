package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;
import static net.minecraft.item.Items.GLASS_PANE;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockStoneLantern extends BlockDoubleLantern
{
    public BlockStoneLantern()
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

    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty MOSSY = BooleanProperty.create("mossy");

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SEALED, MOSSY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT) && stateIn.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.125D;
            double d2 = (double)pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(HALF) != DoubleBlockHalf.LOWER)
            {
                worldIn.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
            }
            else if (state.getValue(LIT) && state.getValue(WATERLOGGED) && !state.getValue(SEALED))
            {
                worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        }
        else if (doubleblockhalf == DoubleBlockHalf.LOWER)
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (player.getItemInHand(handIn).getItem() == Items.AIR && state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            if (player.isShiftKeyDown() && state.getValue(SEALED))
            {
                worldIn.playSound(player, pos, SoundEvents.GLASS_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, state.setValue(SEALED, false));
                if (!player.isCreative()) player.setItemInHand(handIn, new ItemStack(GLASS_PANE));
                return ActionResultType.SUCCESS;
            }
            else if (!state.getValue(WATERLOGGED) || state.getValue(SEALED))
            {
                Random random = worldIn.getRandom();
                Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
                worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockAndUpdate(pos, state.setValue(LIT, !instantState));
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else if (player.getItemInHand(handIn).getItem().equals(GLASS_PANE) && state.getValue(HALF).equals(DoubleBlockHalf.UPPER) && !state.getValue(SEALED))
        {
            worldIn.playSound(player, pos, SoundEvents.GLASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockAndUpdate(pos, state.setValue(SEALED, true));
            if (!player.isCreative()) player.getItemInHand(handIn).shrink(1);
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape v1 = Block.box(2.0d, 0.0d, 2.0d, 14.0d, 8.0d, 14.0d);
        VoxelShape v2 = Block.box(3.0d, 8.0d, 3.0d, 13.0d, 16.0d, 13.0d);
        VoxelShape LOWER = VoxelShapes.or(v1, v2);
        VoxelShape v3 = Block.box(5.0d, 0.0d, 5.0d, 11.0d, 5.0d, 11.0d);
        VoxelShape v4 = Block.box(2.0d, 5.0d, 2.0d, 14.0d, 11.25d, 14.0d);
        VoxelShape v5 = Block.box(7.0d, 11.25d, 7.0d, 9.0d, 13.25d, 9.0d);
        VoxelShape UPPER = VoxelShapes.or(v3, v4, v5);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {return LOWER;}
        else {return UPPER;}
    }

    @Override
    public void handleRain(World worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.is(BlockRegistryHandler.STONE_LANTERN.get())) return;
        Random random = worldIn.getRandom();
        if (random.nextInt(10) <= 5)
        {
            BlockPos offset = state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            worldIn.setBlockAndUpdate(pos, worldIn.getBlockState(pos).setValue(MOSSY, true));
            worldIn.setBlockAndUpdate(offset, worldIn.getBlockState(offset).setValue(MOSSY, true));
        }
    }
}
