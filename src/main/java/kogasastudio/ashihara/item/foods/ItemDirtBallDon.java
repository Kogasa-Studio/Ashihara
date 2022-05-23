package kogasastudio.ashihara.item.foods;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemDirtBallDon extends FoodBowled
{
    public ItemDirtBallDon()
    {
        super
        (
            new Properties().tab(ASHIHARA)
            .rarity(Rarity.EPIC)
            .food(new FoodProperties.Builder().nutrition(16)
            .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1000, 2), 1)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 1500, 1), 1)
            .effect(new MobEffectInstance(MobEffects.LUCK, 1000, 3), 1)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 100, 2), 1).build())
        );
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<TextComponent> tooltip, TooltipFlag flagIn)
    {
        tooltip.add((new TranslatableComponent("item.ashihara.dirt_ball_don.projectile")));
        tooltip.add((new TranslatableComponent("item.ashihara.dirt_ball_don.projectile_1")));
    }
}
