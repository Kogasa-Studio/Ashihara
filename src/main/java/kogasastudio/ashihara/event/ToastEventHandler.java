package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.IRenderInWorldToolTip;
import kogasastudio.ashihara.client.gui.InWorldToolTipTipToast;
import kogasastudio.ashihara.registry.KeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Ashihara.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ToastEventHandler
{
    private static int toastTipTicks = 0;
    private static boolean showedToast = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        if (Minecraft.getInstance().hitResult == null || !(Minecraft.getInstance().hitResult instanceof BlockHitResult) || Minecraft.getInstance().level == null)
            return;
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
        } else toastTipTicks = 0;
    }

    @SubscribeEvent
    public static void onLogOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        showedToast = false;
    }

}
