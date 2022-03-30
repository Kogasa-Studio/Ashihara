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

public class ItemBlockCandle extends BlockItem
{
    public ItemBlockCandle() {super(CANDLE.get(), new Properties().group(BUILDING_BLOCKS));}

    @Override
    public ActionResultType tryPlace(BlockItemUseContext context)
    {
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        Vector3d hitVec = context.getHitVec();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.matchesBlock(CANDLE.get()))
        {
            if (state.get(BlockCandle.MULTIPLE))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    if (candle.addCurrentCandle(hitVec.getX() - pos.getX(), hitVec.getZ() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    else return super.tryPlace(context);
                }
                else throw new IllegalStateException("the candle doesn't have a correct te wut happened");
            }
            else
            {
                world.setBlockState(pos, state.with(BlockCandle.MULTIPLE, true));
                TileEntity te = world.getTileEntity(pos);
                if (te != null && te.getType().equals(CANDLE_TE.get()))
                {
                    CandleTE candle = (CandleTE) te;
                    candle.init();
                    if (candle.addCurrentCandle(hitVec.getX() - pos.getX(), hitVec.getZ() - pos.getZ(), world.getRandom()))
                    {
                        if (player != null && !player.isCreative()) stack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    else return super.tryPlace(context);
                }
                else throw new IllegalStateException("te create failed...");
            }
        }
        else if (state.matchesBlock(AIR) && player != null && player.isSneaking())
        {
            world.setBlockState(pos, CANDLE.get().getDefaultState().with(BlockCandle.MULTIPLE, true));
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te.getType().equals(CANDLE_TE.get()))
            {
                CandleTE candle = (CandleTE) te;
                candle.init(hitVec.getX() - pos.getX(), hitVec.getZ() - pos.getZ());
                if (!player.isCreative()) stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            else throw new IllegalStateException("te create failed...");
        }
        else return super.tryPlace(context);
    }
}
