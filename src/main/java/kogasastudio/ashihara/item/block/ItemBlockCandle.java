package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.CandleBlock;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static kogasastudio.ashihara.Ashihara.BUILDING_BLOCKS;
import static kogasastudio.ashihara.block.BlockRegistryHandler.CANDLE;
import static kogasastudio.ashihara.block.tileentities.TERegistryHandler.CANDLE_TE;
import static net.minecraft.world.level.block.Blocks.AIR;

public class ItemBlockCandle extends BlockItem
{
    public ItemBlockCandle()
    {
        super(CANDLE.get(), new Properties().tab(BUILDING_BLOCKS));
    }

    @Override
    public InteractionResult place(BlockPlaceContext context)
    {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        Vec3 hitVec = context.getClickLocation();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (state.is(CANDLE.get()))
        {
            if (state.getValue(CandleBlock.MULTIPLE))
            {
                BlockEntity te = world.getBlockEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    if (candle.addCurrentCandle(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else return super.place(context);
                } else throw new IllegalStateException("the candle doesn't have a correct te wut happened");
            } else
            {
                world.setBlockAndUpdate(pos, state.setValue(CandleBlock.MULTIPLE, true));
                BlockEntity te = world.getBlockEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    candle.init();
                    if (candle.addCurrentCandle(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else return super.place(context);
                } else throw new IllegalStateException("te create failed...");
            }
        } else if (state.is(AIR) && player != null && player.isShiftKeyDown())
        {
            world.setBlockAndUpdate(pos, CANDLE.get().defaultBlockState().setValue(CandleBlock.MULTIPLE, true));
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && te.getType().equals(CANDLE_TE.get()))
            {
                CandleTE candle = (CandleTE) te;
                candle.init(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ());
                if (!player.isCreative()) stack.shrink(1);
                return InteractionResult.SUCCESS;
            } else throw new IllegalStateException("te create failed...");
        } else return super.place(context);
    }
}
