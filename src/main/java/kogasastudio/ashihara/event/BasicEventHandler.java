package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.IRenderInWorldToolTip;
import kogasastudio.ashihara.client.gui.InWorldToolTipTipToast;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.registry.KeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Ashihara.MODID)
public class BasicEventHandler
{
    private static int toastTipTicks = 0;
    private static boolean showedToast = false;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        if (Minecraft.getInstance().hitResult == null || Minecraft.getInstance().level == null) return;
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(((BlockHitResult) Minecraft.getInstance().hitResult).getBlockPos());
        if (be instanceof IRenderInWorldToolTip iBe)
        {
            if (!showedToast)
            {
                toastTipTicks++;
                if (toastTipTicks >= 100)
                {
                    Minecraft.getInstance().getToasts().addToast(new InWorldToolTipTipToast());
                    showedToast = true;
                }
            }

            if (KeyMappings.SHOW_IN_WORLD_TOOLTIP.consumeClick() && Minecraft.getInstance().player != null)
            {
                iBe.switchRender(Minecraft.getInstance().player);
                showedToast = true;
            }
        }
        else toastTipTicks = 0;
    }

    @SubscribeEvent
    public static void onLogOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        showedToast = false;
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        BlockState clickState = world.getBlockState(pos);
        Player player = event.getEntity();
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
                    item.hurtAndBreak(1, player, item.getEquipmentSlot());
                }
                return;
            }
        }

        //锄头事件
        if (item.getItem() instanceof HoeItem)
        {
            //矫正位置偏移
            BlockHitResult result = event.getHitVec();
            if (clickState.is(BlockRegistryHandler.DIRT_DEPRESSION.get()))
            {
                //相当于手动放置
                world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                BlockState placement = BlockRegistryHandler.WATER_FIELD.get().getStateForPlacement(new BlockPlaceContext(world, player, event.getHand(), item, result));
                world.setBlock(pos, placement == null ? BlockRegistryHandler.WATER_FIELD.get().defaultBlockState() : placement, 1);
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
