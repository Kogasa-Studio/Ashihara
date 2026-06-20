package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Ashihara.MODID)
public class BasicEventHandler
{
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        BlockState clickState = world.getBlockState(pos);
        Player player = event.getEntity();
        //对于上方无方块的操作
        if (world.getBlockState(pos.above()).getBlock() == net.minecraft.world.level.block.Blocks.AIR)
        {
            //铲土洼
            if (item.getItem() instanceof ShovelItem)
            {
                if (clickState.is(net.minecraft.world.level.block.Blocks.DIRT) || (player.isShiftKeyDown() && clickState.is(net.minecraft.world.level.block.Blocks.DIRT_PATH)))
                {
                    world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.setBlockAndUpdate(pos, Blocks.DIRT_DEPRESSION.get().defaultBlockState());
                    player.swing(event.getHand());
                    Containers.dropItemStack(world, pos.getX(), pos.getY() + 0.5F, pos.getZ(), new ItemStack(Items.DIRT_BALL.get()));
                    if (!player.getAbilities().instabuild)
                    {
                        item.hurtAndBreak(1, player, item.getEquipmentSlot());
                    }
                    return;
                }
                if (player.isShiftKeyDown() && clickState.is(BlockTags.SAND))
                {
                    world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.setBlockAndUpdate(pos, Blocks.SALT_FIELD.get().defaultBlockState());
                    player.swing(event.getHand());
                    if (!player.getAbilities().instabuild)
                    {
                        item.hurtAndBreak(1, player, item.getEquipmentSlot());
                    }
                    return;
                }
            }
        }

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
                if (player != null)
                {
                    player.swing(event.getHand());
                    if (!player.getAbilities().instabuild)
                    {
                        item.hurtAndBreak(1, player, item.getEquipmentSlot());
                    }
                }
            }
        }
    }
}
