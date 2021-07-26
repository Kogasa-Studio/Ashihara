package kogasastudio.ashihara.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemCookedRice extends Item
{
    public ItemCookedRice()
    {
        super
        (
            new Properties().group(ASHIHARA)
            .food(new Food.Builder().hunger(5).build())
        );
    }
}
