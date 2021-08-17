package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class BlockTatami extends Block
{
    public BlockTatami()
    {
        super
        (
            Properties.create(Material.BAMBOO_SAPLING)
            .hardnessAndResistance(0.3F)
            .sound(SoundType.BAMBOO_SAPLING)
        );
        this.setDefaultState
        (
            getStateContainer().getBaseState()
            .with(N, false)
            .with(E, false)
            .with(S, false)
            .with(W, false)
            .with(WN, false)
            .with(EN, false)
            .with(WS, false)
            .with(ES, false)
            .with(XCUT, false)
            .with(ZCUT, false)
            .with(LOCKED, false)
            .with(AXIS, Direction.Axis.X)
        );
    }

    public static final BooleanProperty N = BooleanProperty.create("n");
    public static final BooleanProperty E = BooleanProperty.create("e");
    public static final BooleanProperty S = BooleanProperty.create("s");
    public static final BooleanProperty W = BooleanProperty.create("w");
    public static final BooleanProperty WN = BooleanProperty.create("wn");
    public static final BooleanProperty EN = BooleanProperty.create("en");
    public static final BooleanProperty WS = BooleanProperty.create("ws");
    public static final BooleanProperty ES = BooleanProperty.create("es");
    public static final BooleanProperty XCUT = BooleanProperty.create("xcut");
    public static final BooleanProperty ZCUT = BooleanProperty.create("zcut");
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    //获取和更新bs，输入操作前的bs，返回操作后的bs
    //获取8个方向相邻的方块对其判定，若判定结果为true则取消这一点上的边缘权重
    //随后由multipart模型对其进行模型更新
    private BlockState updateState(BlockState state, World worldIn, BlockPos pos)
    {
        if (state.matchesBlock(BlockRegistryHandler.TATAMI.get()) && !state.get(LOCKED))
        {
            BlockState n = worldIn.getBlockState(pos.north());
            BlockState e = worldIn.getBlockState(pos.east());
            BlockState s = worldIn.getBlockState(pos.south());
            BlockState w = worldIn.getBlockState(pos.west());
            BlockState wn = worldIn.getBlockState(pos.north().west());
            BlockState en = worldIn.getBlockState(pos.north().east());
            BlockState ws = worldIn.getBlockState(pos.south().west());
            BlockState es = worldIn.getBlockState(pos.south().east());
            state = state.with(N, check(n, state))
            .with(E, check(e, state))
            .with(S, check(s, state))
            .with(W, check(w, state))
            .with(WN, check(wn, state))
            .with(EN, check(en, state))
            .with(WS, check(ws, state))
            .with(ES, check(es, state));
        }
        return state;
    }

    //检查传入的方块是否是榻榻米且轴与判断源榻榻米方块相同
    private boolean check(BlockState state, BlockState newState)
    {return state.matchesBlock(BlockRegistryHandler.TATAMI.get()) && state.get(AXIS) == newState.get(AXIS);}

//    目前没什么作用
//    private BlockState setEdgeAll(BlockState state)
//    {
//        if (state.matchesBlock(BlockRegistryHandler.TATAMI.get()))
//        {
//            state = this.getDefaultState().with(LOCKED, true);
//        }
//        return state;
//    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(N,S,E,W,WN,EN,WS,ES,XCUT,ZCUT,LOCKED,AXIS);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState fromState = worldIn.getBlockState(fromPos);
        if (fromState.matchesBlock(BlockRegistryHandler.TATAMI.get()) || fromState.matchesBlock(Blocks.AIR))
        {
            worldIn.setBlockState(pos, updateState(state, worldIn, pos));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();
        return updateState(this.getDefaultState(), worldIn, pos).with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    //空手shift右键锁定，剪刀右键加中央边缘权重（剪开或合上）
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (state.matchesBlock(BlockRegistryHandler.TATAMI.get()))
        {
            if (player.getHeldItem(handIn).isEmpty() && player.isSneaking())
            {
                worldIn.setBlockState(pos, state.with(LOCKED, !state.get(LOCKED)));
                worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                for(byte b = 0; b < 12; b += 1)
                {
                    worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, ParticleRegistryHandler.RICE.get()), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, RANDOM.nextFloat() / 2.0F, 5.0E-5D, RANDOM.nextFloat() / 2.0F);
                }
                return ActionResultType.SUCCESS;
            }
            else if (player.getHeldItem(handIn).getItem() instanceof ShearsItem && !player.isSneaking())
            {
                Direction direction = player.getHorizontalFacing();
                if (direction.getAxis().equals(Direction.Axis.X))
                {
                    worldIn.setBlockState(pos, state.with(XCUT, !state.get(XCUT)));
                    worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_SAPLING_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                else if (direction.getAxis().equals(Direction.Axis.Z))
                {
                    worldIn.setBlockState(pos, state.with(ZCUT, !state.get(ZCUT)));
                    worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_SAPLING_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResultType.SUCCESS;
            }
//            else if (player.getHeldItem(handIn).getItem() instanceof ShearsItem && player.isSneaking())
//            {
//                worldIn.setBlockState(pos, setEdgeAll(state));
//                worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
//                return ActionResultType.SUCCESS;
//            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.TATAMI.get()));
        return list;
    }
}
