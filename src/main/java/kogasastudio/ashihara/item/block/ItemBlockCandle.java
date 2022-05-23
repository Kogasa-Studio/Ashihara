package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockCandle;
import kogasastudio.ashihara.block.tileentities.CandleTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import static kogasastudio.ashihara.Ashihara.BUILDING_BLOCKS;
import static kogasastudio.ashihara.block.BlockRegistryHandler.CANDLE;
import static kogasastudio.ashihara.block.tileentities.TERegistryHandler.CANDLE_TE;
import static net.minecraft.block.Blocks.AIR;

import net.minecraft.item.Item.Properties;

public class ItemBlockCandle extends BlockItem
{
    public ItemBlockCandle() {super(CANDLE.get(), new Properties().tab(BUILDING_BLOCKS));}

    @Override
    public ActionResultType place(BlockItemUseContext context)
    {
        ItemStack stack = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        Vector3d hitVec = context.getClickLocation();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (state.is(CANDLE.get()))
        {
            if (state.getValue(BlockCandle.MULTIPLE))
            {
                TileEntity te = world.getBlockEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    if (candle.addCurrentCandle(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    else return super.place(context);
                }
                else throw new IllegalStateException("the candle doesn't have a correct te wut happened");
            }
            else
            {
                world.setBlockAndUpdate(pos, state.setValue(BlockCandle.MULTIPLE, true));
                TileEntity te = world.getBlockEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    candle.init();
                    if (candle.addCurrentCandle(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    else return super.place(context);
                }
                else throw new IllegalStateException("te create failed...");
            }
        }
        else if (state.is(AIR) && player != null && player.isShiftKeyDown())
        {
            world.setBlockAndUpdate(pos, CANDLE.get().defaultBlockState().setValue(BlockCandle.MULTIPLE, true));
            TileEntity te = world.getBlockEntity(pos);
            if (te != null && te.getType().equals(CANDLE_TE.get()))
            {
                CandleTE candle = (CandleTE) te;
                candle.init(hitVec.x() - pos.getX(), hitVec.z() - pos.getZ());
                if (!player.isCreative()) stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            else throw new IllegalStateException("te create failed...");
        }
        else return super.place(context);
    }
}
