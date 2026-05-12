package kogasastudio.ashihara.item.food;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;
import java.util.function.Consumer;

public class DirtBallDon extends FoodBowled
{
    private static final Consumable DIRT_BALL_DON = Consumables.defaultFood().onConsume
    (
        new ApplyStatusEffectsConsumeEffect
        (
            List.of
            (
                new MobEffectInstance(MobEffects.HASTE, 1000, 2),
                new MobEffectInstance(MobEffects.REGENERATION, 1500, 1),
                new MobEffectInstance(MobEffects.LUCK, 1000, 3),
                new MobEffectInstance(MobEffects.NAUSEA, 100, 2)
            )
        )
    ).build();

    public DirtBallDon(Properties properties)
    {
        super(properties.rarity(Rarity.EPIC).food(new FoodProperties.Builder().nutrition(16).build(), DIRT_BALL_DON));
    }

    public DirtBallDon()
    {
        this(new Properties());
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
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag)
    {
        tooltip.accept(PROJECTILE);
        tooltip.accept(PROJECTILE_1);
        super.appendHoverText(itemStack, context, display, tooltip, tooltipFlag);
    }
}
