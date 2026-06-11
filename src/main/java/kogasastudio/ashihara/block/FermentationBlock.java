package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FermentationBlock extends Block implements EntityBlock, FermentationBlockEntity.IFermentationSizeProvider
{
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");

    protected final FermentationBlockEntity.Size size;

    protected FermentationBlock(BlockBehaviour.Properties properties, FermentationBlockEntity.Size size)
    {
        super(properties
            .mapColor(MapColor.WOOD)
            .strength(2.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()
            .dynamicShape());
        this.size = size;
        registerDefaultState(stateDefinition.any().setValue(HAS_LID, true));
    }

    @Override public FermentationBlockEntity.Size getSize() { return size; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HAS_LID);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        FermentationBlockEntity be = getBE(level, pos, state);
        if (be == null) return InteractionResult.PASS;
        return InteractionResult.PASS;
    }

    @Nullable
    public static FermentationBlockEntity getBE(Level level, BlockPos clickPos, BlockState clickState)
    {
        if (!(clickState.getBlock() instanceof FermentationBlock)) return null;
        return level.getBlockEntity(clickPos) instanceof FermentationBlockEntity be ? be : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getFullShape(state.getValue(HAS_LID));
    }

    protected abstract VoxelShape getFullShape(boolean hasLid);

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getShape(state, level, pos, ctx);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder)
    {
        return java.util.Collections.singletonList(new ItemStack(this));
    }

    @Override @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FermentationBlockEntity(pos, state);
    }
}