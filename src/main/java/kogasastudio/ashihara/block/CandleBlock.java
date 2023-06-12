package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.CandleTE;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static kogasastudio.ashihara.block.tileentities.TERegistryHandler.CANDLE_TE;
import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class CandleBlock extends Block implements EntityBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty MULTIPLE = BooleanProperty.create("multiple");

    public CandleBlock()
    {
        super
                (
                        Properties.of(Material.TOP_SNOW)
                                .strength(0.05F)
                                .sound(SoundType.SNOW)
                                .lightLevel(getLightValueLit(15))
                                .noOcclusion()
                );
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, false).setValue(MULTIPLE, false));
    }

    private CandleTE checkCandle(BlockGetter worldIn, BlockPos pos)
    {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te != null && te.getType().equals(CANDLE_TE.get())) return (CandleTE) te;
        else return null;
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack)
    {
        if (state.getValue(MULTIPLE) && te instanceof CandleTE)
        {
            CandleTE candle = (CandleTE) te;
            int amount = candle.pickCandle(true, worldIn, pos);
            if (amount == 0) return;

            ItemStack candleItem = new ItemStack(ItemRegistryHandler.CANDLE.get(), amount);
            ItemEntity entity = new ItemEntity(worldIn, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, candleItem);
            entity.setDefaultPickUpDelay();
            worldIn.addFreshEntity(entity);
        }
        super.playerDestroy(worldIn, player, pos, state, te, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, MULTIPLE);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (player.getItemInHand(handIn).isEmpty())
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (player.isShiftKeyDown() && te != null)
            {
                int amount = te.pickCandle(false, worldIn, pos);
                if (amount == 0) return InteractionResult.PASS;
                player.setItemInHand(handIn, new ItemStack(ItemRegistryHandler.CANDLE.get(), amount));
                return InteractionResult.SUCCESS;
            }
            Random random = worldIn.getRandom();
            Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, !instantState));
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (stateIn.getValue(MULTIPLE) && te != null)
            {
                for (double[] d : te.getPosList())
                {
                    double x = d[0];
                    double z = d[1];
                    double y = d[2];
                    worldIn.addParticle(ParticleTypes.FLAME, pos.getX() + x, pos.getY() + 1.075d + y, pos.getZ() + z, 0.0D, 0.0D, 0.0D);
                }
            } else
            {
                double d0 = (double) pos.getX() + 0.5D;
                double d1 = (double) pos.getY() + 1.075D;
                double d2 = (double) pos.getZ() + 0.5D;
                worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).getBlock() != Blocks.AIR;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(MULTIPLE))
        {
            CandleTE te = checkCandle(worldIn, pos);
            if (te != null)
            {
                VoxelShape shape = Shapes.empty();
                for (double[] d : te.getPosList())
                {
                    shape = Shapes.or(shape, box(16 * d[0] - 1.3d, 0d, 16 * d[1] - 1.3d, 16 * d[0] + 1.3d, 16 * d[2] + 14d, 16 * d[1] + 1.3d));
                }
                return shape;
            }
        }
        return box(6.7d, 0.0d, 6.7d, 9.3d, 14.0d, 9.3d);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new CandleTE(pPos, pState);
    }
}
