package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static kogasastudio.ashihara.block.tileentities.TERegistryHandler.CANDLE_TE;
import static kogasastudio.ashihara.utils.EasyBlockActionHandler.getLightValueLit;

public class BlockCandle extends Block
{
    public BlockCandle()
    {
        super
        (
            AbstractBlock.Properties.create(Material.SNOW)
            .hardnessAndResistance(0.05F)
            .sound(SoundType.SNOW)
            .setLightLevel(getLightValueLit(15))
            .notSolid()
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(LIT, false).with(MULTIPLE, false));
    }

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty MULTIPLE = BooleanProperty.create("multiple");

    private CandleTE checkCandle(IBlockReader worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te.getType().equals(CANDLE_TE.get())) return (CandleTE) te;
        else return null;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
    {
        if (state.get(MULTIPLE) && te instanceof CandleTE)
        {
            CandleTE candle = (CandleTE) te;
            int amount = candle.pickCandle(true, worldIn, pos);
            if (amount == 0) return;

            ItemStack candleItem = new ItemStack(ItemRegistryHandler.CANDLE.get(), amount);
            ItemEntity entity = new ItemEntity(worldIn, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, candleItem);
            entity.setDefaultPickupDelay();
            worldIn.addEntity(entity);
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, MULTIPLE);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(player.getHeldItem(handIn).isEmpty())
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (player.isSneaking() && te != null)
            {
                int amount = te.pickCandle(false, worldIn, pos);
                if (amount == 0) return ActionResultType.PASS;
                player.setHeldItem(handIn, new ItemStack(ItemRegistryHandler.CANDLE.get(), amount));
                return ActionResultType.SUCCESS;
            }
            Random random = worldIn.getRandom();
            Boolean instantState = worldIn.getBlockState(pos).get(LIT);
            worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockState(pos, state.with(LIT, !instantState));
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT))
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (stateIn.get(MULTIPLE) && te != null)
            {
                for (double[] d : te.getPosList())
                {
                    double x = d[0];
                    double z = d[1];
                    double y = d[2];
                    worldIn.addParticle(ParticleTypes.FLAME, pos.getX() + x, pos.getY() + 1.075d + y, pos.getZ() + z, 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                double d0 = (double)pos.getX() + 0.5D;
                double d1 = (double)pos.getY() + 1.075D;
                double d2 = (double)pos.getZ() + 0.5D;
                worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getBlock() != Blocks.AIR;
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(MULTIPLE))
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (te != null)
            {
                VoxelShape shape = VoxelShapes.empty();
                for (double[] d : te.getPosList())
                {
                    shape = VoxelShapes.or(shape, makeCuboidShape(16 * d[0] - 1.3d, 0d, 16 * d[1] - 1.3d, 16 * d[0] + 1.3d, 16 * d[2] + 14d, 16 * d[1] + 1.3d));
                }
                return shape;
            }
        }
        return makeCuboidShape(6.7d, 0.0d, 6.7d, 9.3d, 14.0d, 9.3d);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return state.get(MULTIPLE);}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new CandleTE();}
}
