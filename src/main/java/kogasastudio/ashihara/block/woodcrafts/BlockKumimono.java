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

import net.minecraft.block.AbstractBlock.Properties;

public class BlockKumimono extends Block implements IVariable<AshiharaWoodTypes>
{
    private static AshiharaWoodTypes type;
    public BlockKumimono(AshiharaWoodTypes typeIn)
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD)
        );
        this.registerDefaultState
        (this.defaultBlockState().setValue(BEAM_N, false).setValue(BEAM_E, false).setValue(BEAM_S, false).setValue(BEAM_W, false));
        type = typeIn;
    }

    public static final BooleanProperty BEAM_N = BlockStateProperties.NORTH;
    public static final BooleanProperty BEAM_E = BlockStateProperties.EAST;
    public static final BooleanProperty BEAM_S = BlockStateProperties.SOUTH;
    public static final BooleanProperty BEAM_W = BlockStateProperties.WEST;

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BEAM_N, BEAM_E, BEAM_S, BEAM_W);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);

        boolean isBeam = stack.getItem() instanceof BlockItem
        && state.getBlock() instanceof BlockKumimono
        && ((BlockItem) stack.getItem()).getBlock() instanceof BlockKawaki
        && ((BlockKawaki) ((BlockItem) stack.getItem()).getBlock()).getType().equals(((BlockKumimono) state.getBlock()).getType());

        if (isBeam || stack.getItem() instanceof AxeItem)
        {
            SoundEvent event = isBeam ? SoundEvents.WOOD_PLACE : SoundEvents.WOOD_BREAK;
            BooleanProperty dir = BEAM_N;
            Direction facing = player.getDirection();
            switch (facing)
            {
                case NORTH:
                {
                    dir = BEAM_N;
                    if (state.getValue(BEAM_S) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_S, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.getValue(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    break;
                }
                case EAST:
                {
                    if (state.getValue(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.getValue(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_E;
                    break;
                }
                case SOUTH:
                {
                    if (state.getValue(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.getValue(BEAM_S) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_S, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_S;
                    break;
                }
                case WEST:
                {
                    if (state.getValue(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.getValue(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                    dir = BEAM_W;
                }
            }
            BlockState expandedState = worldIn.getBlockState(pos.relative(player.getDirection().getOpposite()));

            boolean expandable = expandedState.getBlock() instanceof BlockKumimono
            && state.getBlock() instanceof BlockKumimono
            && ((BlockKumimono) expandedState.getBlock()).getType().equals(((BlockKumimono) state.getBlock()).getType());

            if (isBeam && expandable && !expandedState.getValue(dir))
            {
                worldIn.setBlockAndUpdate(pos.relative(player.getDirection().getOpposite()), expandedState.setValue(dir, true));
                worldIn.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape body = box(2.5d, 0.0d, 2.5d, 13.5d, 10.0d, 13.5d);
        VoxelShape beam_n = box(5.0d, 6.0d, 0.0d, 11.0d, 16.0d, 8.0d);
        VoxelShape beam_e = box(8.0d, 6.0d, 5.0d, 16.0d, 16.0d, 11.0d);
        VoxelShape beam_s = box(5.0d, 6.0d, 8.0d, 11.0d, 16.0d, 16.0d);
        VoxelShape beam_w = box(0.0d, 6.0d, 5.0d, 8.0d, 16.0d, 11.0d);

        if (state.getValue(BEAM_N)) body = VoxelShapes.or(body, beam_n);
        if (state.getValue(BEAM_E)) body = VoxelShapes.or(body, beam_e);
        if (state.getValue(BEAM_S)) body = VoxelShapes.or(body, beam_s);
        if (state.getValue(BEAM_W)) body = VoxelShapes.or(body, beam_w);

        return body;
    }

    @Override
    public AshiharaWoodTypes getType() {return type;}
}
