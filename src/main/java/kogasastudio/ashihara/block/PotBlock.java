package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.gui.PotScreen;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotBlock extends Block
{
    public static final VoxelShape NORMAL = Shapes.or
    (
        Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
        Shapes.box(0.8125, 0.0625, 0.125, 0.875, 0.5, 0.875),
        Shapes.box(0.125, 0.0625, 0.8125, 0.8125, 0.5, 0.875),
        Shapes.box(0.875, 0.4375, 0.0625, 0.9375, 0.5, 0.9375),
        Shapes.box(0.0625, 0.4375, 0.875, 0.875, 0.5, 0.9375),
        Shapes.box(0.125, 0.4375, 0.0625, 0.875, 0.5, 0.125),
        Shapes.box(0.1875, 0.0625, 0.125, 0.8125, 0.5, 0.1875),
        Shapes.box(0.125, 0.0625, 0.125, 0.1875, 0.5, 0.8125),
        Shapes.box(0.0625, 0.4375, 0.0625, 0.125, 0.5, 0.875)
    );
    public static final VoxelShape NORMAL_WITH_LID = Shapes.or
    (
        NORMAL,
        Shapes.box(0.125, 0.5, 0.125, 0.875, 0.5625, 0.875),
        Shapes.box(0.13125, 0.5625, 0.4375, 0.86875, 0.6875, 0.5625)
    );
    public static final VoxelShape ON_STOVE_SHAPE = ShapeHelper.offsetShape(NORMAL, 0, -2 / 16d, 0);
    public static final VoxelShape ON_STOVE_WITH_LID = ShapeHelper.offsetShape(NORMAL_WITH_LID, 0, -2 / 16d, 0);

    public static final BooleanProperty ON_STOVE = BooleanProperty.create("on_stove");
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");

    public PotBlock()
    {
        super
        (
            Properties.of()
            .noOcclusion()
            .mapColor(MapColor.TERRACOTTA_CYAN)
            .strength(0.5F)
            .sound(SoundType.LANTERN)
        );
        this.registerDefaultState(defaultBlockState().setValue(HAS_LID, true).setValue(ON_STOVE, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(ON_STOVE))
        {
            return state.getValue(HAS_LID) ? ON_STOVE_WITH_LID : ON_STOVE_SHAPE;
        }
        else return state.getValue(HAS_LID) ? NORMAL_WITH_LID : NORMAL;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (state.getValue(ON_STOVE)) return level.getBlockState(pos.below()).getBlock() == Blocks.DIRT_COOKSTOVE.get();
        else return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (context.getLevel().getBlockState(context.getClickedPos().below()).is(Blocks.DIRT_COOKSTOVE)) return defaultBlockState().setValue(ON_STOVE, true);
        return defaultBlockState();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.isEmpty() && player.isShiftKeyDown())
        {
            if (level.isClientSide()) Minecraft.getInstance().setScreen(new PotScreen());
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.isEmpty() && state.getValue(HAS_LID))
        {
            player.setItemInHand(hand, Items.POT_LID.toStack());
            level.setBlockAndUpdate(pos, state.setValue(HAS_LID, false));
            return ItemInteractionResult.SUCCESS;
        }
        else if (stack.is(Items.POT_LID))
        {
            stack.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(HAS_LID, true));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HAS_LID);
        builder.add(ON_STOVE);
    }
}
