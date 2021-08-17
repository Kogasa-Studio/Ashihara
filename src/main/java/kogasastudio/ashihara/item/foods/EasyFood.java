package kogasastudio.ashihara.item.foods;

import net.minecraft.item.Food;
import net.minecraft.item.Item;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class EasyFood extends Item
{
    public EasyFood(int hunger)
    {
        super
        (
            new Item.Properties().group(ASHIHARA)
            .food(new Food.Builder().hunger(hunger).build())
        );
    }
}