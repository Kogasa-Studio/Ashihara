package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.DirtCookStoveBE;
import kogasastudio.ashihara.helper.InventoryHelper;
import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DirtCookStoveBlock extends Block implements SimpleWaterloggedBlock, EntityBlock
{
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");
    public static final BooleanProperty FIREWOOD = BooleanProperty.create("firewood");
    public static final BooleanProperty ASH = BooleanProperty.create("ash");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ForeHind> FORE_HIND = EnumProperty.create("fore_hind", ForeHind.class);

    public static final VoxelShape N_HIND = Shapes.or
    (
        box(0,0,0,16,8,16),
        box(0,8,0,2,16,16),
        box(2,8,14,14,16,16),
        box(14,8,0,16,16,16)
    );
    public static final VoxelShape E_HIND = ShapeHelper.rotateShape(N_HIND, 90);
    public static final VoxelShape S_HIND = ShapeHelper.rotateShape(N_HIND, 180);
    public static final VoxelShape W_HIND = ShapeHelper.rotateShape(N_HIND, 270);

    public static final VoxelShape N_FORE = Shapes.or
    (
        box(0,0,8,16,8,16),
        box(0,8,8,2,14,16),
        box(2,8,8,14,12,10),
        box(14,8,8,16,14,16)
    );
    public static final VoxelShape E_FORE = ShapeHelper.rotateShape(N_FORE, 90);
    public static final VoxelShape S_FORE = ShapeHelper.rotateShape(N_FORE, 180);
    public static final VoxelShape W_FORE = ShapeHelper.rotateShape(N_FORE, 270);

    public DirtCookStoveBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState
        (
            defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(BURNING, false)
            .setValue(FIREWOOD, false)
            .setValue(ASH, false)
            .setValue(WATERLOGGED, false)
            .setValue(FORE_HIND, ForeHind.HIND)
        );
    }

    public DirtCookStoveBlock()
    {
        this
        (
            Properties.of()
            .noOcclusion()
            .mapColor(MapColor.DIRT)
            .strength(0.5F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .lightLevel(state -> state.getValue(BURNING) ? 15 : 0)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(FORE_HIND).equals(ForeHind.FORE))
        {
            return switch (state.getValue(FACING))
            {
                case EAST -> E_FORE;
                case WEST -> W_FORE;
                case SOUTH -> S_FORE;
                default -> N_FORE;
            };
        }
        else
        {
            return switch (state.getValue(FACING))
            {
                case EAST -> E_HIND;
                case WEST -> W_HIND;
                case SOUTH -> S_HIND;
                default -> N_HIND;
            };
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, BURNING, FIREWOOD, ASH, WATERLOGGED, FORE_HIND);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        return context.getLevel().getBlockState(blockpos.relative(facing.getOpposite())).canBeReplaced(context)
        ? this.defaultBlockState().setValue(FACING, facing.getOpposite()).setValue(WATERLOGGED, context.getLevel().getFluidState(blockpos).getType().equals(Fluids.WATER))
        : null;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.setBlock
        (
            pos.relative(state.getValue(FACING)),
            this.defaultBlockState()
            .setValue(FACING, state.getValue(FACING))
            .setValue(FORE_HIND, ForeHind.FORE)
            .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType().equals(Fluids.WATER))
            , 3
        );
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston)
    {
        Direction direction = state.getValue(FORE_HIND).equals(ForeHind.FORE) ? state.getValue(FACING).getOpposite() : state.getValue(FACING);
        BlockPos toCheck = pos.relative(direction);
        BlockState blockstate = level.getBlockState(toCheck);
        if (blockstate.getBlock() != state.getBlock() || blockstate.getValue(FORE_HIND) == state.getValue(FORE_HIND))
        {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        BlockPos bePos = state.getValue(FORE_HIND).equals(ForeHind.FORE) ? pos.relative(state.getValue(FACING).getOpposite()) : pos;
        if (level.getBlockEntity(bePos) instanceof DirtCookStoveBE be)
        {
            if (itemStack.isEmpty() && state.getValue(BURNING) && player.isShiftKeyDown())
            {
                be.extinguish();
                return InteractionResult.SUCCESS;
            }
            else if (hand.equals(InteractionHand.MAIN_HAND) && itemStack.isEmpty() || (!itemStack.isEmpty() && itemStack.getBurnTime(null, level.fuelValues()) > 0))
            {
                if (InventoryHelper.interactWithInventory(be.fuelStorage, itemStack, player, hand, 64))
                {
                    be.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
            else if (itemStack.getItem() instanceof FlintAndSteelItem && be.hasFuel())
            {
                if (!player.isCreative()) itemStack.hurtAndBreak(1, player, hand);
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                be.ignite();
                return InteractionResult.SUCCESS;
            }
            if (level.isClientSide())
            {
                ItemStack fuel = be.fuelStorage.getStackInSlot(0);
                if (!fuel.isEmpty())
                {
                    player.sendOverlayMessage(Component.translatable("message.ashihara.cookstove.fuel_left").append(fuel.getDisplayName()).append(" * ").append(String.valueOf(fuel.count())));
                }
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        return blockState.getValue(FORE_HIND).equals(ForeHind.FORE) ? null : new DirtCookStoveBE(worldPosition, blockState);
    }

    @Override
    public @org.jspecify.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type)
    {
        return (lvl, pos, st, be) ->
        {
            if (be instanceof DirtCookStoveBE dirtCookStoveBE)
            {
                DirtCookStoveBE.tick(lvl, pos, st, dirtCookStoveBE);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(BURNING) && state.getValue(FORE_HIND).equals(ForeHind.HIND))
        {
            if (random.nextInt(10) == 0)
            {
                level.playLocalSound(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.CAMPFIRE_CRACKLE,
                SoundSource.BLOCKS,
                0.5F + random.nextFloat(),
                random.nextFloat() * 0.7F + 0.6F,
                false);
            }

            if (random.nextInt(5) == 0)
            {
                for (int i = 0; i < random.nextInt(1) + 1; i++)
                {
                    level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, random.nextFloat() / 2.0F, 5.0E-5, random.nextFloat() / 2.0F);
                }
            }
        }
    }

    public enum ForeHind implements StringRepresentable
    {
        FORE("fore"), HIND("hind");

        final String name;

        ForeHind(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
