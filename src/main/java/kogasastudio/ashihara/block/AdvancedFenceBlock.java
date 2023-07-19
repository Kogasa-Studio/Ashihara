package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.building.ColumnBlock;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static kogasastudio.ashihara.block.FenceDecorationBlock.AXIS;
import static kogasastudio.ashihara.block.FenceDecorationBlock.ORB;
import static kogasastudio.ashihara.block.FenceExpansionBlock.FACING;

public class AdvancedFenceBlock extends Block implements IVariable<AshiharaWoodTypes>
{
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final EnumProperty<ColumnType> COLUMN = EnumProperty.create("column", ColumnType.class);
    public static AshiharaWoodTypes type;

    public AdvancedFenceBlock(AshiharaWoodTypes typeIn)
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(0.5F)
                                // todo tag .harvestTool(ToolType.AXE)
                                .sound(SoundType.WOOD)
                );
        this.registerDefaultState(this.getStateDefinition().any().setValue(COLUMN, ColumnType.CORE));
        type = typeIn;
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH).add(SOUTH).add(WEST).add(EAST).add(COLUMN);
    }

    private boolean canConnect(Level world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos);
        BlockState fromState = world.getBlockState(pos.relative(direction));

        boolean expanded = false;
        if (state.getBlock() instanceof AdvancedFenceBlock && fromState.getBlock() instanceof FenceExpansionBlock)
        {
            if (((FenceExpansionBlock) fromState.getBlock()).getType().equals(((AdvancedFenceBlock) state.getBlock()).getType()))
            {
                expanded = fromState.getValue(FACING).equals(direction);
            }
        }

        return fromState.isFaceSturdy(world, pos.relative(direction), direction.getOpposite()) || expanded;
    }

    protected Block getExpansion()
    {
        return Blocks.AIR;
    }

    public BlockState updateState(Level worldIn, BlockPos pos)
    {
        BlockState init = this.defaultBlockState();

        BlockState n = worldIn.getBlockState(pos.north());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState up = worldIn.getBlockState(pos.above());

        boolean nC = n.is(init.getBlock());
        boolean sC = s.is(init.getBlock());
        boolean wC = w.is(init.getBlock());
        boolean eC = e.is(init.getBlock());

        boolean nS = canConnect(worldIn, pos, Direction.NORTH) || nC;
        boolean sS = canConnect(worldIn, pos, Direction.SOUTH) || sC;
        boolean wS = canConnect(worldIn, pos, Direction.WEST) || wC;
        boolean eS = canConnect(worldIn, pos, Direction.EAST) || eC;

        int connected = 0;
        connected += nS ? 1 : 0;
        connected += sS ? 1 : 0;
        connected += wS ? 1 : 0;
        connected += eS ? 1 : 0;

        if
        (
                connected < 2
                        || ((nS && wS && !sS && !eS) || (nS && eS && !sS && !wS) || (sS && wS && !nS && !eS) || (sS && eS && !nS && !wS))
                        || (worldIn.getBlockState(pos.above()).isFaceSturdy(worldIn, pos.above(), Direction.DOWN))
                        || (up.getBlock() instanceof AdvancedFenceBlock)
                        || (up.is(BlockRegistryHandler.GOLD_FENCE_DECORATION.get()) && up.getValue(ORB))
        )
        {
            init = init.setValue(COLUMN, ColumnType.CORE);
        } else if
        (
                (
                        (n.is(init.getBlock()) && (n.getValue(COLUMN).equals(ColumnType.CORE) || n.getValue(COLUMN).equals(ColumnType.MID)))
                                && (s.is(init.getBlock()) && (s.getValue(COLUMN).equals(ColumnType.CORE) || s.getValue(COLUMN).equals(ColumnType.MID)))
                ) ||
                        (
                                (w.is(init.getBlock()) && (w.getValue(COLUMN).equals(ColumnType.CORE) || w.getValue(COLUMN).equals(ColumnType.MID)))
                                        && (e.is(init.getBlock()) && (e.getValue(COLUMN).equals(ColumnType.CORE) || e.getValue(COLUMN).equals(ColumnType.MID)))
                        )
        )
        {
            init = init.setValue(COLUMN, ColumnType.SHORT);
        } else init = init.setValue(COLUMN, ColumnType.MID);

        init = init.setValue(NORTH, nS).setValue(SOUTH, sS).setValue(WEST, wS).setValue(EAST, eS);

        return init;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState updated = this.updateState(worldIn, pos);
        if (!updated.equals(state)) worldIn.setBlockAndUpdate(pos, updated);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.updateState(worldIn, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getItem().equals(Items.GOLD_INGOT))
        {
            if (hit.getDirection().equals(Direction.UP) && worldIn.getBlockState(pos.above()).isAir())
            {
                BlockState deco = BlockRegistryHandler.GOLD_FENCE_DECORATION.get().defaultBlockState();

                if (state.getValue(COLUMN).equals(ColumnType.CORE)) deco = deco.setValue(ORB, true);
                else if (state.getValue(NORTH) && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST)) deco = deco.setValue(AXIS, Direction.Axis.Y);
                else if (state.getValue(NORTH) && state.getValue(SOUTH)) deco = deco.setValue(AXIS, Direction.Axis.Z);
                else if (state.getValue(EAST) && state.getValue(WEST)) deco = deco.setValue(AXIS, Direction.Axis.X);

                worldIn.setBlockAndUpdate(pos.above(), deco);
                worldIn.playSound(player, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getItemInHand(handIn).shrink(1);

                return InteractionResult.SUCCESS;
            }
        } else if
        (
                stack.getItem() instanceof BlockItem
                        && ((BlockItem) stack.getItem()).getBlock() instanceof ColumnBlock)
                        //&& ((AdvancedFenceBlock) state.getBlock()).getType().equals(((ColumnBlock) ((BlockItem) stack.getItem()).getBlock()).getType())
        {
            if (!state.getValue(COLUMN).equals(ColumnType.CORE)) state = state.setValue(COLUMN, ColumnType.CORE);
            worldIn.setBlockAndUpdate(pos, state);
            worldIn.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        } else if
        (
                stack.getItem() instanceof AxeItem
                        && state.getValue(COLUMN).equals(ColumnType.CORE)
                        && !((worldIn.getBlockState(pos.above()).isFaceSturdy(worldIn, pos.above(), Direction.DOWN))
                        || (worldIn.getBlockState(pos.above()).getBlock() instanceof AdvancedFenceBlock)
                        || (worldIn.getBlockState(pos.above()).is(BlockRegistryHandler.GOLD_FENCE_DECORATION.get()) && worldIn.getBlockState(pos.above()).getValue(ORB)))
        )
        {
            worldIn.setBlockAndUpdate(pos, this.updateState(worldIn, pos));
            worldIn.playSound(player, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        } else if (!state.getValue(COLUMN).equals(ColumnType.SHORT) && stack.getItem().equals(Items.STICK) && (player.isCreative() || stack.getCount() >= 3))
        {
            if (!(this.getExpansion() instanceof FenceExpansionBlock)) return InteractionResult.PASS;
            Direction direction = hit.getDirection();
            if (direction.getAxis().isHorizontal() && worldIn.getBlockState(pos.relative(direction)).isAir())
            {
                BlockState exp = this.getExpansion().defaultBlockState();

                worldIn.setBlockAndUpdate(pos.relative(direction), exp.setValue(FACING, direction));
                worldIn.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getItemInHand(handIn).shrink(3);

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape part_N_DOWN = box(5.0d, 0.0d, 0.0d, 11.0d, 3.0d, 8.0d);
        VoxelShape part_N_MID = box(5.0d, 6.0d, 0.0d, 11.0d, 9.0d, 8.0d);
        VoxelShape part_N_UP = box(6.5d, 11.5d, 0.0d, 9.5d, 14.5d, 8.0d);
        VoxelShape part_W_DOWN = box(0.0d, 0.0d, 5.0d, 8.0d, 3.0d, 11.0d);
        VoxelShape part_W_MID = box(0.0d, 6.0d, 5.0d, 8.0d, 9.0d, 11.0d);
        VoxelShape part_W_UP = box(0.0d, 11.5d, 6.5d, 8.0d, 14.5d, 9.5d);
        VoxelShape part_S_DOWN = box(5.0d, 0.0d, 8.0d, 11.0d, 3.0d, 16.0d);
        VoxelShape part_S_MID = box(5.0d, 6.0d, 8.0d, 11.0d, 9.0d, 16.0d);
        VoxelShape part_S_UP = box(6.5d, 11.5d, 8.0d, 9.5d, 13.5d, 16.0d);
        VoxelShape part_E_DOWN = box(8.0d, 0.0d, 5.0d, 16.0d, 3.0d, 11.0d);
        VoxelShape part_E_MID = box(8.0d, 6.0d, 5.0d, 16.0d, 9.0d, 11.0d);
        VoxelShape part_E_UP = box(8.0d, 11.5d, 6.5d, 16.0d, 14.5d, 9.5d);

        VoxelShape part_N = Shapes.or(part_N_UP, part_N_MID, part_N_DOWN);
        VoxelShape part_S = Shapes.or(part_S_UP, part_S_MID, part_S_DOWN);
        VoxelShape part_W = Shapes.or(part_W_UP, part_W_MID, part_W_DOWN);
        VoxelShape part_E = Shapes.or(part_E_UP, part_E_MID, part_E_DOWN);

        VoxelShape column_core = box(4.0d, 0.0d, 4.0d, 12.0d, 16.0d, 12.0d);
        VoxelShape column_mid = box(6.0d, 0.0d, 6.0d, 10.0d, 13.5d, 10.0d);
        VoxelShape column_short = box(6.0d, 0.0d, 6.0d, 10.0d, 7.0d, 10.0d);

        VoxelShape column;

        switch (state.getValue(COLUMN))
        {
            case MID:
                column = column_mid;
                break;
            case SHORT:
                column = column_short;
                break;
            default:
                column = column_core;
        }

        if (state.getValue(NORTH)) column = Shapes.or(column, part_N);
        if (state.getValue(SOUTH)) column = Shapes.or(column, part_S);
        if (state.getValue(WEST)) column = Shapes.or(column, part_W);
        if (state.getValue(EAST)) column = Shapes.or(column, part_E);

        return column;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return 5;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return true;
    }

    public enum ColumnType implements StringRepresentable
    {
        CORE("core"),
        MID("mid"),
        SHORT("short");

        private final String name;

        ColumnType(String name)
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
