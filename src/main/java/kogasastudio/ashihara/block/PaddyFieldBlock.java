package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static kogasastudio.ashihara.helper.BlockActionHelper.fourWaysFluidsIncludesWater;
import static kogasastudio.ashihara.helper.BlockActionHelper.getMarkedBlockPosAround;

public class PaddyFieldBlock extends Block implements BucketPickup, LiquidBlockContainer
{
    public static final BooleanProperty HAS_WATER = BooleanProperty.create("haswaterinside");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 4, 8);

    public PaddyFieldBlock()
    {
        super
                (Properties.of()
                         .mapColor(MapColor.DIRT)
                        .strength(0.5F)
                        // todo tag .harvestTool(ToolType.SHOVEL)
                        // todo tag .harvestLevel(2)
                        .sound(SoundType.GRAVEL)
                );
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_WATER, false));
    }

    private boolean matchesWaterField(BlockState state)
    {
        return state.is(BlockRegistryHandler.WATER_FIELD.get());
    }

    private boolean hasExit(Level worldIn, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.MutableBlockPos pos1 = pos.mutable();
        BlockPos.MutableBlockPos pos2 = pos.mutable();

        for (Direction direction : Direction.values())
        {
            if (direction != Direction.DOWN && direction != Direction.UP)
            {
                pos1.setWithOffset(pos, direction);
                pos2.setWithOffset(pos1, direction);
                BlockState state = worldIn.getBlockState(pos1);
                FluidState fluid = worldIn.getFluidState(pos);
                FluidState fluidF = worldIn.getFluidState(pos1);
                if
                (
                        (state.is(Blocks.AIR)
                                || (fluid.getType() == Fluids.FLOWING_WATER
                                && fluidF.getType() == Fluids.FLOWING_WATER
                                && fluidF.getValue(BlockStateProperties.LEVEL_FLOWING) < fluid.getValue(BlockStateProperties.LEVEL_FLOWING)))
                )
                {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private void onScheduleTick(Level worldIn, BlockPos pos, int time)
    {
        worldIn.scheduleTick(pos, this, time);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) //注册BS
    {
        builder.add(HAS_WATER, LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) //碰撞箱的设定
    {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) //设置掉落物品为2土球
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.DIRT_BALL.get(), 2));
        return list;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return 1.0F;
    }

    /**
     * 自身附近有水源？
     * 是：
     *  自身bool为false?
     *  是：自身bool设为true
     *  否：不做更改
     *  加入计划刻
     * 否：
     *  自身bool为true？
     *  是：
     *      自身附近有出口？
     *      是：加入计划刻
     *      否：不做更改
     *      自身设为false
     *  否：不做更改
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
     * 两者水位不相同？
     * 是：
     *  来者 > 自身？
     *  是：自身水位设为来者水位
     *  否：
     *      来者 < 自身？
     *      是：
     *          自身附近有水源？
     *          是：来者水位设为自身水位
     *          否：自身水位设为来者水位
     *      否：不做更改
     * 否：不做更改
     *
     * @param state    2
     * @param worldIn  2
     * @param pos      2
     * @param blockIn  2
     * @param fromPos  2
     * @param isMoving 2
     */
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) //那一大堆判定
    {
        BlockState fromState = worldIn.getBlockState(fromPos);
        if (!fromState.is(BlockRegistryHandler.RICE_CROP.get()))
        {
            boolean watered = fourWaysFluidsIncludesWater(worldIn, pos);
            if (worldIn.getFluidState(pos.above()).getType().equals(Fluids.WATER))
            {
                watered = true;
            }
            boolean hasExit = hasExit(worldIn, pos);
            int levelT = state.getValue(LEVEL);
            boolean boolT = state.getValue(HAS_WATER);
            if (watered)
            {
                if (!boolT)
                {
                    worldIn.setBlockAndUpdate(pos, state.setValue(HAS_WATER, true));
                    onScheduleTick(worldIn, pos, 22);
                }
            } else
            {
                if (hasExit)
                {
                    onScheduleTick(worldIn, pos, 10);
                }
            }
            if (matchesWaterField(fromState))
            {
                int levelF = fromState.getValue(LEVEL);
                boolean boolF = fromState.getValue(HAS_WATER);
                if (boolT != boolF)
                {
                    if (!boolF)
                    {
                        if (watered)
                        {
                            worldIn.setBlockAndUpdate(fromPos, fromState.setValue(HAS_WATER, true));
                        } else
                        {
                            worldIn.setBlockAndUpdate(pos, state.setValue(HAS_WATER, false));
                        }
                    } else
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(HAS_WATER, true));
                    }
                }
                if (levelT != levelF)
                {
                    if (levelF > levelT)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(LEVEL, levelF));
                    } else
                    {
                        if (watered)
                        {
                            worldIn.setBlockAndUpdate(fromPos, fromState.setValue(LEVEL, levelT));
                        } else
                        {
                            worldIn.setBlockAndUpdate(pos, state.setValue(LEVEL, levelF));
                        }
                    }
                }
            } else
            {
                if (!watered)
                {
                    worldIn.setBlockAndUpdate(pos, state.setValue(HAS_WATER, false));
                }
            }
            if (levelT > 5)
            {
                List<BlockPos> list = getMarkedBlockPosAround(worldIn, pos, Blocks.AIR);
                if (!list.isEmpty())
                {
                    for (BlockPos pos1 : list)
                    {
                        if (worldIn.getBlockState(pos1).is(Blocks.AIR) && worldIn.getFluidState(pos1).isEmpty())
                        {
                            worldIn.setBlockAndUpdate(pos1, Fluids.FLOWING_WATER.getFlowing(state.getValue(LEVEL) - 1, false).createLegacyBlock());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand)
    {
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        boolean hasWater = state.getValue(HAS_WATER);
        boolean hasExit = hasExit(worldIn, pos);
        boolean watered = fourWaysFluidsIncludesWater(worldIn, pos); //用来进水
        int level = state.getValue(LEVEL);
        if (watered && hasWater && level < 8)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(LEVEL, level + 1));
            onScheduleTick(worldIn, pos, 15 + level);
        } else if (!watered && !hasWater && level >= 5 && hasExit) //用来排水
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(LEVEL, level - 1));
            onScheduleTick(worldIn, pos, 15 + level);
        }
        //用来延时状态反弹
        if (watered)
        {
            if (matchesWaterField(n) && !n.getValue(HAS_WATER))
            {
                worldIn.setBlockAndUpdate(pos.north(), n.setValue(HAS_WATER, true));
            }
            if (matchesWaterField(e) && !e.getValue(HAS_WATER))
            {
                worldIn.setBlockAndUpdate(pos.east(), e.setValue(HAS_WATER, true));
            }
            if (matchesWaterField(s) && !s.getValue(HAS_WATER))
            {
                worldIn.setBlockAndUpdate(pos.south(), s.setValue(HAS_WATER, true));
            }
            if (matchesWaterField(w) && !w.getValue(HAS_WATER))
            {
                worldIn.setBlockAndUpdate(pos.west(), w.setValue(HAS_WATER, true));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState preState = this.defaultBlockState();
        boolean watered = false;
        int level = 4;
        BlockState n = worldIn.getBlockState(pos.north());
        BlockState e = worldIn.getBlockState(pos.east());
        BlockState s = worldIn.getBlockState(pos.south());
        BlockState w = worldIn.getBlockState(pos.west());
        if (fourWaysFluidsIncludesWater(worldIn, pos))
        {
            watered = true;
            level = 8;
        } else
        {
            if (matchesWaterField(n))
            {
                if (n.getValue(HAS_WATER))
                {
                    watered = true;
                }
                if (n.getValue(LEVEL) > level)
                {
                    level = n.getValue(LEVEL);
                }
            }
            if (matchesWaterField(e))
            {
                if (e.getValue(HAS_WATER))
                {
                    watered = true;
                }
                if (e.getValue(LEVEL) > level)
                {
                    level = e.getValue(LEVEL);
                }
            }
            if (matchesWaterField(s))
            {
                if (s.getValue(HAS_WATER))
                {
                    watered = true;
                }
                if (s.getValue(LEVEL) > level)
                {
                    level = s.getValue(LEVEL);
                }
            }
            if (matchesWaterField(w))
            {
                if (w.getValue(HAS_WATER))
                {
                    watered = true;
                }
                if (w.getValue(LEVEL) > level)
                {
                    level = w.getValue(LEVEL);
                }
            }
        }
        if (fourWaysFluidsIncludesWater(worldIn, pos) || hasExit(worldIn, pos))
        {
            onScheduleTick(worldIn, pos, 10);
        }
        return preState.setValue(HAS_WATER, watered).setValue(LEVEL, level);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (state.getValue(LEVEL) > 5)
        {
            return Fluids.FLOWING_WATER.getFlowing(state.getValue(LEVEL), false);
        } else if (state.getValue(LEVEL) == 8)
        {
            return Fluids.WATER.getSource(false);
        } else
        {
            return Fluids.EMPTY.defaultFluidState();
        }
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player pPlayer, BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid)
    {
        return pState.getValue(LEVEL) < 8 && (pFluid == Fluids.FLOWING_WATER || pFluid == Fluids.WATER);
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        if (fluidStateIn.getType() == Fluids.FLOWING_WATER || fluidStateIn.getType() == Fluids.WATER)
        {
            if (!worldIn.isClientSide())
            {
                onScheduleTick((Level) worldIn, pos, 10);
            }
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState)
    {
        return pState.getValue(LEVEL) == 8 ? Items.WATER_BUCKET.getDefaultInstance() : ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound()
    {
        return Fluids.WATER.getPickupSound();
    }
}
