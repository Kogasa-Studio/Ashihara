package kogasastudio.ashihara.block;

import kogasastudio.ashihara.helper.BlockActionHelper;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceExpansionBlock extends Block implements IVariable<AshiharaWoodTypes>
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<DecorationTypes> DECORATION = EnumProperty.create("decoration", DecorationTypes.class);
    public static AshiharaWoodTypes type;

    public FenceExpansionBlock(AshiharaWoodTypes typeIn)
    {
        super
                (
                        Properties.of(Material.WOOD)
                                .strength(0.2F)
                                .sound(SoundType.WOOD)
                );
        type = typeIn;
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, DECORATION);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState connectedState = worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        return BlockActionHelper.typeMatches(state, connectedState) && connectedState.getBlock() instanceof AdvancedFenceBlock;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);

        if (state.getValue(DECORATION).equals(DecorationTypes.NONE) && stack.getItem().equals(Items.GOLD_INGOT))
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(DECORATION, DecorationTypes.GOLD));
            worldIn.playSound(player, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative()) player.getItemInHand(handIn).shrink(1);

            return InteractionResult.SUCCESS;
        } else if (!state.getValue(DECORATION).equals(DecorationTypes.NONE) && stack.isEmpty())
        {
            player.setItemInHand(handIn, new ItemStack(Items.GOLD_INGOT));
            worldIn.setBlockAndUpdate(pos, state.setValue(DECORATION, DecorationTypes.NONE));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape n_down = box(6.5d, 6.0d, 8.0d, 9.5d, 9.0d, 16.0d);
        VoxelShape n_up = box(6.5d, 12.0d, 8.0d, 9.5d, 15.0d, 16.0d);
        VoxelShape w_down = box(8.0d, 6.0d, 6.5d, 16.0d, 9.0d, 9.5d);
        VoxelShape w_up = box(8.0d, 12.0d, 6.5d, 16.0d, 15.0d, 9.5d);
        VoxelShape s_down = box(6.5d, 6.0d, 0.0d, 9.5d, 9.0d, 8.0d);
        VoxelShape s_up = box(6.5d, 12.0d, 0.0d, 9.5d, 15.0d, 8.0d);
        VoxelShape e_down = box(0.0d, 6.0d, 6.5d, 8.0d, 9.0d, 9.5d);
        VoxelShape e_up = box(0.0d, 12.0d, 6.5d, 8.0d, 15.0d, 9.5d);

        VoxelShape n = Shapes.or(n_up, n_down);
        VoxelShape w = Shapes.or(w_up, w_down);
        VoxelShape s = Shapes.or(s_up, s_down);
        VoxelShape e = Shapes.or(e_up, e_down);

        switch (state.getValue(FACING))
        {
            case EAST:
                return e;
            case SOUTH:
                return s;
            case WEST:
                return w;
            default:
                return n;
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
        return new ItemStack(Items.STICK);
    }

    public enum DecorationTypes implements StringRepresentable
    {
        NONE("none"),
        GOLD("gold");
//        COPPER("copper");

        private final String name;

        DecorationTypes(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
