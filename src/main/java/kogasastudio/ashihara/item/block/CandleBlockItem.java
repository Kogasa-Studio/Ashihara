package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.CandleBlock;
import kogasastudio.ashihara.block.blockentity.CandleBE;
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

import static kogasastudio.ashihara.registry.Blocks.CANDLE;
import static kogasastudio.ashihara.registry.BlockEntities.CANDLE_BE;
import static net.minecraft.world.level.block.Blocks.AIR;

public class CandleBlockItem extends BlockItem
{
    public CandleBlockItem(Properties properties)
    {
        super(CANDLE.get(), properties.useBlockDescriptionPrefix());
    }

    public CandleBlockItem()
    {
        this(new Properties());
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
                if (te != null && te.getType().equals(CANDLE_BE.get()))
                {
                    CandleBE candle = (CandleBE) te;
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
                if (te != null && te.getType().equals(CANDLE_BE.get()))
                {
                    CandleBE candle = (CandleBE) te;
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
            if (te != null && te.getType().equals(CANDLE_BE.get()))
            {
                CandleBE candle = (CandleBE) te;
                candle.init(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ());
                if (!player.isCreative()) stack.shrink(1);
                return InteractionResult.SUCCESS;
            } else throw new IllegalStateException("te create failed...");
        } else return super.place(context);
    }
}
