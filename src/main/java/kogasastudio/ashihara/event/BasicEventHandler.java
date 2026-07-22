package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import kogasastudio.ashihara.block.blockentity.AshiharaCommonBE;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Ashihara.MODID)
public class BasicEventHandler
{
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void onRightClickBlockDenied(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getLevel().isClientSide()) return;
        if (!event.isCanceled()) return;
        if (event.getLevel().getBlockEntity(event.getPos()) instanceof AshiharaCommonBE be)
            be.sync();
    }


    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event)
    {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        BlockState clickState = world.getBlockState(pos);
        Player player = event.getEntity();

        //锄头事件
        if (item.getItem() instanceof HoeItem)
        {
            //矫正位置偏移
            BlockHitResult result = event.getHitVec();
            if (clickState.is(Blocks.DIRT_DEPRESSION.get()))
            {
                //相当于手动放置
                world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                BlockState placement = Blocks.WATER_FIELD.get().getStateForPlacement(new BlockPlaceContext(world, player, event.getHand(), item, result));
                world.setBlock(pos, placement == null ? Blocks.WATER_FIELD.get().defaultBlockState() : placement, 1);
                player.swing(event.getHand());
                item.hurtAndBreak(1, player, event.getHand());
            }
        }
    }
}
