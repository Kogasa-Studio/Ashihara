package kogasastudio.ashihara.block;

import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class BlockAdvancedFence extends Block implements IVariable<AshiharaWoodTypes>
{
    public BlockAdvancedFence(AshiharaWoodTypes typeIn)
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.5F)
            .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(COLUMN, ColumnType.CORE));
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH).add(SOUTH).add(WEST).add(EAST).add(COLUMN);
    }

    private boolean canConnect(World world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos);
        BlockState fromState = world.getBlockState(pos.offset(direction));

        boolean expanded = false;
        if (state.getBlock() instanceof BlockAdvancedFence && fromState.getBlock() instanceof BlockFenceExpansion)
        {
            if (((BlockFenceExpansion) fromState.getBlock()).getType().equals(((BlockAdvancedFence) state.getBlock()).getType()))
            {
                expanded = fromState.get(FACING).equals(direction);
            }
        }

        return fromState.isSolidSide(world, pos.offset(direction), direction.getOpposite()) || expanded;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState up = worldIn.getBlockState(pos.up());

        boolean nC = n.matchesBlock(state.getBlock());
        boolean sC = s.matchesBlock(state.getBlock());
        boolean wC = w.matchesBlock(state.getBlock());
        boolean eC = e.matchesBlock(state.getBlock());

        boolean nS = canConnect(worldIn, pos, Direction.NORTH) || nC;
        boolean sS = canConnect(worldIn, pos, Direction.SOUTH) || sC;
        boolean wS = canConnect(worldIn, pos, Direction.WEST) || wC;
        boolean eS = canConnect(worldIn, pos, Direction.EAST) || eC;

        int connected = 0;
        connected += nS ? 0 : 1;
        connected += sS ? 0 : 1;
        connected += wS ? 0 : 1;
        connected += eS ? 0 : 1;

        BlockState init = this.getDefaultState();

        if
        (
            (worldIn.getBlockState(pos.up()).isSolidSide(worldIn, pos.up(), Direction.DOWN))
            || (up.getBlock() instanceof BlockAdvancedFence
                && ((BlockAdvancedFence) up.getBlock()).getType().equals(((BlockAdvancedFence) state.getBlock()).getType()))
            || (up.getBlock().matchesBlock(BlockRegistryHandler.GOLD_FENCE_DECORATION.get()) && up.get(ORB))
            || (connected != 2)
            || ((nS && (wS || eS)) || (sS && (wS || eS)))
        )
        {
            init = init.with(COLUMN, ColumnType.CORE);
        }
        else if
        (
            (
                (n.matchesBlock(state.getBlock()) && (n.get(COLUMN).equals(ColumnType.CORE) || n.get(COLUMN).equals(ColumnType.MID)))
                && (s.matchesBlock(state.getBlock()) && (s.get(COLUMN).equals(ColumnType.CORE) || s.get(COLUMN).equals(ColumnType.MID)))
            ) ||
            (
                (w.matchesBlock(state.getBlock()) && (w.get(COLUMN).equals(ColumnType.CORE) || w.get(COLUMN).equals(ColumnType.MID)))
                && (e.matchesBlock(state.getBlock()) && (e.get(COLUMN).equals(ColumnType.CORE) || e.get(COLUMN).equals(ColumnType.MID)))
            )
        )
        {
            init = init.with(COLUMN, ColumnType.SHORT);
        }
        else if
        (
            (
                (n.matchesBlock(state.getBlock()) && n.get(COLUMN).equals(ColumnType.SHORT))
                && (s.matchesBlock(state.getBlock()) && s.get(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (w.matchesBlock(state.getBlock()) && w.get(COLUMN).equals(ColumnType.SHORT))
                && (e.matchesBlock(state.getBlock()) && e.get(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (n.matchesBlock(state.getBlock()) && n.get(COLUMN).equals(ColumnType.SHORT))
                && (s.matchesBlock(state.getBlock()) && s.get(COLUMN).equals(ColumnType.MID))
            ) ||
            (
                (n.matchesBlock(state.getBlock()) && n.get(COLUMN).equals(ColumnType.MID))
                && (s.matchesBlock(state.getBlock()) && s.get(COLUMN).equals(ColumnType.SHORT))
            ) ||
            (
                (w.matchesBlock(state.getBlock()) && w.get(COLUMN).equals(ColumnType.SHORT))
                && (e.matchesBlock(state.getBlock()) && e.get(COLUMN).equals(ColumnType.MID))
            ) ||
            (
                (w.matchesBlock(state.getBlock()) && w.get(COLUMN).equals(ColumnType.MID))
                && (e.matchesBlock(state.getBlock()) && e.get(COLUMN).equals(ColumnType.SHORT))
            )
        )
        {
            init = init.with(COLUMN, ColumnType.MID);
        }
        else init = init.with(COLUMN, ColumnType.MID);

        init = init.with(NORTH, nS).with(SOUTH, sS).with(WEST, wS).with(EAST, eS);

        worldIn.setBlockState(pos, init);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();

        BlockState init = this.getDefaultState();

        BlockState n = worldIn.getBlockState(pos.north());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        BlockState e = worldIn.getBlockState(pos.east());

        boolean nC = n.matchesBlock(init.getBlock());
        boolean sC = s.matchesBlock(init.getBlock());
        boolean wC = w.matchesBlock(init.getBlock());
        boolean eC = e.matchesBlock(init.getBlock());

        boolean nS = n.isSolidSide(worldIn, pos.north(), Direction.SOUTH) || nC;
        boolean sS = s.isSolidSide(worldIn, pos.south(), Direction.NORTH) || sC;
        boolean wS = w.isSolidSide(worldIn, pos.west(), Direction.EAST) || wC;
        boolean eS = e.isSolidSide(worldIn, pos.east(), Direction.WEST) || eC;

        int connected = 0;
        connected += nS ? 0 : 1;
        connected += sS ? 0 : 1;
        connected += wS ? 0 : 1;
        connected += eS ? 0 : 1;

        if
        (
            (worldIn.getBlockState(pos.up()).isSolidSide(worldIn, pos.up(), Direction.DOWN))
            || (connected != 2)
            || ((nS && (wS || eS)) || (sS && (wS || eS)))
        )
        {
            init = init.with(COLUMN, ColumnType.CORE);
        }
        else if
        (
            (
                (nC && (n.get(COLUMN).equals(ColumnType.CORE)))
                && (sC && (s.get(COLUMN).equals(ColumnType.CORE)))
            ) ||
            (
                (wC && (w.get(COLUMN).equals(ColumnType.CORE)))
                && (eC && (e.get(COLUMN).equals(ColumnType.CORE)))
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
                    (nF.matchesBlock(init.getBlock()) && (nF.get(COLUMN).equals(ColumnType.CORE) || nF.get(COLUMN).equals(ColumnType.MID)))
                    && (sF.matchesBlock(init.getBlock()) && (sF.get(COLUMN).equals(ColumnType.CORE) || sF.get(COLUMN).equals(ColumnType.MID)))
                ) ||
                (
                    (wF.matchesBlock(init.getBlock()) && (wF.get(COLUMN).equals(ColumnType.CORE) || wF.get(COLUMN).equals(ColumnType.MID)))
                    && (eF.matchesBlock(init.getBlock()) && (eF.get(COLUMN).equals(ColumnType.CORE) || eF.get(COLUMN).equals(ColumnType.MID)))
                )
            )
            {
                init = init.with(COLUMN, ColumnType.MID);
            }
        }
        else init = init.with(COLUMN, ColumnType.CORE);

        init = init.with(NORTH, nS).with(SOUTH, sS).with(WEST, wS).with(EAST, eS);

        return init;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);
        if (stack.getItem().equals(Items.GOLD_INGOT))
        {
            if (hit.getFace().equals(Direction.UP) && worldIn.getBlockState(pos.up()).isAir())
            {
                BlockState deco = BlockRegistryHandler.GOLD_FENCE_DECORATION.get().getDefaultState();

                if (state.get(COLUMN).equals(ColumnType.CORE)) deco = deco.with(ORB, true);
                else if (state.get(NORTH) && state.get(SOUTH)) deco = deco.with(AXIS, Direction.Axis.Z);
                else if (state.get(EAST) && state.get(WEST)) deco = deco.with(AXIS, Direction.Axis.X);

                worldIn.setBlockState(pos.up(), deco);
                worldIn.playSound(player, pos, SoundEvents.BLOCK_LANTERN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getHeldItem(handIn).shrink(1);

                return ActionResultType.SUCCESS;
            }
        }
        else if (state.get(COLUMN).equals(ColumnType.CORE) && stack.getItem().equals(Items.STICK) && (player.isCreative() || stack.getCount() >= 2))
        {
            Direction direction = hit.getFace();
            if (direction.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(direction)).isAir())
            {
                BlockState exp = BlockRegistryHandler.RED_FENCE_EXPANSION.get().getDefaultState();

                worldIn.setBlockState(pos.offset(direction), exp.with(FACING, direction));
                worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) player.getHeldItem(handIn).shrink(2);

                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape part_N_DOWN = makeCuboidShape(6.5d, 6.0d, 0.0d, 9.5d, 9.0, 8.0d);
        VoxelShape part_N_UP = makeCuboidShape(6.5d, 12.0d, 0.0d, 9.5d, 15.0, 8.0d);
        VoxelShape part_W_DOWN = makeCuboidShape(0.0d, 6.0d, 6.5d, 8.0d, 9.0, 9.5d);
        VoxelShape part_W_UP = makeCuboidShape(0.0d, 12.0d, 6.5d, 8.0d, 15.0, 9.5d);
        VoxelShape part_S_DOWN = makeCuboidShape(6.5d, 6.0d, 8.0d, 9.5d, 9.0, 16.0d);
        VoxelShape part_S_UP = makeCuboidShape(6.5d, 12.0d, 8.0d, 9.5d, 15.0, 16.0d);
        VoxelShape part_E_DOWN = makeCuboidShape(8.0d, 6.0d, 6.5d, 16.0d, 9.0, 9.5d);
        VoxelShape part_E_UP = makeCuboidShape(8.0d, 12.0d, 6.5d, 16.0d, 15.0, 9.5d);

        VoxelShape part_N = VoxelShapes.or(part_N_UP, part_N_DOWN);
        VoxelShape part_S = VoxelShapes.or(part_S_UP, part_S_DOWN);
        VoxelShape part_W = VoxelShapes.or(part_W_UP, part_W_DOWN);
        VoxelShape part_E = VoxelShapes.or(part_E_UP, part_E_DOWN);

        VoxelShape column_core = makeCuboidShape(6.0d, 0.0d, 6.0d, 10.0d, 16.0d, 10.0d);
        VoxelShape column_mid = makeCuboidShape(6.0d, 0.0d, 6.0d, 10.0d, 13.5d, 10.0d);
        VoxelShape column_short = makeCuboidShape(6.0d, 0.0d, 6.0d, 10.0d, 7.5d, 10.0d);

        VoxelShape column;

        switch (state.get(COLUMN))
        {
            case MID: column = column_mid;break;
            case SHORT: column = column_short;break;
            default: column = column_core;
        }

        if (state.get(NORTH)) column = VoxelShapes.or(column, part_N);
        if (state.get(SOUTH)) column = VoxelShapes.or(column, part_S);
        if (state.get(WEST)) column = VoxelShapes.or(column, part_W);
        if (state.get(EAST)) column = VoxelShapes.or(column, part_E);

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
        public String getString() {return this.name;}
    }
}
