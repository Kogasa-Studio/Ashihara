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
import net.minecraft.world.level.material.MapColor;
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
                        Properties.of()
                                .mapColor(MapColor.WOOD)
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
        VoxelShape n_down = box(5, 0, 8, 11, 3, 16);
        VoxelShape n_mid = box(5, 6, 8, 11, 11, 16);
        VoxelShape n_up = box(6.5, 11.5, 8, 9.5, 16, 16);
        VoxelShape w_down = box(8, 0, 5, 16, 3, 11);
        VoxelShape w_mid = box(8, 6, 5, 16, 11, 11);
        VoxelShape w_up = box(8, 11.5, 6.5, 16, 16, 9.5);
        VoxelShape s_down = box(5, 0, 0, 11, 3, 8);
        VoxelShape s_mid = box(5, 6, 0, 11, 11, 8);
        VoxelShape s_up = box(6.5, 11.5, 0, 9.5, 16, 8);
        VoxelShape e_down = box(0, 0, 5, 8, 3, 11);
        VoxelShape e_mid = box(0, 6, 5, 8, 11, 11);
        VoxelShape e_up = box(0, 11.5, 6.5, 8, 16, 9.5);

        VoxelShape n = Shapes.or(n_up, n_mid, n_down);
        VoxelShape w = Shapes.or(w_up, w_mid, w_down);
        VoxelShape s = Shapes.or(s_up, s_mid, s_down);
        VoxelShape e = Shapes.or(e_up, e_mid, e_down);

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
