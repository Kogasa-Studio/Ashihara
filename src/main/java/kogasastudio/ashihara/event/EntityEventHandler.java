package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import kogasastudio.ashihara.item.IHasHoldAnim;
import kogasastudio.ashihara.item.IHasPreSwing;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import kogasastudio.ashihara.utils.ItemHoldAnimHandler;
import kogasastudio.ashihara.utils.PrePostSwingHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Ashihara.MODID)
public class EntityEventHandler
{
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event)
    {
        if (event.getEntity().getWeaponItem().getItem() instanceof IHasPreSwing preSwingItem)
        {
            if (!event.getEntity().hasData(DataAttachmentTypes.PRE_SWING_REMAINING)) preSwingItem.prepareAttack(event.getEntity().getWeaponItem(), event.getEntity(), event.getTarget());
            PrePostSwingHandler handler = event.getEntity().getData(DataAttachmentTypes.PRE_SWING_REMAINING);
            if (handler.isUse())
            {
                handler.setUse(false);
                event.getEntity().setData(DataAttachmentTypes.PRE_SWING_REMAINING, handler);
            }
            if (handler.getTicksRemain() > 0) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event)
    {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (livingEntity instanceof Player player)
        {
            for (InteractionHand hand : InteractionHand.values())
            {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof IHasHoldAnim a)
                {
                    boolean playHoldAnim = true;
                    if (player.hasData(DataAttachmentTypes.ITEM_PLAYING_HOLDING_ANIM))
                    {
                        playHoldAnim = false;
                        Item item = player.getData(DataAttachmentTypes.ITEM_PLAYING_HOLDING_ANIM).getItem();
                        InteractionHand hand2 = InteractionHand.MAIN_HAND;
                        if (hand == hand2 && item != stack.getItem()) playHoldAnim = true;
                    }
                    if (playHoldAnim)
                    {
                        player.setData(DataAttachmentTypes.ITEM_PLAYING_HOLDING_ANIM, new ItemHoldAnimHandler(stack.getItem(), hand));
                        PlayerAnimationHelper.pushPlayerAnimation(player, a.getHoldAnim());
                    }
                    break;
                }
                else player.removeData(DataAttachmentTypes.ITEM_PLAYING_HOLDING_ANIM);
            }

            if (player.hasData(DataAttachmentTypes.PRE_SWING_REMAINING))
            {
                PrePostSwingHandler handler = player.getData(DataAttachmentTypes.PRE_SWING_REMAINING);
                ItemStack stack = handler.getItem();
                if (!player.getItemInHand(handler.getHand()).equals(stack) || !(stack.getItem() instanceof IHasPreSwing item))
                {
                    player.removeData(DataAttachmentTypes.PRE_SWING_REMAINING);
                    return;
                }
                handler.tick();
                if (handler.getTicksRemain() <= 0)
                {
                    if (handler.isUse())
                    {
                        item.actuallyUse(stack, player);
                    }
                    else item.actuallyAttack(stack, player);
                    player.removeData(DataAttachmentTypes.PRE_SWING_REMAINING);
                }
            }
        }
    }
}
