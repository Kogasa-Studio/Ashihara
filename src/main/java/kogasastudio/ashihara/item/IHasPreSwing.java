package kogasastudio.ashihara.item;

import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.utils.PrePostSwingHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface IHasPreSwing
{
    default void prepareAttack(ItemStack stack, Player player, Entity target)
    {
        InteractionHand hand = player.getMainHandItem().is(stack.getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        player.setData(DataComponentTypes.PRE_SWING_REMAINING, new PrePostSwingHandler(stack, hand, getDelayValue(false)));
        playPrePostSwingAnim(player);
    }

    void actuallyAttack(ItemStack stack, Player entity);

    default void playPrePostSwingAnim(Player player) {}

    default void prepareUse(UseOnContext context)
    {
        if (context.getPlayer() == null) return;
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (!player.hasData(DataComponentTypes.PRE_SWING_REMAINING))
        {
            InteractionHand hand = player.getMainHandItem().is(stack.getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            player.setData(DataComponentTypes.PRE_SWING_REMAINING, new PrePostSwingHandler(stack, hand, true, getDelayValue(true)));
            playPrePostSwingAnim(player);
        }
        else
        {
            PrePostSwingHandler handler = player.getData(DataComponentTypes.PRE_SWING_REMAINING);
            if (!handler.isUse())
            {
                handler.setUse(true);
                player.setData(DataComponentTypes.PRE_SWING_REMAINING, handler);
            }
        }
    }
    void actuallyUse(ItemStack stack, Player entity);

    int getDelayValue(boolean isUse);

    interface AttackOnly extends IHasPreSwing
    {
        @Override
        default void prepareUse(UseOnContext context) {}

        @Override
        default void actuallyUse(ItemStack stack, Player entity) {}
    }

    interface UseOnly extends IHasPreSwing
    {
        @Override
        default void prepareAttack(ItemStack stack, Player player, Entity target) {}

        @Override
        default void actuallyAttack(ItemStack stack, Player entity) {}
    }
}
