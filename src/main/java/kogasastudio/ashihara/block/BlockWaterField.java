package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemExmpleContainer;
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

import static kogasastudio.ashihara.utils.EasyBlockActionHandler.*;

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
//        LOGGER.info("检测出口结果为 " + flag);
        return flag;
    }

//    private int getHighestLevelAround(World worldIn, BlockPos pos)
//    {
//         /*
//            首先获取四周的流动水集合，若集合不为空则遍历集合将其水位添加
//            到数组中，对数组进行排序后返回数组中下标为0的成员，即为附近
//            最高水位。若集合为空则获取四周的静止水集合，若不为空则返回最
//            高水位8，若仍为空返回0.
//         */
//        List<BlockPos> list = getMarkedFluidPosAround(worldIn, pos, Fluids.FLOWING_WATER);
//        if (list.isEmpty())
//        {
//            List<BlockPos> list_still = getMarkedFluidPosAround(worldIn, pos, Fluids.WATER);
//            if (list_still.isEmpty()) {return 0;}
//            else return 8;
//        }
//        else
//        {
//            int[] levels = new int[list.size()];
//            byte b = 0;
//            for (BlockPos target : list)
//            {
//                levels[b] = worldIn.getFluidState(target).get(LEVEL_1_8);
//                b += 1;
//            }
//            Arrays.sort(levels);LOGGER.info("最高水位是" + levels[0] + ", 共有" + levels.length + "个可用数据");
//            if (levels[0] > 5) {return levels[0];} else return 0;
//        }
//    }

    private void onScheduleTick(World worldIn, BlockPos pos, int time)
    {
        //if (!worldIn.getPendingBlockTicks().isTickScheduled(pos, this))
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, time);
        //LOGGER.info("检测到进/排水需要！在22ticks后加入计划刻！ x: " + pos.getX() + "z: " + pos.getZ());
    }

    /*------------------------方块特性结束------------------------*/

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) //不明所以地类似注册BS的东西
    {
        builder.add(ISLINKEDTOSOURCE, LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) //碰撞箱的设定
    {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) //设置掉落物品为土球
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemExmpleContainer.DIRT_BALL, 3));
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
        //TODO:水田不会朝某两个方向流水，整片水田有一块与出口相邻就会导致状态反复弹
        BlockState fromState = worldIn.getBlockState(fromPos);
        if (!fromState.matchesBlock(BlockExampleContainer.BLOCK_RICE_CROP))
        {
            boolean watered = fourWaysFluidsIncludesWater(worldIn, pos);
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
            if (fromState.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
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
                    else if (levelF < levelT)
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
//        LOGGER.info("bool状态：" + hasWater + hasExit + watered);
//        String caseI = "无";
        int level = state.get(LEVEL);
        if (watered && hasWater && level < 8)
        {
            worldIn.setBlockState(pos, state.with(LEVEL, level + 1));
            onScheduleTick(worldIn, pos, 15 + level);
//            caseI = " 进水";
        }
        else if (!watered && !hasWater && level >= 5 && hasExit) //用来排水
        {
            worldIn.setBlockState(pos, state.with(LEVEL, level - 1));
            onScheduleTick(worldIn, pos, 15 + level);
//            caseI = "排水";
        }
//        else if (!watered && hasWater && hasExit)
//        {
//            worldIn.setBlockState(pos, state.with(ISLINKEDTOSOURCE, false));
//            onScheduleTick(worldIn, pos, 22);
//        }
//        if (worldIn.getFluidState(pos).getFluid() != Fluids.EMPTY)
//        {
//            worldIn.getPendingFluidTicks().scheduleTick(pos, worldIn.getFluidState(pos).getFluid(), worldIn.getFluidState(pos).getFluid().getTickRate(worldIn));
//        }
        //用来延时状态反弹
        if (watered)
        {
            if (n.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD) && !n.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.north(), n.with(ISLINKEDTOSOURCE, true));/*LOGGER.info("nfucked x: " + pos.north().getX() + "z: " + pos.getZ());*/}
            if (e.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD) && !e.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.east(), e.with(ISLINKEDTOSOURCE, true));/*LOGGER.info("efucked x: " + pos.east().getX() + "z: " + pos.getZ());*/}
            if (s.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD) && !s.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.south(), s.with(ISLINKEDTOSOURCE, true));/*LOGGER.info("sfucked x: " + pos.south().getX() + "z: " + pos.getZ());*/}
            if (w.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD) && !w.get(ISLINKEDTOSOURCE))
            {worldIn.setBlockState(pos.west(), w.with(ISLINKEDTOSOURCE, true));/*LOGGER.info("wfucked x: " + pos.west().getX() + "z: " + pos.getZ());*/}
        }
//        LOGGER.info("计划刻成功执行！执行内容：" + caseI);
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
        if (n.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
        {
            if (n.get(ISLINKEDTOSOURCE)) {watered = true;}
            if (n.get(LEVEL) > level) {level = n.get(LEVEL);}
        }
        if (e.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
        {
            if (e.get(ISLINKEDTOSOURCE)) {watered = true;}
            if (e.get(LEVEL) > level) {level = e.get(LEVEL);}
        }
        if (s.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
        {
            if (s.get(ISLINKEDTOSOURCE)) {watered = true;}
            if (s.get(LEVEL) > level) {level = s.get(LEVEL);}
        }
        if (w.matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
        {
            if (w.get(ISLINKEDTOSOURCE)) {watered = true;}
            if (w.get(LEVEL) > level) {level = w.get(LEVEL);}
        }
        if (fourWaysFluidsIncludesWater(worldIn, pos) || hasExit(worldIn, pos))
        {onScheduleTick(worldIn, pos, 10);}
//        LOGGER.info("现在附近最高的水位是" + level);
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
            return Fluids.WATER.getStillFluidState(false);
        }
        else return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return state.get(LEVEL) < 8 && (fluidIn == Fluids.FLOWING_WATER || fluidIn == Fluids.WATER);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        if (fluidStateIn.getFluid() == Fluids.FLOWING_WATER || fluidStateIn.getFluid() == Fluids.WATER)
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
        //return state.get(LEVEL) == 8 ? Fluids.WATER : Fluids.EMPTY;
        return Fluids.EMPTY;
    }
}
