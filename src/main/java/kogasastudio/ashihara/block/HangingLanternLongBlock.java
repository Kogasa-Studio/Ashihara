package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class HangingLanternLongBlock extends LanternBlock implements EntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public HangingLanternLongBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOL)
                                .strength(1.0F)
                                .sound(SoundType.BAMBOO_SAPLING)
                                .lightLevel(getLightValueLit(15))
                );
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape1 = Block.box(5, 0.5, 5, 11, 1.5, 11);
        VoxelShape shape2 = Block.box(4, 1.5, 4, 12, 14.5, 12);
        VoxelShape shape3 = Block.box(5, 14.5, 5, 11, 15.5, 11);
        return Shapes.or(shape1, shape2, shape3);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        if (stateIn.getValue(LIT))
        {
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + 0.5D;
            double d2 = (double) pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.above()).getBlock() != Blocks.AIR;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (player.getItemInHand(handIn).getItem() == ItemRegistryHandler.KOISHI.get())
        {
            MarkableLanternTE te = (MarkableLanternTE) worldIn.getBlockEntity(pos);
            if (te != null)
            {
                te.nextIcon();
                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        } else return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new MarkableLanternTE(pPos, pState);
    }
}
