package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KumimonoBlock extends Block implements IVariable<AshiharaWoodTypes>
{
    public static final BooleanProperty BEAM_N = BlockStateProperties.NORTH;
    public static final BooleanProperty BEAM_E = BlockStateProperties.EAST;
    public static final BooleanProperty BEAM_S = BlockStateProperties.SOUTH;
    public static final BooleanProperty BEAM_W = BlockStateProperties.WEST;
    private static AshiharaWoodTypes type;

    public KumimonoBlock(AshiharaWoodTypes typeIn)
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BEAM_N, BEAM_E, BEAM_S, BEAM_W);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);

        boolean isBeam = stack.getItem() instanceof BlockItem
                && state.getBlock() instanceof KumimonoBlock
                && ((BlockItem) stack.getItem()).getBlock() instanceof KawakiBlock
                && ((KawakiBlock) ((BlockItem) stack.getItem()).getBlock()).getType().equals(((KumimonoBlock) state.getBlock()).getType());

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
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (state.getValue(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                    break;
                }
                case EAST:
                {
                    if (state.getValue(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (state.getValue(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                    dir = BEAM_E;
                    break;
                }
                case SOUTH:
                {
                    if (state.getValue(BEAM_N) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_N, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (state.getValue(BEAM_S) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_S, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                    dir = BEAM_S;
                    break;
                }
                case WEST:
                {
                    if (state.getValue(BEAM_E) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_E, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (state.getValue(BEAM_W) != isBeam)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(BEAM_W, isBeam));
                        worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                    dir = BEAM_W;
                }
            }
            BlockState expandedState = worldIn.getBlockState(pos.relative(player.getDirection().getOpposite()));

            boolean expandable = expandedState.getBlock() instanceof KumimonoBlock
                    && state.getBlock() instanceof KumimonoBlock
                    && ((KumimonoBlock) expandedState.getBlock()).getType().equals(((KumimonoBlock) state.getBlock()).getType());

            if (isBeam && expandable && !expandedState.getValue(dir))
            {
                worldIn.setBlockAndUpdate(pos.relative(player.getDirection().getOpposite()), expandedState.setValue(dir, true));
                worldIn.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape body = box(2.5d, 0.0d, 2.5d, 13.5d, 10.0d, 13.5d);
        VoxelShape beam_n = box(5.0d, 6.0d, 0.0d, 11.0d, 16.0d, 8.0d);
        VoxelShape beam_e = box(8.0d, 6.0d, 5.0d, 16.0d, 16.0d, 11.0d);
        VoxelShape beam_s = box(5.0d, 6.0d, 8.0d, 11.0d, 16.0d, 16.0d);
        VoxelShape beam_w = box(0.0d, 6.0d, 5.0d, 8.0d, 16.0d, 11.0d);

        if (state.getValue(BEAM_N)) body = Shapes.or(body, beam_n);
        if (state.getValue(BEAM_E)) body = Shapes.or(body, beam_e);
        if (state.getValue(BEAM_S)) body = Shapes.or(body, beam_s);
        if (state.getValue(BEAM_W)) body = Shapes.or(body, beam_w);

        return body;
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return type;
    }
}
