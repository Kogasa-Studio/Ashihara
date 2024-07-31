package kogasastudio.ashihara.item.foods;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemDirtBallDon extends FoodBowled
{
    public ItemDirtBallDon()
    {
        super
                (
                        new Properties()
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

    // todo 推荐只创建一次这种不变的 TranslatableComponent 对象
    private static final Component PROJECTILE = Component.translatable("item.ashihara.dirt_ball_don.projectile");
    private static final Component PROJECTILE_1 = Component.translatable("item.ashihara.dirt_ball_don.projectile_1");

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(PROJECTILE);
        tooltip.add(PROJECTILE_1);
    }
}
