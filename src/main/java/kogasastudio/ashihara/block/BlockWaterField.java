package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import java.util.*;

import static kogasastudio.ashihara.helper.BlockActionHelper.*;
import static net.minecraft.fluid.Fluids.WATER;

public class BlockWaterField extends Block implements ILiquidContainer, IBucketPickupHandler
{
    public BlockWaterField()
    {
        super
        (Properties.create(Material.EARTH)
        .hardnessAndResistance(0.5F)
        .harvestTool(ToolType.SHOVEL)
        .harvestLevel(2)
        .sound(SoundType.GROUND)
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(ISLINKEDTOSOURCE, false));
    }

    public static final BooleanProperty ISLINKEDTOSOURCE = BooleanProperty.create("haswaterinside");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level",4,8);

    private boolean matchesWaterField(BlockState state) {return state.matchesBlock(BlockRegistryHandler.WATER_FIELD.get());}

    private boolean hasExit(World worldIn, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.Mutable pos1 = pos.toMutable();
        BlockPos.Mutable pos2 = pos.toMutable();

        for(Direction direction : Direction.values())
        {
            if (direction != Direction.DOWN && direction != Direction.UP)
            {
                pos1.setAndMove(pos, direction);
                pos2.setAndMove(pos1, direction);
                BlockState state = worldIn.getBlockState(pos1);
                FluidState fluid = worldIn.getFluidState(pos);
                FluidState fluidF = worldIn.getFluidState(pos1);
                if
                (
                    (state.matchesBlock(Blocks.AIR)
                    || (fluid.getFluid() == Fluids.FLOWING_WATER
                    && fluidF.getFluid() == Fluids.FLOWING_WATER
                    && fluidF.get(BlockStateProperties.LEVEL_1_8) < fluid.get(BlockStateProperties.LEVEL_1_8)))
                )
                {flag = true;break;}
            }
        }
        return flag;
    }

    private void onScheduleTick(World worldIn, BlockPos pos, int time)
    {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, time);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) //注册BS
    {
        builder.add(ISLINKEDTOSOURCE, LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) //碰撞箱的设定
    {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) //设置掉落物品为2土球
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.DIRT_BALL.get(), 2));
        return list;
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1.0F;
    }

    /**
     * 自身附近有水源？
     * 是：
     *     自身bool为false?
     *     是：自身bool设为true
     *     否：不做更改
     *     加入计划刻
     * 否：
     *    自身bool为true？
     *    是：
     *        自身附近有出口？
     *        是：加入计划刻
     *        否：不做更改
     *        自身设为false
     *    否：不做更改
     * 发生变更的方块是水田？
     * 是：
     *     两者bool不相同？
     *     是：
     *         来者bool为false？
     *         是：
     *             自身附近有水源？
     *             是：将来者bool设为true
     *             否：自身设为false
     *         否：自身设为true
     *     否：不做更改
     *     两者水位不相同？
     *     是：
     *         来者 > 自身？
     *         是：自身水位设为来者水位
     *         否：
     *             来者 < 自身？
     *             是：
     *                 自身附近有水源？
     *                 是：来者水位设为自身水位
     *                 否：自身水位设为来者水位
     *             否：不做更改
     *     否：不做更改
     * @param state 2
     * @param worldIn 2
     * @param pos 2
     * @param blockIn 2
     * @param fromPos 2
     * @param isMoving 2
     */
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) //那一大堆判定
    {
        BlockState fromState = worldIn.getBlockState(fromPos);
        if (!fromState.matchesBlock(BlockRegistryHandler.RICE_CROP.get()))
        {
            boolean watered = fourWaysFluidsIncludesWater(worldIn, pos);
            if (worldIn.getFluidState(pos.up()).getFluid().equals(WATER)) watered = true;
            boolean hasExit = hasExit(worldIn, pos);
            int levelT = state.get(LEVEL);
            boolean boolT = state.get(ISLINKEDTOSOURCE);
            if (watered)
            {
                if (!boolT) {worldIn.setBlockState(pos, state.with(ISLINKEDTOSOURCE, true));onScheduleTick(worldIn, pos, 22);}
            }
            else
            {
                if (hasExit)
                {
                    onScheduleTick(worldIn, pos, 10);
                }
            }
            if (matchesWaterField(fromState))
            {
                int levelF = fromState.get(LEVEL);
                boolean boolF = fromState.get(ISLINKEDTOSOURCE);
                if (boolT != boolF)
                {
                    if (!boolF)
                    {
                        if (watered)
                        {
                            worldIn.setBlockState(fromPos, fromState.with(ISLINKEDTOSOURCE, true));
                        }
                        else worldIn.setBlockState(pos, state.with(ISLINKEDTOSOURCE, false));
                    }
                    else worldIn.setBlockState(pos, state.with(ISLINKEDTOSOURCE, true));
                }
                if (levelT != levelF)
                {
                    if (levelF > levelT) {worldIn.setBlockState(pos, state.with(LEVEL, levelF));}
                    else
                    {
                        if (watered)
                        {worldIn.setBlockState(fromPos, fromState.with(LEVEL, levelT));}
                        else {worldIn.setBlockState(pos, state.with(LEVEL, levelF));}
                    }
                }
            }
            else
            {
                if (!watered) {worldIn.setBlockState(pos, state.with(ISLINKEDTOSOURCE, false));}
            }
            if (levelT > 5)
            {
                List<BlockPos> list = getMarkedBlockPosAround(worldIn, pos, Blocks.AIR);
                if (!list.isEmpty())
                {
                    for (BlockPos pos1 : list)
                    {
                        if (worldIn.getBlockState(pos1).matchesBlock(Blocks.AIR) && worldIn.getFluidState(pos1).isEmpty())
                        {worldIn.setBlockState(pos1, Fluids.FLOWING_WATER.getFlowingFluidState(state.get(LEVEL) - 1, false).getBlockState());}
                    }
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        boolean hasWater = state.get(ISLINKEDTOSOURCE);
        boolean hasExit = hasExit(worldIn, pos);
        boolean watered = fourWaysFluidsIncludesWater(worldIn, pos); //用来进水
        int level = state.get(LEVEL);
        if (watered && hasWater && level < 8)
        {
            worldIn.setBlockState(pos, state.with(LEVEL, level + 1));
            onScheduleTick(worldIn, pos, 15 + level);
        }
        else if (!watered && !hasWater && level >= 5 && hasExit) //用来排水
        {
            worldIn.setBlockState(pos, state.with(LEVEL, level - 1));
            onScheduleTick(worldIn, pos, 15 + level);
        }
        //用来延时状态反弹
        if (watered)
        {
            if (matchesWaterField(n) && !n.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.north(), n.with(ISLINKEDTOSOURCE, true));}
            if (matchesWaterField(e) && !e.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.east(), e.with(ISLINKEDTOSOURCE, true));}
            if (matchesWaterField(s) && !s.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.south(), s.with(ISLINKEDTOSOURCE, true));}
            if (matchesWaterField(w) && !w.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.west(), w.with(ISLINKEDTOSOURCE, true));}
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState preState = this.getDefaultState();
        boolean watered = false;int level = 4;
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        if (fourWaysFluidsIncludesWater(worldIn, pos))
        {
            watered = true;
            level = 8;
        }
        else
        {
            if (matchesWaterField(n))
            {
                if (n.get(ISLINKEDTOSOURCE)) {watered = true;}
                if (n.get(LEVEL) > level) {level = n.get(LEVEL);}
            }
            if (matchesWaterField(e))
            {
                if (e.get(ISLINKEDTOSOURCE)) {watered = true;}
                if (e.get(LEVEL) > level) {level = e.get(LEVEL);}
            }
            if (matchesWaterField(s))
            {
                if (s.get(ISLINKEDTOSOURCE)) {watered = true;}
                if (s.get(LEVEL) > level) {level = s.get(LEVEL);}
            }
            if (matchesWaterField(w))
            {
                if (w.get(ISLINKEDTOSOURCE)) {watered = true;}
                if (w.get(LEVEL) > level) {level = w.get(LEVEL);}
            }
        }
        if (fourWaysFluidsIncludesWater(worldIn, pos) || hasExit(worldIn, pos))
        {onScheduleTick(worldIn, pos, 10);}
        return preState.with(ISLINKEDTOSOURCE, watered).with(LEVEL, level);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (state.get(LEVEL) > 5)
        {
            return Fluids.FLOWING_WATER.getFlowingFluidState(state.get(LEVEL), false);
        }
        else if (state.get(LEVEL) == 8)
        {
            return WATER.getStillFluidState(false);
        }
        else return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return state.get(LEVEL) < 8 && (fluidIn == Fluids.FLOWING_WATER || fluidIn == WATER);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        if (fluidStateIn.getFluid() == Fluids.FLOWING_WATER || fluidStateIn.getFluid() == WATER)
        {
            if (!worldIn.isRemote())
            {
                onScheduleTick((World)worldIn, pos, 10);
            }
            return true;
        }
        else return false;
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        return Fluids.EMPTY;
    }
}
