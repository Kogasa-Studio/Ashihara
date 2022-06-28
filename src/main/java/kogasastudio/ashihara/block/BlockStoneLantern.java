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
        this.setDefaultState(this.getDefaultState().with(SEALED, false).with(MOSSY, false));
    }

    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty MOSSY = BooleanProperty.create("mossy");

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(SEALED, MOSSY);
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
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.down();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.get(HALF) != DoubleBlockHalf.LOWER)
            {
                worldIn.setBlockState(pos, state.get(WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(), 35);
            }
            else if (state.get(LIT) && state.get(WATERLOGGED) && !state.get(SEALED))
            {
                worldIn.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, state.with(LIT, false));
            }
        }
        else if (doubleblockhalf == DoubleBlockHalf.LOWER)
        {
            BlockPos blockpos = pos.up();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != state.getBlock() || blockstate.get(HALF) != DoubleBlockHalf.UPPER)
            {
                worldIn.setBlockState(pos, state.get(WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(), 35);
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (player.getHeldItem(handIn).getItem() == Items.AIR && state.get(HALF) == DoubleBlockHalf.UPPER)
        {
            if (player.isSneaking() && state.get(SEALED))
            {
                worldIn.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, state.with(SEALED, false));
                if (!player.isCreative()) player.setHeldItem(handIn, new ItemStack(GLASS_PANE));
                return ActionResultType.SUCCESS;
            }
            else if (!state.get(WATERLOGGED) || state.get(SEALED))
            {
                Random random = worldIn.getRandom();
                Boolean instantState = worldIn.getBlockState(pos).get(LIT);
                worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockState(pos, state.with(LIT, !instantState));
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else if (player.getHeldItem(handIn).getItem().equals(GLASS_PANE) && state.get(HALF).equals(DoubleBlockHalf.UPPER) && !state.get(SEALED))
        {
            worldIn.playSound(player, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos, state.with(SEALED, true));
            if (!player.isCreative()) player.getHeldItem(handIn).shrink(1);
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
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

    @Override
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.matchesBlock(BlockRegistryHandler.STONE_LANTERN.get())) return;
        Random random = worldIn.getRandom();
        if (random.nextInt(10) <= 5)
        {
            BlockPos offset = state.get(HALF).equals(DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
            worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(MOSSY, true));
            worldIn.setBlockState(offset, worldIn.getBlockState(offset).with(MOSSY, true));
        }
    }
}
