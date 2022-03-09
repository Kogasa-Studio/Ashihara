package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public class BlockLantern extends Block
{
    public BlockLantern(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.FALSE).with(FACING, Direction.NORTH));
    }

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(player.getHeldItem(handIn).getItem() == Items.AIR)
        {
            Random random = worldIn.getRandom();
            Boolean instantState = worldIn.getBlockState(pos).get(LIT);
            worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockState(pos, state.with(LIT, !instantState));
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) //不明所以地类似注册BS的东西
    {
        builder.add(LIT, FACING);
    }
}
