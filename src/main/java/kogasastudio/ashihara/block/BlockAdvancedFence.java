package kogasastudio.ashihara.block;

import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import static kogasastudio.ashihara.block.BlockFenceDecoration.AXIS;
import static kogasastudio.ashihara.block.BlockFenceDecoration.ORB;
import static kogasastudio.ashihara.block.BlockFenceExpansion.FACING;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockAdvancedFence extends Block implements IVariable<AshiharaWoodTypes>
{
    public BlockAdvancedFence(AshiharaWoodTypes typeIn)
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(0.5F)
            .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(COLUMN, ColumnType.CORE));
        type = typeIn;
    }

    public static AshiharaWoodTypes type;

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final EnumProperty<ColumnType> COLUMN = EnumProperty.create("column", ColumnType.class);

    @Override
    public AshiharaWoodTypes getType() {return type;}

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH).add(SOUTH).add(WEST).add(EAST).add(COLUMN);
    }

    private boolean canConnect(World world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos);
        BlockState fromState = world.getBlockState(pos.relative(direction));

        boolean expanded = false;
        if (state.getBlock() instanceof BlockAdvancedFence && fromState.getBlock() instanceof BlockFenceExpansion)
        {
            if (((BlockFenceExpansion) fromState.getBlock()).getType().equals(((BlockAdvancedFence) state.getBlock()).getType()))
            {
                expanded = fromState.getValue(FACING).equals(direction);
            }
        }

        return fromState.isFaceSturdy(world, pos.relative(direction), direction.getOpposite()) || expanded;
    }

    protected Block getExpansion() {return Blocks.AIR;}

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState up = worldIn.getBlockState(pos.above());

        boolean nC = n.is(state.getBlock());
        boolean sC = s.is(state.getBlock());
        boolean wC = w.is(state.getBlock());
        boolean eC = e.is(state.getBlock());

        boolean nS = canConnect(worldIn, pos, Direction.NORTH) || nC;
        boolean sS = canConnect(worldIn, pos, Direction.SOUTH) || sC;
        boolean wS = canConnect(worldIn, pos, Direction.WEST) || wC;
        boolean eS = canConnect(worldIn, pos, Direction.EAST) || eC;

        int connected = 0;
        connected += nS ? 0 : 1;
        connected += sS ? 0 : 1;
        connected += wS ? 0 : 1;
        connected += eS ? 0 : 1;

        BlockState init = this.defaultBlockState();

        if
        (
            (worldIn.getBlockState(pos.above()).isFaceSturdy(worldIn, pos.above(), Direction.DOWN))
            || (up.getBlock() instanceof BlockAdvancedFence
                && ((BlockAdvancedFence) up.getBlock()).getType().equals(((BlockAdvancedFence) state.getBlock()).getType()))
            || (up.getBlock().is(BlockRegistryHandler.GOLD_FENCE_DECORATION.get()) && up.getValue(ORB))
            || (connected != 2)
            || ((nS && (wS || eS)) || (sS && (wS || eS)))
        )
        {
            init = init.setValue(COLUMN, ColumnType.CORE);
        }
        else if
        (
            (
                (n.is(state.getBlock()) && (n.getValue(COLUMN).equals(ColumnType.CORE) || n.getValue(COLUMN).equals(ColumnType.MID)))
                && (s.is(state.getBlock()) && (s.getValue(COLUMN).equals(ColumnType.CORE) || s.getValue(COLUMN).equals(ColumnType.MID)))
            ) ||
            (
                (w.is(state.getBlock()) && (w.getValue(COLUMN).equals(ColumnType.CORE) || w.getValue(COLUMN).equals(ColumnType.MID)))
                && (e.is(state.getBlock()) && (e.getValue(COLUMN).equals(ColumnType.CORE) || e.getValue(COLUMN).equals(ColumnType.MID)))
            )
        )
        {
            init = init.setValue(COLUMN, ColumnType.SHORT);
        }
        else if
        (
            (
                (n.is(state.getBlock()) && n.getValue(COLUMN).equals(ColumnType.SHORT))
                && (s.is(state.getBlock()) && s.getValue(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (w.is(state.getBlock()) && w.getValue(COLUMN).equals(ColumnType.SHORT))
                && (e.is(state.getBlock()) && e.getValue(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (n.is(state.getBlock()) && n.getValue(COLUMN).equals(ColumnType.SHORT))
                && (s.is(state.getBlock()) && s.getValue(COLUMN).equals(ColumnType.MID))
            ) ||
            (
                (n.is(state.getBlock()) && n.getValue(COLUMN).equals(ColumnType.MID))
                && (s.is(state.getBlock()) && s.getValue(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (w.is(state.getBlock()) && w.getValue(COLUMN).equals(ColumnType.SHORT))
                && (e.is(state.getBlock()) && e.getValue(COLUMN).equals(ColumnType.MID))
            ) ||
            (
                (w.is(state.getBlock()) && w.getValue(COLUMN).equals(ColumnType.MID))
                && (e.is(state.getBlock()) && e.getValue(COLUMN).equals(ColumnType.SHORT))
            )
        )
        {
            init = init.setValue(COLUMN, ColumnType.MID);
        }
        else init = init.setValue(COLUMN, ColumnType.MID);

        init = init.setValue(NORTH, nS).setValue(SOUTH, sS).setValue(WEST, wS).setValue(EAST, eS);

        worldIn.setBlockAndUpdate(pos, init);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState init = this.defaultBlockState();

        BlockState n = worldIn.getBlockState(pos.north());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        BlockState e = worldIn.getBlockState(pos.east());

        boolean nC = n.is(init.getBlock());
        boolean sC = s.is(init.getBlock());
        boolean wC = w.is(init.getBlock());
        boolean eC = e.is(init.getBlock());

        boolean nS = n.isFaceSturdy(worldIn, pos.north(), Direction.SOUTH) || nC;
        boolean sS = s.isFaceSturdy(worldIn, pos.south(), Direction.NORTH) || sC;
        boolean wS = w.isFaceSturdy(worldIn, pos.west(), Direction.EAST) || wC;
        boolean eS = e.isFaceSturdy(worldIn, pos.east(), Direction.WEST) || eC;

        int connected = 0;
        connected += nS ? 0 : 1;
        connected += sS ? 0 : 1;
        connected += wS ? 0 : 1;
        connected += eS ? 0 : 1;

        if
        (
            (worldIn.getBlockState(pos.above()).isFaceSturdy(worldIn, pos.above(), Direction.DOWN))
            || (connected != 2)
            || ((nS && (wS || eS)) || (sS && (wS || eS)))
        )
        {
            init = init.setValue(COLUMN, ColumnType.CORE);
        }
        else if
        (
            (
                (nC && (n.getValue(COLUMN).equals(ColumnType.CORE)))
                && (sC && (s.getValue(COLUMN).equals(ColumnType.CORE)))
            ) ||
            (
                (wC && (w.getValue(COLUMN).equals(ColumnType.CORE)))
                && (eC && (e.getValue(COLUMN).equals(ColumnType.CORE)))
            )
        )
        {
            BlockState nF = worldIn.getBlockState(pos.north(2));
            BlockState sF = worldIn.getBlockState(pos.south(2));
            BlockState wF = worldIn.getBlockState(pos.west(2));
            BlockState eF = worldIn.getBlockState(pos.east(2));

            if
            (
                (
                    (nF.is(init.getBlock()) && (nF.getValue(COLUMN).equals(ColumnType.CORE) || nF.getValue(COLUMN).equals(ColumnType.MID)))
                    && (sF.is(init.getBlock()) && (sF.getValue(COLUMN).equals(ColumnType.CORE) || sF.getValue(COLUMN).equals(ColumnType.MID)))
                ) ||
                (
                    (wF.is(init.getBlock()) && (wF.getValue(COLUMN).equals(ColumnType.CORE) || wF.getValue(COLUMN).equals(ColumnType.MID)))
                    && (eF.is(init.getBlock()) && (eF.getValue(COLUMN).equals(ColumnType.CORE) || eF.getValue(COLUMN).equals(ColumnType.MID)))
                )
            )
            {
                init = init.setValue(COLUMN, ColumnType.MID);
            }
        }
        else init = init.setValue(COLUMN, ColumnType.CORE);

        init = init.setValue(NORTH, nS).setValue(SOUTH, sS).setValue(WEST, wS).setValue(EAST, eS);

        return init;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getItem().equals(Items.GOLD_INGOT))
        {
            if (hit.getDirection().equals(Direction.UP) && worldIn.getBlockState(pos.above()).isAir())
            {
                BlockState deco = BlockRegistryHandler.GOLD_FENCE_DECORATION.get().defaultBlockState();

                if (state.getValue(COLUMN).equals(ColumnType.CORE)) deco = deco.setValue(ORB, true);
                else if (state.getValue(NORTH) && state.getValue(SOUTH)) deco = deco.setValue(AXIS, Direction.Axis.Z);
                else if (state.getValue(EAST) && state.getValue(WEST)) deco = deco.setValue(AXIS, Direction.Axis.X);

                worldIn.setBlockAndUpdate(pos.above(), deco);
                worldIn.playSound(player, pos, SoundEvents.LANTERN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getItemInHand(handIn).shrink(1);

                return ActionResultType.SUCCESS;
            }
        }
        else if (state.getValue(COLUMN).equals(ColumnType.CORE) && stack.getItem().equals(Items.STICK) && (player.isCreative() || stack.getCount() >= 2))
        {
            if (!(this.getExpansion() instanceof BlockFenceExpansion)) return ActionResultType.PASS;
            Direction direction = hit.getDirection();
            if (direction.getAxis().isHorizontal() && worldIn.getBlockState(pos.relative(direction)).isAir())
            {
                BlockState exp = this.getExpansion().defaultBlockState();

                worldIn.setBlockAndUpdate(pos.relative(direction), exp.setValue(FACING, direction));
                worldIn.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getItemInHand(handIn).shrink(2);

                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape part_N_DOWN = box(6.5d, 6.0d, 0.0d, 9.5d, 9.0, 8.0d);
        VoxelShape part_N_UP = box(6.5d, 12.0d, 0.0d, 9.5d, 15.0, 8.0d);
        VoxelShape part_W_DOWN = box(0.0d, 6.0d, 6.5d, 8.0d, 9.0, 9.5d);
        VoxelShape part_W_UP = box(0.0d, 12.0d, 6.5d, 8.0d, 15.0, 9.5d);
        VoxelShape part_S_DOWN = box(6.5d, 6.0d, 8.0d, 9.5d, 9.0, 16.0d);
        VoxelShape part_S_UP = box(6.5d, 12.0d, 8.0d, 9.5d, 15.0, 16.0d);
        VoxelShape part_E_DOWN = box(8.0d, 6.0d, 6.5d, 16.0d, 9.0, 9.5d);
        VoxelShape part_E_UP = box(8.0d, 12.0d, 6.5d, 16.0d, 15.0, 9.5d);

        VoxelShape part_N = VoxelShapes.or(part_N_UP, part_N_DOWN);
        VoxelShape part_S = VoxelShapes.or(part_S_UP, part_S_DOWN);
        VoxelShape part_W = VoxelShapes.or(part_W_UP, part_W_DOWN);
        VoxelShape part_E = VoxelShapes.or(part_E_UP, part_E_DOWN);

        VoxelShape column_core = box(6.0d, 0.0d, 6.0d, 10.0d, 16.0d, 10.0d);
        VoxelShape column_mid = box(6.0d, 0.0d, 6.0d, 10.0d, 13.5d, 10.0d);
        VoxelShape column_short = box(6.0d, 0.0d, 6.0d, 10.0d, 7.5d, 10.0d);

        VoxelShape column;

        switch (state.getValue(COLUMN))
        {
            case MID: column = column_mid;break;
            case SHORT: column = column_short;break;
            default: column = column_core;
        }

        if (state.getValue(NORTH)) column = VoxelShapes.or(column, part_N);
        if (state.getValue(SOUTH)) column = VoxelShapes.or(column, part_S);
        if (state.getValue(WEST)) column = VoxelShapes.or(column, part_W);
        if (state.getValue(EAST)) column = VoxelShapes.or(column, part_E);

        return column;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return 5;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return true;}

    public enum ColumnType implements IStringSerializable
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
        public String getSerializedName() {return this.name;}
    }
}
