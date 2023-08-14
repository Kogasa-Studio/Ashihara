package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LanternBlock extends Block
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public LanternBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        RandomSource random = worldIn.getRandom();
        Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
        if (player.getItemInHand(handIn).getItem().equals(Items.FLINT_AND_STEEL) && !instantState)
        {
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, true));
            return InteractionResult.SUCCESS;
        }
        else if (player.getItemInHand(handIn).getItem().equals(Items.AIR) && instantState)
        {
            worldIn.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, false));
            return InteractionResult.SUCCESS;
        }
        else return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }
}
