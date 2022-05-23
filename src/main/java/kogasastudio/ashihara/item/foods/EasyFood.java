package kogasastudio.ashihara.item.foods;

import net.minecraft.item.Food;
import net.minecraft.item.Item;

import static kogasastudio.ashihara.Ashihara.MATERIALS;

public class EasyFood extends Item
{
    public EasyFood(int hunger)
    {
        super
        (
            new Item.Properties().tab(MATERIALS)
            .food(new Food.Builder().nutrition(hunger).build())
        );
    }
}