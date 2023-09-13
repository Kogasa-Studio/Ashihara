package kogasastudio.ashihara.item.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemBlockDouble extends BlockItem
{
    public ItemBlockDouble(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return super.canPlace(context, state) && level.getBlockState(pos.above()).canBeReplaced();
    }
}
