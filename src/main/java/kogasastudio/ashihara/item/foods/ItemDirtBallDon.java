package kogasastudio.ashihara.item.foods;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemDirtBallDon extends FoodBowled
{
    public ItemDirtBallDon()
    {
        super
        (
            new Properties().group(ASHIHARA)
            .rarity(Rarity.EPIC)
            .food(new Food.Builder().hunger(16)
            .effect(new EffectInstance(Effects.HASTE, 1000, 2), 1)
            .effect(new EffectInstance(Effects.REGENERATION, 1500, 1), 1)
            .effect(new EffectInstance(Effects.LUCK, 1000, 3), 1)
            .effect(new EffectInstance(Effects.NAUSEA, 100, 2), 1).build())
        );
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add((new TranslationTextComponent("item.ashihara.dirt_ball_don.projectile")));
        tooltip.add((new TranslationTextComponent("item.ashihara.dirt_ball_don.projectile_1")));
    }
}
