package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.block.blockentity.FermentationSubBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LargeFermentationVatBlock extends FermentationBlock
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty IS_ORIGIN = BooleanProperty.create("is_origin");

    private static final VoxelShape BASE_SHAPE = Shapes.or(
        Shapes.box(-0.9375, 0, 0.0625, 0.9375, 1.75, 1.9375),
        Shapes.box(-1, 1.75, 0, 1, 1.875, 2),
        Shapes.box(-0.984375, 1.875, 1.375, 0.984375, 2, 1.625),
        Shapes.box(-0.984375, 1.875, 0.375, 0.984375, 2, 0.625),
        Shapes.box(0.5625, 1.875, 0.84375, 0.875, 2, 1.15625)
    );

    private static final VoxelShape BASE_NO_LID = Shapes.or(
        Shapes.box(-0.9375, 0, 0.0625, 0.9375, 1.75, 0.1875),
        Shapes.box(-0.9375, 0, 1.8125, 0.9375, 1.75, 1.9375),
        Shapes.box(-0.9375, 0, 0.1875, -0.8125, 1.75, 1.8125),
        Shapes.box(0.8125, 0, 0.1875, 0.9375, 1.75, 1.8125),
        Shapes.box(-0.8125, 0.0625, 0.1875, 0.8125, 0.125, 1.8125)
    );

    private static final int[] X_SIGN = {1, 1, -1, -1};
    private static final int[] Z_SIGN = {-1, 1, 1, -1};
    private static final int[] ROT_DEG = {180, 270, 0, 90};

    static final VoxelShape[][] SLICE_CACHE = new VoxelShape[8][8];

    static
    {
        VoxelShape[] bases = {BASE_NO_LID, BASE_SHAPE};
        for (int lid = 0; lid < 2; lid++)
        {
            for (int dir = 0; dir < 4; dir++)
            {
                VoxelShape rotated = ShapeHelper.rotateShape(bases[lid], Direction.Axis.Y, 0, 1.0, ROT_DEG[dir]);
                VoxelShape placed = rotated.move(1, 0, 0);
                int idx = lid * 4 + dir;
                for (int y = 0; y < 2; y++)
                {
                    for (int x = 0; x < 2; x++)
                    {
                        for (int z = 0; z < 2; z++)
                        {
                            SLICE_CACHE[idx][y * 4 + x * 2 + z] = ShapeHelper.sliceShape(placed, 2, new Vec3i(x, y, z));
                        }
                    }
                }
            }
        }
    }

    public LargeFermentationVatBlock(BlockBehaviour.Properties properties)
    {
        super(properties, FermentationBlockEntity.Size.LARGE_VAT);
        registerDefaultState(stateDefinition.any()
            .setValue(HAS_LID, true).setValue(FACING, Direction.NORTH).setValue(IS_ORIGIN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, IS_ORIGIN);
    }

    // --------------------------------------------------
    // Sub-block traversal
    // --------------------------------------------------

    @FunctionalInterface
    private interface SubBlockAction
    {
        void accept(BlockPos target, int ix, int y, int iz, int dirIdx);
    }

    private void forEachSubBlock(BlockPos origin, int dirIdx, SubBlockAction action)
    {
        int sx = X_SIGN[dirIdx], sz = Z_SIGN[dirIdx];
        for (int y = 0; y < 2; y++)
        {
            for (int ix = 0; ix < 2; ix++)
            {
                for (int iz = 0; iz < 2; iz++)
                {
                    action.accept(origin.offset(sx * ix, y, sz * iz), ix, y, iz, dirIdx);
                }
            }
        }
    }

    private static int cacheSubIdx(int ix, int y, int iz, int dirIdx)
    {
        int cx = (dirIdx >= 2) ? 1 - ix : ix;
        int cz = (dirIdx == 0 || dirIdx == 3) ? 1 - iz : iz;
        return y * 4 + cx * 2 + cz;
    }

    // --------------------------------------------------
    // Interaction
    // --------------------------------------------------

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hit.getDirection() != Direction.UP || !stack.isEmpty()) return InteractionResult.PASS;

        BlockPos origin = getOrigin(level, pos);
        if (origin == null) return InteractionResult.PASS;

        BlockState originState = level.getBlockState(origin);
        boolean newLid = !originState.getValue(HAS_LID);
        int d = originState.getValue(FACING).get2DDataValue();

        level.playSound(player, origin, newLid ? SoundEvents.BARREL_CLOSE : SoundEvents.BARREL_OPEN,
            SoundSource.BLOCKS, 1.0F, 1.0F);

        int cacheRow = (newLid ? 1 : 0) * 4 + d;
        forEachSubBlock(origin, d, (target, ix, y, iz, dir) ->
        {
            BlockState targetState = level.getBlockState(target);
            level.setBlock(target, targetState.setValue(HAS_LID, newLid), Block.UPDATE_CLIENTS);

            if (!targetState.getValue(IS_ORIGIN))
            {
                BlockEntity be = level.getBlockEntity(target);
                if (be instanceof FermentationSubBlockEntity sub)
                {
                    int si = cacheSubIdx(ix, y, iz, dir);
                    sub.cacheShape(SLICE_CACHE[cacheRow][si], si);
                }
            }
        });

        return InteractionResult.SUCCESS;
    }

    // --------------------------------------------------
    // Placement
    // --------------------------------------------------

    private boolean canPlaceMulti(Level level, BlockPos origin, int d)
    {
        boolean[] ok = {true};
        forEachSubBlock(origin, d, (target, ix, y, iz, dir) ->
        {
            if (!level.getBlockState(target).canBeReplaced()) ok[0] = false;
        });
        return ok[0];
    }

    @Override @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        int d = ctx.getHorizontalDirection().getOpposite().get2DDataValue();
        return canPlaceMulti(ctx.getLevel(), ctx.getClickedPos(), d)
            ? defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(IS_ORIGIN, true)
            : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        Direction facing = state.getValue(FACING);
        boolean lid = state.getValue(HAS_LID);
        int d = facing.get2DDataValue();
        int cacheRow = (lid ? 1 : 0) * 4 + d;

        forEachSubBlock(pos, d, (target, ix, y, iz, dir) ->
        {
            if (ix == 0 && iz == 0 && y == 0) return;
            level.setBlockAndUpdate(target, defaultBlockState()
                .setValue(HAS_LID, lid).setValue(FACING, facing).setValue(IS_ORIGIN, false));

            BlockEntity be = level.getBlockEntity(target);
            if (be instanceof FermentationSubBlockEntity sub)
            {
                sub.setMainPos(pos);
                int si = cacheSubIdx(ix, y, iz, dir);
                sub.cacheShape(SLICE_CACHE[cacheRow][si], si);
            }
        });
    }

    // --------------------------------------------------
    // Destruction
    // --------------------------------------------------

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        BlockPos origin = getOrigin(level, pos);
        if (origin != null) multiDestroy(level, origin, state, player);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion e,
        java.util.function.BiConsumer<ItemStack, BlockPos> dc)
    {
        BlockPos origin = getOrigin(level, pos);
        if (origin != null) multiDestroy(level, origin, state, null);
        super.onExplosionHit(state, level, pos, e, dc);
    }

    private void multiDestroy(Level level, BlockPos origin, BlockState state, @Nullable Player player)
    {
        if (level.isClientSide()) return;
        int d = state.getValue(FACING).get2DDataValue();
        boolean drop = player == null || !player.isCreative();
        forEachSubBlock(origin, d, (target, ix, y, iz, dir) ->
        {
            level.destroyBlock(target, drop);
        });
    }

    // --------------------------------------------------
    // Origin / shape
    // --------------------------------------------------

    @Nullable
    private BlockPos getOrigin(BlockGetter level, BlockPos pos)
    {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FermentationBlockEntity) return pos;
        if (be instanceof FermentationSubBlockEntity sub) return sub.getMainPos();
        return null;
    }

    @Nullable
    public static FermentationBlockEntity getBE(Level level, BlockPos clickPos, BlockState clickState)
    {
        if (!(clickState.getBlock() instanceof LargeFermentationVatBlock fb)) return null;
        BlockPos origin = fb.getOrigin(level, clickPos);
        return origin != null ? level.getBlockEntity(origin) instanceof FermentationBlockEntity fbe ? fbe : null : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FermentationBlockEntity)
        {
            int d = state.getValue(FACING).get2DDataValue();
            int lid = state.getValue(HAS_LID) ? 1 : 0;
            int cx = (d >= 2) ? 1 : 0;
            int cz = (d == 0 || d == 3) ? 1 : 0;
            return SLICE_CACHE[lid * 4 + d][cx * 2 + cz];
        }
        if (be instanceof FermentationSubBlockEntity sub)
        {
            VoxelShape cached = sub.getCachedShape();
            if (cached != null) return cached;
            int si = sub.getSliceIndex();
            if (si >= 0)
            {
                int row = (state.getValue(HAS_LID) ? 1 : 0) * 4 + state.getValue(FACING).get2DDataValue();
                VoxelShape rebuilt = SLICE_CACHE[row][si];
                sub.cacheShape(rebuilt, si);
                return rebuilt;
            }
        }
        return Shapes.block();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return state.getValue(IS_ORIGIN) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getFullShape(boolean hasLid)
    {
        return hasLid ? BASE_SHAPE : BASE_NO_LID;
    }

    @Override @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return state.getValue(IS_ORIGIN)
            ? new FermentationBlockEntity(pos, state)
            : new FermentationSubBlockEntity(pos, state);
    }
}