package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.building.AbstractWallBlock;
import kogasastudio.ashihara.block.building.AbstractBeamBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemBlockBeam extends BlockItem
{
    public ItemBlockBeam()
    {
        super(BlockRegistryHandler.RED_THIN_BEAM.get(), new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clicked = level.getBlockState(clickedPos);
        if (clicked.getBlock() instanceof AbstractWallBlock)
        {
            BlockState state = this.getBlock().getStateForPlacement(new BlockPlaceContext(context));
            state = state.setValue(AbstractBeamBlock.WALL_TYPE, ((AbstractWallBlock) clicked.getBlock()).getType());
            state = state.setValue(AbstractBeamBlock.WALL_FILL_TYPE, AbstractBeamBlock.WallFillType.ALL);
            state = ((AbstractBeamBlock) state.getBlock()).updateState(level, clickedPos, state);
            level.setBlockAndUpdate(clickedPos, state);
            level.playSound(context.getPlayer(), clickedPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (context.getPlayer() != null && !context.getPlayer().isCreative()) context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
