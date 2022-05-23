package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockTatami extends Block
{
    public BlockTatami()
    {
        super
        (
            Properties.of(Material.BAMBOO_SAPLING)
            .strength(0.3F)
            .sound(SoundType.BAMBOO_SAPLING)
        );
        this.registerDefaultState
        (
            getStateDefinition().any()
            .setValue(LEFT, false)
            .setValue(RIGHT, false)
            .setValue(XCUT, false)
            .setValue(ZCUT, false)
            .setValue(LOCKED, false)
            .setValue(AXIS, Direction.Axis.X)
        );
    }

    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty XCUT = BooleanProperty.create("xcut");
    public static final BooleanProperty ZCUT = BooleanProperty.create("zcut");
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    //获取和更新bs，输入操作前的bs，返回操作后的bs
    //获取8个方向相邻的方块对其判定，若判定结果为true则取消这一点上的边缘权重
    //随后由multipart模型对其进行模型更新
    private BlockState updateState(BlockState state, World worldIn, BlockPos pos)
    {
        if (state.is(BlockRegistryHandler.TATAMI.get()) && !state.getValue(LOCKED))
        {
            BlockState n = worldIn.getBlockState(pos.north());
            BlockState s = worldIn.getBlockState(pos.south());
            BlockState e = worldIn.getBlockState(pos.east());
            BlockState w = worldIn.getBlockState(pos.west());

            boolean isX = state.getValue(AXIS).equals(Direction.Axis.X);

            state = state.setValue(LEFT, check(isX ? n : e, state))
            .setValue(RIGHT, check(isX ? s : w, state));
        }
        return state;
    }

    //检查传入的方块是否是榻榻米且轴与判断源榻榻米方块相同
    private boolean check(BlockState state, BlockState newState)
    {return state.is(BlockRegistryHandler.TATAMI.get()) && state.getValue(AXIS) == newState.getValue(AXIS);}

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LEFT, RIGHT, XCUT, ZCUT, LOCKED, AXIS);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState fromState = worldIn.getBlockState(fromPos);
        if (fromState.is(BlockRegistryHandler.TATAMI.get()) || fromState.is(Blocks.AIR))
        {
            worldIn.setBlockAndUpdate(pos, updateState(state, worldIn, pos));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return updateState(this.defaultBlockState(), worldIn, pos).setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    //空手shift右键锁定，剪刀右键加中央边缘权重（剪开或合上）
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (state.is(BlockRegistryHandler.TATAMI.get()))
        {
            if (player.getItemInHand(handIn).isEmpty() && player.isShiftKeyDown())
            {
                worldIn.setBlockAndUpdate(pos, state.setValue(LOCKED, !state.getValue(LOCKED)));
                worldIn.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                for(byte b = 0; b < 12; b += 1)
                {
                    worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, ParticleRegistryHandler.RICE.get()), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, RANDOM.nextFloat() / 2.0F, 5.0E-5D, RANDOM.nextFloat() / 2.0F);
                }
                return ActionResultType.SUCCESS;
            }
            else if (player.getItemInHand(handIn).getItem() instanceof ShearsItem && !player.isShiftKeyDown())
            {
                Direction direction = player.getDirection();
                if (direction.getAxis().equals(Direction.Axis.X))
                {
                    worldIn.setBlockAndUpdate(pos, state.setValue(XCUT, !state.getValue(XCUT)));
                    worldIn.playSound(player, pos, SoundEvents.BAMBOO_SAPLING_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                else if (direction.getAxis().equals(Direction.Axis.Z))
                {
                    worldIn.setBlockAndUpdate(pos, state.setValue(ZCUT, !state.getValue(ZCUT)));
                    worldIn.playSound(player, pos, SoundEvents.BAMBOO_SAPLING_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.PASS;
    }
}
