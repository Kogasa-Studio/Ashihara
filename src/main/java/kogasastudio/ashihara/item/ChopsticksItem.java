package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.datacomponent.ChopsticksFood;
import kogasastudio.ashihara.helper.BowlFoodHelper;
import kogasastudio.ashihara.registry.DataComponentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class ChopsticksItem extends Item implements IContainerItem
{
    public ChopsticksItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        ItemStack chopsticks = player.getItemInHand(hand);
        ChopsticksFood food = chopsticks.getOrDefault(DataComponentTypes.CHOPSTICKS_FOOD.get(), ChopsticksFood.EMPTY);

        if (!food.food().isEmpty())
        {
            if (player.isShiftKeyDown())
            {
                if (!level.isClientSide())
                {
                    /*ItemStack drop = food.food().copy();
                    if (!player.getInventory().add(drop)) player.drop(drop, false);*/
                    chopsticks.remove(DataComponentTypes.CHOPSTICKS_FOOD.get());
                    BowlFoodHelper.clear(chopsticks);
                }
                return InteractionResult.SUCCESS;
            }
            else
            {
                Consumable consumable = chopsticks.get(DataComponents.CONSUMABLE);
                boolean hasContent = consumable != null && consumable.canConsume(player, chopsticks);
                if (hasContent) return consumable.startConsuming(player, chopsticks, hand);
                return InteractionResult.FAIL;
            }
        }

        // Empty: try offhand container grab (placed-container grab is handled via block useItemOn)
        ItemStack offhand = player.getOffhandItem();
        var offContent = ContainerComponent.getContent(offhand);
        if (!offContent.isEmpty() && offContent.has(DataComponents.FOOD))
        {
            if (!level.isClientSide())
            {
                // Split stack: only modify one bowl
                ItemStack target = offhand;
                if (offhand.getCount() > 1) { offhand.shrink(1); target = offhand.copy(); target.setCount(1); }

                int cl = target.getOrDefault(DataComponentTypes.CHOP_LEFT.get(), 0);
                int maxBites = target.getOrDefault(DataComponentTypes.MAX_BITES.get(), 3);
                if (cl == 0) cl = maxBites;
                int newCl = cl - 1;
                if (newCl <= 0)
                {
                    ContainerComponent.setContent(target, ItemStack.EMPTY);
                    target.remove(DataComponentTypes.CHOP_LEFT.get());
                    target.remove(DataComponentTypes.MAX_BITES.get());
                }
                else
                {
                    target.set(DataComponentTypes.CHOP_LEFT.get(), newCl);
                    if (!target.has(DataComponentTypes.MAX_BITES.get())) target.set(DataComponentTypes.MAX_BITES.get(), maxBites);
                    BowlFoodHelper.applyFood(target, offContent);
                }
                int storage = target.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent cc ? cc.containerStorage() : 1;
                int bites = Math.max(1, maxBites / storage);
                chopsticks.set(DataComponentTypes.CHOPSTICKS_FOOD.get(), new ChopsticksFood(offContent.copy(), bites));
                BowlFoodHelper.applyFoodChopsticks(chopsticks, offContent, bites);

                // Return modified bowl to inventory if split
                if (target != offhand)
                {
                    if (!player.getInventory().add(target)) player.drop(target, false);
                }
            }
            player.playSound(SoundEvents.ITEM_PICKUP, 0.8f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return stack.has(DataComponents.CONSUMABLE) ? 32 : 0;
    }

   @Override
   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
   {
       // Let vanilla apply FOOD nutrition + CONSUMABLE effects + USE_REMAINDER
       if (entity instanceof Player player)
       {
           player.playSound(SoundEvents.PLAYER_BURP, 0.8f, 1.0f);
           player.getCooldowns().addCooldown(stack, 8);
       }
       return super.finishUsingItem(stack, level, entity);
   }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag)
    {
        ChopsticksFood food = stack.getOrDefault(DataComponentTypes.CHOPSTICKS_FOOD.get(), ChopsticksFood.EMPTY);
        if (!food.food().isEmpty()) builder.accept(Component.translatable("tooltip.ashihara.bowl_content", food.food().getHoverName()));
    }
}
