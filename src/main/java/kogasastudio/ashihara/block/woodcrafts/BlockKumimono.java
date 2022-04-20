package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockKumimono extends Block implements IVariable<AshiharaWoodTypes>
{
    private static AshiharaWoodTypes type;
    public BlockKumimono(AshiharaWoodTypes typeIn)
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.5F)
            .sound(SoundType.WOOD)
        );
        this.setDefaultState
        (this.getDefaultState().with(BEAM_N, false).with(BEAM_E, false).with(BEAM_S, false).with(BEAM_W, false));
        type = typeIn;
    }

    public static final BooleanProperty BEAM_N = BlockStateProperties.NORTH;
    public static final BooleanProperty BEAM_E = BlockStateProperties.EAST;
    public static final BooleanProperty BEAM_S = BlockStateProperties.SOUTH;
    public static final BooleanProperty BEAM_W = BlockStateProperties.WEST;

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BEAM_N, BEAM_E, BEAM_S, BEAM_W);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);

        boolean isBeam = stack.getItem() instanceof BlockItem
        && state.getBlock() instanceof BlockKumimono
        && ((BlockItem) stack.getItem()).getBlock() instanceof BlockKawaki
        && ((BlockKawaki) ((BlockItem) stack.getItem()).getBlock()).getType().equals(((BlockKumimono) state.getBlock()).getType());

        if (isBeam || stack.getItem() instanceof AxeItem)
        {
            SoundEvent event = isBeam ? SoundEvents.BLOCK_WOOD_PLACE : SoundEvents.BLOCK_WOOD_BREAK;
            BooleanProperty dir = BEAM_N;
            Direction facing = player.getHorizontalFacing();
            switch (facing)
            {
                case NORTH:
                {
                    dir = BEAM_N;
                    if (state.get(BEAM_S) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_S, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.get(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    break;
                }
                case EAST:
                {
                    if (state.get(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.get(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_E;
                    break;
                }
                case SOUTH:
                {
                    if (state.get(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.get(BEAM_S) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_S, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_S;
                    break;
                }
                case WEST:
                {
                    if (state.get(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.get(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockState(pos, state.with(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_W;
                }
            }
            BlockState expandedState = worldIn.getBlockState(pos.offset(player.getHorizontalFacing().getOpposite()));

            boolean expandable = expandedState.getBlock() instanceof BlockKumimono
            && state.getBlock() instanceof BlockKumimono
            && ((BlockKumimono) expandedState.getBlock()).getType().equals(((BlockKumimono) state.getBlock()).getType());

            if (isBeam && expandable && !expandedState.get(dir))
            {
                worldIn.setBlockState(pos.offset(player.getHorizontalFacing().getOpposite()), expandedState.with(dir, true));
                worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape body = makeCuboidShape(2.5d, 0.0d, 2.5d, 13.5d, 10.0d, 13.5d);
        VoxelShape beam_n = makeCuboidShape(5.0d, 6.0d, 0.0d, 11.0d, 16.0d, 8.0d);
        VoxelShape beam_e = makeCuboidShape(8.0d, 6.0d, 5.0d, 16.0d, 16.0d, 11.0d);
        VoxelShape beam_s = makeCuboidShape(5.0d, 6.0d, 8.0d, 11.0d, 16.0d, 16.0d);
        VoxelShape beam_w = makeCuboidShape(0.0d, 6.0d, 5.0d, 8.0d, 16.0d, 11.0d);

        if (state.get(BEAM_N)) body = VoxelShapes.or(body, beam_n);
        if (state.get(BEAM_E)) body = VoxelShapes.or(body, beam_e);
        if (state.get(BEAM_S)) body = VoxelShapes.or(body, beam_s);
        if (state.get(BEAM_W)) body = VoxelShapes.or(body, beam_w);

        return body;
    }

    @Override
    public AshiharaWoodTypes getType() {return type;}
}
