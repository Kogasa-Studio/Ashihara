package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.block.IVariable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

/**
 * 这是一个为了方便提供各种方块操作的类
 * 非常的屎山，如果血压升高请立即关闭该类的浏览页面
 */
public class BlockActionHelper {
    /**
     * 在给定的世界检测处在给定的坐标的方块是否与给定的方块相邻
     *
     * @param worldIn 方块所处的世界
     * @param pos     方块所处的坐标
     * @param block   你想检测的方块实例
     * @return 这个坐标上的方块旁边有没有你想检测的方块
     */
    public static boolean fourWaysNeighborsIncludes(Level worldIn, BlockPos pos, Block block) {
        return worldIn.getBlockState(pos.north()).is(block)
                || worldIn.getBlockState(pos.east()).is(block)
                || worldIn.getBlockState(pos.south()).is(block)
                || worldIn.getBlockState(pos.west()).is(block);
    }

    /**
     * 跟上面类似，检测四周方块有没有被tag
     *
     * @param worldIn 世界
     * @param pos     坐标
     * @param tag     你想判断的方块tag
     * @return 是否被tag
     */
    public static boolean fourWaysBlocksTagged(Level worldIn, BlockPos pos, TagKey<Block> tag) {
        return worldIn.getBlockState(pos.north()).is(tag)
                || worldIn.getBlockState(pos.east()).is(tag)
                || worldIn.getBlockState(pos.south()).is(tag)
                || worldIn.getBlockState(pos.west()).is(tag);
    }

    /**
     * 跟上面类似，检测四周流体有没有被tag
     *
     * @param worldIn  世界
     * @param pos      坐标
     * @param fluidTag 你想判断的流体tag
     * @return 是否被tag
     */
    public static boolean fourWaysFluidsTagged(Level worldIn, BlockPos pos, TagKey<Fluid> fluidTag) {
        return worldIn.getBlockState(pos.north()).getFluidState().is(fluidTag)
                || worldIn.getBlockState(pos.east()).getFluidState().is(fluidTag)
                || worldIn.getBlockState(pos.south()).getFluidState().is(fluidTag)
                || worldIn.getBlockState(pos.west()).getFluidState().is(fluidTag);
    }

    private static boolean watered(Level worldIn, BlockPos pos1) {
        boolean flag = false;
        FluidState fluid1 = worldIn.getFluidState(pos1);
        if (fluid1.getType() == Fluids.WATER) {
            flag = true;
        } else if (fluid1.getType() == Fluids.FLOWING_WATER) {
            if (fluid1.getValue(LEVEL_FLOWING) == 8 && fluid1.getValue(FALLING)) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean fourWaysFluidsIncludesWater(Level worldIn, BlockPos pos) {
        //        LOGGER_MAIN.info("检测了水源，水源状态： " + flag + "， 坐标：x：" + pos.getX() + "，z：" + pos.getZ());
        return watered(worldIn, pos.north())
                || watered(worldIn, pos.east())
                || watered(worldIn, pos.south())
                || watered(worldIn, pos.west())
                || watered(worldIn, pos.above());
    }

    /**
     * 一个用来获取周边所有匹配方块坐标的玩意（不包括下方）
     *
     * @param worldIn  世界
     * @param pos      坐标
     * @param expected 预期的方块
     * @return 判定到的方块的坐标，以List形式
     */
    public static List<BlockPos> getMarkedBlockPosAround(Level worldIn, BlockPos pos, Block expected) {
        List<BlockPos> list = new LinkedList<>();

        if (worldIn.getBlockState(pos.north()).getBlock() == expected) {
            list.add(pos.north());
        }
        if (worldIn.getBlockState(pos.east()).getBlock() == expected) {
            list.add(pos.east());
        }
        if (worldIn.getBlockState(pos.south()).getBlock() == expected) {
            list.add(pos.south());
        }
        if (worldIn.getBlockState(pos.west()).getBlock() == expected) {
            list.add(pos.west());
        }
        return list;
    }

    /**
     * 一个用来获取周边所有匹配流体坐标的玩意（不包括下方）
     *
     * @param worldIn  世界
     * @param pos      坐标
     * @param expected 预期的流体
     * @return 判定到的流体的坐标，以List形式
     */
    public static List<BlockPos> getMarkedFluidPosAround(Level worldIn, BlockPos pos, Fluid expected) {
        List<BlockPos> list = new LinkedList<>();

        if (worldIn.getFluidState(pos.north()).getType() == expected) {
            list.add(pos.north());
        }
        if (worldIn.getFluidState(pos.east()).getType() == expected) {
            list.add(pos.east());
        }
        if (worldIn.getFluidState(pos.south()).getType() == expected) {
            list.add(pos.south());
        }
        if (worldIn.getFluidState(pos.west()).getType() == expected) {
            list.add(pos.west());
        }
        return list;
    }

    /**
     * 这是一个用来方便地制作可点亮光源方块的方法
     *
     * @param lightValue 你想让它在点亮时发出的光强度
     * @return 可以直接放进构造方法的东西
     * 注意方块必须有原版提供的LIT方块状态
     */
    public static ToIntFunction<BlockState> getLightValueLit(int lightValue) {
        return (state) -> state.getValue(LIT) ? lightValue : 1;
    }

    /**
     * 按照所给的方向判断贴图应该在ZP上旋转多少度才能在该方向上显示
     *
     * @param dir 方向
     * @return 需要在ZP上旋转的度数
     */
    public static int getRotationByFacing(Direction dir) {
        switch (dir) {
            case NORTH:
                return 180;
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            case EAST:
                return -90;
        }
        return 0;
    }

    public static boolean typeMatches(BlockState state, BlockState toBeChecked) {
        return state.getBlock() instanceof IVariable<?> && toBeChecked.getBlock() instanceof IVariable<?>
                && ((IVariable<?>) state.getBlock()).getType().equals(((IVariable<?>) toBeChecked.getBlock()).getType());
    }
}
