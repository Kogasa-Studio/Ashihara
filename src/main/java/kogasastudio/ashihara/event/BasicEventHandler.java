package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ashihara.MODID)
public class BasicEventHandler
{
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Level world = event.getWorld();
        BlockState clickState = world.getBlockState(pos);
        Player player = event.getPlayer();
        //对于上方无方块的操作
        if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR)
        {
            //铲土洼
            if (item.getItem() instanceof ShovelItem && (clickState.is(Blocks.DIRT) || (player.isShiftKeyDown() && clickState.is(Blocks.DIRT_PATH))))
            {
                world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.setBlockAndUpdate(pos, BlockRegistryHandler.DIRT_DEPRESSION.get().defaultBlockState());
                player.swing(event.getHand());
                Containers.dropItemStack(world, pos.getX(), pos.getY() + 0.5F, pos.getZ(), new ItemStack(ItemRegistryHandler.DIRT_BALL.get()));
                if (!player.getAbilities().instabuild)
                {
                    item.hurtAndBreak(1, player, (playerEntity) -> player.broadcastBreakEvent(event.getHand()));
                }
            }
        }
    }

    //锄头事件
    // todo 这个事件下个版本就寄了，你想一下别的办法吧
    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event)
    {
        UseOnContext context = event.getContext();
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState clickState = world.getBlockState(pos);
        Player player = context.getPlayer();
        //矫正位置偏移
        BlockHitResult result = new BlockHitResult
                (
                        context.getClickLocation(),
                        context.getClickedFace(),
                        pos.below(),
                        context.isInside()
                );
        if (clickState.is(BlockRegistryHandler.DIRT_DEPRESSION.get()))
        {
            //相当于手动放置
            world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState placement = BlockRegistryHandler.WATER_FIELD.get().getStateForPlacement(new BlockPlaceContext(world, player, context.getHand(), context.getItemInHand(), result));
            world.setBlock(pos, placement == null ? BlockRegistryHandler.WATER_FIELD.get().defaultBlockState() : placement, 1);
            if (player != null)
            {
                player.swing(context.getHand());
            }
            if (player != null && !player.getAbilities().instabuild)
            {
                context.getItemInHand().hurtAndBreak(1, player, (playerEntity) -> player.broadcastBreakEvent(context.getHand()));
            }
        }
    }
}
