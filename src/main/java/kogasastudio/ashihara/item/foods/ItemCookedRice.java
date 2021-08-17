package kogasastudio.ashihara.item.foods;

import net.minecraft.item.Food;
import net.minecraft.item.Item;

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
