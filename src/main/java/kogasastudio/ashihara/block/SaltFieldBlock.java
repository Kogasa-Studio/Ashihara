package kogasastudio.ashihara.block;

import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SaltFieldBlock extends PaddyFieldBlock
{
    public static final IntegerProperty SALT_COUNT = IntegerProperty.create("salt_count", 0, 4);

    public SaltFieldBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_WATER, false).setValue(SALT_COUNT, 0));
    }

    @Override
    protected boolean matchesWaterField(BlockState state)
    {
        return state.is(Blocks.SALT_FIELD);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SALT_COUNT);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(HAS_WATER) && state.getValue(LEVEL) > 4;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!isRandomlyTicking(state)) return;
        BlockState newState = state;
        int i = random.nextInt(10);
        if (i > 3)
        {
            newState = newState.setValue(SALT_COUNT, Math.min(4, state.getValue(SALT_COUNT) + random.nextInt(3)));
            level.setBlockAndUpdate(pos, newState);
        }
        if (i == 0 && !level.isRainingAt(pos))
        {
            newState = newState.setValue(LEVEL, Math.max(state.getValue(LEVEL) - 1, 4));
            level.setBlockAndUpdate(pos, newState);
            this.tick(state, level, pos, random);
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (itemStack.getItem() instanceof ShovelItem && state.getValue(SALT_COUNT) > 0)
        {
            level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            popResourceFromFace(level, pos, Direction.UP, Items.SALT.toStack(state.getValue(SALT_COUNT)));
            level.setBlockAndUpdate(pos, state.setValue(SALT_COUNT, 0));
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
}
