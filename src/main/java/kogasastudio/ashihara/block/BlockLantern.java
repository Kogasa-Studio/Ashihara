package kogasastudio.ashihara.block;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockLantern extends Block
{
    public BlockLantern(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.FALSE).setValue(FACING, Direction.NORTH));
    }

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if(player.getItemInHand(handIn).getItem() == Items.AIR)
        {
            Random random = worldIn.getRandom();
            Boolean instantState = worldIn.getBlockState(pos).getValue(LIT);
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, !instantState));
            return InteractionResult.SUCCESS;
        }
        else return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, FACING);
    }
}
