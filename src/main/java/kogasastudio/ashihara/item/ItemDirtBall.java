package kogasastudio.ashihara.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemDirtBall extends Item
{
    public ItemDirtBall()
    {
        super
        (
            new Properties().group(ASHIHARA)
            .food(new Food.Builder().hunger(2).effect(new EffectInstance(Effects.NAUSEA, 400, 2), 1.0F).build())
        );
    }
}
